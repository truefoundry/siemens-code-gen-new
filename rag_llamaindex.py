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

def create_index(config: dict):
    """
    Create vector index from documents
    
    Args:
        config: Dictionary containing indexing settings
    Returns:
        VectorStoreIndex: Created index
    """
    logger = logging.getLogger(__name__)
    
    # Setup embedding model
    Settings.embed_model = OpenAIEmbedding(
        model_name=config["llm"]["models"]["embedding"]["name"],
        api_key=os.getenv("TFY_API_KEY_EO"),
        api_base=os.getenv("TFY_BASE_URL")
    )
    
    # Load and index documents
    documents = SimpleDirectoryReader(config["paths"]["data"]["extracted_texts"]).load_data()
    index = VectorStoreIndex.from_documents(documents)
    
    return index

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
    
    # Setup LLM with system prompt
    Settings.llm = OpenAILike(
        model=config["llm"]["models"]["main"]["name"],
        #api_key=os.getenv("OPENAI_API_KEY"),
        api_key=os.getenv("TFY_API_KEY_INTERNAL"),
        api_base=os.getenv("TFY_BASE_URL"),
        is_chat_model=True,
        system_prompt=load_prompt(config["system"]["prompt_paths"]["system_default"]),
        temperature=config["system"]["model_config"]["temperature"],
        top_p=config["system"]["model_config"]["top_p"],
        presence_penalty=config["system"]["model_config"]["presence_penalty"],
        frequency_penalty=config["system"]["model_config"]["frequency_penalty"],
        max_tokens=config["system"]["model_config"]["max_tokens"],
        context_window=16384,
    )
    
    # Load and format prompt using the dynamic input path
    base_prompt = load_prompt(
        config["system"]["prompt_paths"]["base_case"],
        config["system"]["prompt_paths"]["few_shot_case"],
        config["paths"]["input_prompt_path"],  # Use dynamic input path
    )
    formatted_prompt = format_java_prompt(base_prompt)
    
    # Query and get response
    query_engine = index.as_query_engine(
        similarity_top_k=config["llm"]["rag"]["similarity_top_k"],
        verbose=True
    )
    response = query_engine.query(formatted_prompt)
    
    
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
    index = create_index(config)
    response, source_texts, source_names = generate_response(config, index)
    
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
