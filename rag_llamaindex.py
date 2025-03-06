from llama_index.core import VectorStoreIndex, SimpleDirectoryReader
from llama_index.core.settings import Settings
#from llama_index.llms.openai import OpenAI
from llama_index.llms.openai_like import OpenAILike
from llama_index.core.llms import ChatMessage, MessageRole
from llama_index.embeddings.openai import OpenAIEmbedding
from evals import evaluate_code
import os
import logging
from utils import load_config, load_prompt, format_java_prompt
from dotenv import load_dotenv
from pathlib import Path
from functools import lru_cache
import time

load_dotenv()

def setup_logging(config: dict):
    """Setup logging configuration"""
    logging.basicConfig(
        level=config["logging"]["level"],
        format=config["logging"]["format"],
        filename=config["logging"]["file"],
        filemode='a'
    )
    return logging.getLogger(__name__)

# Cache the embedding model setup to avoid recreating it for each request
@lru_cache(maxsize=1)
def get_embedding_model(model_name, api_key, api_base):
    """Get cached embedding model to avoid recreating it for each request"""
    return OpenAIEmbedding(
        model_name=model_name,
        api_key=api_key,
        api_base=api_base
    )

# Cache document loading to avoid reloading for each index creation
@lru_cache(maxsize=1)
def load_documents(directory_path):
    """Load and cache documents from a directory"""
    return SimpleDirectoryReader(directory_path).load_data()

def create_index(config: dict):
    """
    Create vector index from documents
    
    Args:
        config: Dictionary containing indexing settings
    Returns:
        VectorStoreIndex: Created index
    """
    logger = logging.getLogger(__name__)
    start_time = time.time()
    
    # Setup embedding model using cached function
    Settings.embed_model = get_embedding_model(
        config["llm"]["models"]["embedding"]["name"],
        os.getenv("TFY_API_KEY_EO"),
        os.getenv("TFY_BASE_URL")
    )
    
    # Load and index documents using cached function
    documents = load_documents(config["paths"]["data"]["extracted_texts"])
    logger.info(f"Documents loaded in {time.time() - start_time:.2f} seconds")
    
    # Create index
    index_start_time = time.time()
    index = VectorStoreIndex.from_documents(documents)
    logger.info(f"Index created in {time.time() - index_start_time:.2f} seconds")
    
    return index

# Cache LLM creation to avoid recreating it for each request
@lru_cache(maxsize=1)
def get_llm(model, api_key, api_base, system_prompt, temperature, top_p, presence_penalty, frequency_penalty, max_tokens):
    """Get cached LLM to avoid recreating it for each request"""
    return OpenAILike(
        model=model,
        api_key=api_key,
        api_base=api_base,
        is_chat_model=True,
        system_prompt=system_prompt,
        temperature=temperature,
        top_p=top_p,
        presence_penalty=presence_penalty,
        frequency_penalty=frequency_penalty,
        max_tokens=max_tokens,
        context_window=16384,
    )

def generate_response(config: dict, index: VectorStoreIndex):
    """
    Generate response using RAG with given index and configuration
    
    Args:
        index: VectorStoreIndex to query from
        config: Dictionary containing generation settings
    Returns:
        tuple: (response, source_texts, source_names)
    """
    logger = logging.getLogger(__name__)
    start_time = time.time()
    
    # Setup LLM with system prompt using cached function
    system_prompt = load_prompt(config["system"]["prompt_paths"]["system_default"])
    Settings.llm = get_llm(
        config["llm"]["models"]["main"]["name"],
        os.getenv("TFY_API_KEY_INTERNAL"),
        os.getenv("TFY_BASE_URL"),
        system_prompt,
        config["system"]["model_config"]["temperature"],
        config["system"]["model_config"]["top_p"],
        config["system"]["model_config"]["presence_penalty"],
        config["system"]["model_config"]["frequency_penalty"],
        config["system"]["model_config"]["max_tokens"]
    )
    logger.info(f"LLM setup completed in {time.time() - start_time:.2f} seconds")
    
    # Load and format prompt using the dynamic input path
    prompt_start_time = time.time()
    base_prompt = load_prompt(
        config["system"]["prompt_paths"]["base_case"],
        config["system"]["prompt_paths"]["few_shot_case"],
        config["paths"]["input_prompt_path"],  # Use dynamic input path
    )
    formatted_prompt = format_java_prompt(base_prompt)
    logger.info(f"Prompt formatting completed in {time.time() - prompt_start_time:.2f} seconds")
    
    # Create query engine with optimized settings
    query_start_time = time.time()
    query_engine = index.as_query_engine(
        similarity_top_k=config["llm"]["rag"]["similarity_top_k"],
        verbose=True
    )
    
    # Query and get response
    response = query_engine.query(formatted_prompt)
    logger.info(f"Query execution completed in {time.time() - query_start_time:.2f} seconds")
    
    # Extract source information
    source_nodes = response.source_nodes
    source_names = [os.path.basename(node.metadata["file_path"]) for node in source_nodes]
    source_texts = [node.node.text for node in source_nodes]
    
    logger.info(f"Response generated with {len(source_texts)} source documents")
    logger.info(f"Source documents: {source_names}")
    
    # Save response using dynamic output path
    output_path = Path(config["paths"]["output_file_rag"])  # Use dynamic output path
    output_path.parent.mkdir(parents=True, exist_ok=True)
    
    with open(output_path, "w") as f:
        f.write(str(response))

    logger.info(f"Total response generation time: {time.time() - start_time:.2f} seconds")
    return response, source_texts, source_names

def run_rag(config: dict):
    """
    Run complete RAG pipeline with given configuration
    
    Args:
        config: Dictionary containing pipeline settings
    Returns:
        tuple: (response, source_texts, source_names)
    """
    logger = setup_logging(config)
    logger.info("Starting RAG pipeline")
    
    index = create_index(config)
    return generate_response(config, index)

if __name__ == "__main__":
    # Load configuration from YAML
    config = load_config()
    
    # Setup logging
    logger = setup_logging(config)
    logger.info("Starting test generation process")
    
    # Run RAG pipeline
    start_time = time.time()
    index = create_index(config)
    logger.info(f"Index creation time: {time.time() - start_time:.2f} seconds")
    
    response_start_time = time.time()
    response, source_texts, source_names = generate_response(config, index)
    logger.info(f"Response generation time: {time.time() - response_start_time:.2f} seconds")
    
    print("response : " , response)


    # Update paths for evaluation
    ground_truth_path = Path(config["paths"]["ground_truth_file"])  # Use dynamic ground truth path
    output_path = Path(config["paths"]["output_file_rag"])  # Use dynamic output path
    
    # Evaluate results
    evaluate_code(ground_truth_path, output_path)
    
    logger.info("Test generation completed")
    
    # Display results
    print("response : " , response)
    print("source_texts : ",source_texts)
    print("source_names : ",source_names)
    
    # Compare with ground truth
    import subprocess
    try:
        result = subprocess.run(
            ['diff', '-u', str(output_path), str(ground_truth_path)],
            capture_output=True,
            text=True
        )
        print("\nDiff between output and ground truth:")
        print(result.stdout if result.stdout else "Files are identical")
        if result.stderr:
            print("Errors:", result.stderr)
    except Exception as e:
        print(f"Error running diff: {e}")
