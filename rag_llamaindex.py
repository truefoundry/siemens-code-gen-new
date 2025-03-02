from llama_index.core import VectorStoreIndex, SimpleDirectoryReader
from llama_index.core.settings import Settings
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
from evals import evaluate_code
import os
from utils import load_config, load_prompt, format_java_prompt
from dotenv import load_dotenv


load_dotenv()

def create_index(config: dict):
    """
    Create vector index from documents
    
    Args:
        config: Dictionary containing indexing settings
    Returns:
        VectorStoreIndex: Created index
    """
    # Setup embedding model
    Settings.embed_model = OpenAIEmbedding(
        model_name=f"openai-main/{config['llm']['embedding_model']}", 
        api_key=os.getenv("TFY_API_KEY"),
        api_base=os.getenv("TFY_BASE_URL")
    )
    
    # Load and index documents
    documents = SimpleDirectoryReader(config["paths"]["data_dir"]).load_data()
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
    # Setup LLM with system prompt
    Settings.llm = OpenAI(
        model=config["llm"]["model"],
        #api_key="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImpJTkY3bXJ2RjA3cWJNUzllelhYeU5GYTBWVSJ9.eyJhdWQiOiI2OTZlNzQ2NS03MjZlLTYxNmMtM2EzOS02MTM4Mzg2NTYxNjEiLCJleHAiOjM2ODc4NDcxODEsImlhdCI6MTcyODI5NTE4MSwiaXNzIjoidHJ1ZWZvdW5kcnkuY29tIiwic3ViIjoiY20xeXViczM4c2l3YzAxcXQ5d2hzNzA3eCIsImp0aSI6Ijg1MjFkZWQxLTExMTQtNGM3YS05ZWJkLTg1NGJjNDUxMDI1ZSIsInVzZXJuYW1lIjoiaW50ZXJuYWx0b2tlbiIsInVzZXJUeXBlIjoic2VydmljZWFjY291bnQiLCJ0ZW5hbnROYW1lIjoiaW50ZXJuYWwiLCJyb2xlcyI6WyJ0ZW5hbnQtbWVtYmVyIl0sImFwcGxpY2F0aW9uSWQiOiI2OTZlNzQ2NS03MjZlLTYxNmMtM2EzOS02MTM4Mzg2NTYxNjEifQ.auxiXDusKgjIBPfaC4VNNaTpFgYBjPFKYPnbcEdzZyF2HmpJ8paqJqWAgMETdj7JmoHTOuiuAQKTAu76JgkzTYU1Kwu6mDH4B6vUuZO6SiWr99Z3thmmnoyvD15Y1E-bCFo_JqSSCxbq-oNKOIEWJ7w3bR3U7jQ-orVAXuR7PkNfFP2-YFBuW-1gYkWizWfFYtAQTQQ8ZBIgC7X9KdNNeWyr0KMxNGXmvYZ-Q4Q9HFgzAIX91DFCO4_3QtB3F4AKWuCQs4V1_Wy9J8gUZAN587TP--CwFqstzo7nlj5pKX6UH4dgwVJ1M6LAaVGouQ_PvzmvRnB9UKUpmluaCS9zIg",
        #api_base="https://llm-gateway.truefoundry.com/api/inference/openai",
        system_prompt=config["system"]["prompt"],
        temperature=config["system"]["temperature"],
        # top_p=config["system"]["top_p"],
        # presence_penalty=config["system"]["presence_penalty"],
        # frequency_penalty=config["system"]["frequency_penalty"],
        # max_tokens=config["system"]["max_tokens"],
    )
    
    # Load and format prompt
    base_prompt = load_prompt(config["paths"]["base_prompt_path"], 
                            config["paths"]["input_prompt_path"])
    formatted_prompt = format_java_prompt(base_prompt)
    
    # Query and get response
    query_engine = index.as_query_engine(
        similarity_top_k=config["llm"]["similarity_top_k"],
        verbose=True
    )
    response = query_engine.query(formatted_prompt)
    
    # Extract source information
    source_nodes = response.source_nodes
    source_names = [os.path.basename(node.metadata["file_path"]) for node in source_nodes]
    source_texts = [node.node.text for node in source_nodes]
    
    print(f"Response generated with {len(source_texts)} source documents")
    print(f"Source documents: {source_names}")
    
    # Save response
    with open(config["paths"]["output_file_rag"], "w") as f:
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
    index = create_index(config)
    return generate_response(index, config)

if __name__ == "__main__":
    # Load configuration from YAML
    config = load_config()
    index = create_index(config)
    response, source_texts, source_names = generate_response(config, index)
    
    evaluate_code(config["paths"]["ground_truth_file"], 
                 config["paths"]["output_file_rag"])

    print(response)
    print(source_texts)
    print(source_names)

    os.system(f"code --diff {config['paths']['output_file_rag']} {config['paths']['ground_truth_file']}")

