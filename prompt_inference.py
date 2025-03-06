from langchain.schema import HumanMessage, SystemMessage
from langchain_community.chat_models import ChatOpenAI
from dotenv import load_dotenv
import os
import logging
from pathlib import Path
from evals import evaluate_code
from utils import load_config, load_prompt, format_java_prompt, validate_file_size, setup_logging
from functools import lru_cache
import time

load_dotenv()

@lru_cache(maxsize=1)
def create_llm_client(model, temperature, max_tokens, top_p, presence_penalty, frequency_penalty, max_retries) -> ChatOpenAI:
    """
    Create and cache the LLM client using config parameters.
    
    Args:
        model: Model name
        temperature: Temperature parameter
        max_tokens: Maximum tokens
        top_p: Top p parameter
        presence_penalty: Presence penalty
        frequency_penalty: Frequency penalty
        max_retries: Maximum retries
        
    Returns:
        ChatOpenAI: Configured LLM client
    """
    return ChatOpenAI(
        model=model,
        temperature=temperature,
        max_tokens=max_tokens,
        model_kwargs={
            "top_p": top_p,
            "presence_penalty": presence_penalty,
            "frequency_penalty": frequency_penalty,
        },
        streaming=True,
        api_key=os.getenv("TFY_API_KEY_INTERNAL"),
        base_url=os.getenv("TFY_BASE_URL"),
        max_retries=max_retries
    )

def get_llm_from_config(config: dict) -> ChatOpenAI:
    """
    Get LLM client from config, using cached function.
    
    Args:
        config: Dictionary containing LLM configuration
        
    Returns:
        ChatOpenAI: Configured LLM client
    """
    return create_llm_client(
        config["llm"]["models"]["main"]["name"],
        config["system"]["model_config"]["temperature"],
        config["system"]["model_config"]["max_tokens"],
        config["system"]["model_config"]["top_p"],
        config["system"]["model_config"]["presence_penalty"],
        config["system"]["model_config"]["frequency_penalty"],
        config["system"]["model_config"]["max_retries"]
    )

@lru_cache(maxsize=10)
def get_system_prompt(system_prompt_path: str) -> str:
    """
    Load and cache system prompt.
    
    Args:
        system_prompt_path: Path to system prompt
        
    Returns:
        str: System prompt content
    """
    return load_prompt(system_prompt_path)

def get_messages(prompt: str, config: dict) -> list:
    """
    Create the message list for the LLM.
    
    Args:
        prompt: The prepared prompt text
        config: Configuration dictionary
    
    Returns:
        list: List of messages for the LLM
    """
    system_prompt = get_system_prompt(config["system"]["prompt_paths"]["system_default"])
    return [
        SystemMessage(content=system_prompt),
        HumanMessage(content=prompt),
    ]

def generate_code(llm: ChatOpenAI, messages: list, output_path: str, config: dict) -> str:
    """
    Generate code using LLM.
    
    Args:
        llm: LLM client
        messages: List of messages for the LLM
        output_path: Path to save generated code
        config: Configuration dictionary
        
    Returns:
        str: Generated code content
    """
    logger = logging.getLogger(__name__)
    start_time = time.time()
    
    try:
        # Generate code
        generated_code = llm.invoke(messages).content
        logger.info(f"Code generation completed in {time.time() - start_time:.2f} seconds")
        
        # Ensure output directory exists
        output_dir = Path(output_path).parent
        output_dir.mkdir(parents=True, exist_ok=True)
        
        # Validate output size before saving
        if len(generated_code.encode('utf-8')) <= config["validation"]["max_test_case_size"]:
            with open(output_path, "w") as file:
                file.write(generated_code)
            logger.info(f"Successfully generated and saved code to {output_path}")
        else:
            logger.error(f"Generated code exceeds maximum size limit")
            raise ValueError("Generated code exceeds maximum size limit")
            
        return generated_code
        
    except Exception as e:
        logger.error(f"Error generating code: {str(e)}")
        raise

def run_prompt_inference(config: dict) -> str:
    """
    Run the prompt-based code generation pipeline.
    
    Args:
        config: Configuration dictionary
        
    Returns:
        tuple: (generated_code, results)
    """
    logger = logging.getLogger(__name__)
    logger.info("Starting prompt-based code generation")
    start_time = time.time()
    
    try:
        # Load and validate prompts
        prompt_start_time = time.time()
        base_prompt = load_prompt(
            config["system"]["prompt_paths"]["base_case"],
            config["system"]["prompt_paths"]["few_shot_case"],
            config["paths"]["input_prompt_path"],
        )
        logger.info(f"Prompt loading completed in {time.time() - prompt_start_time:.2f} seconds")
        
        # Setup LLM and generate code
        llm_start_time = time.time()
        llm = get_llm_from_config(config)
        messages = get_messages(base_prompt, config)
        logger.info(f"LLM setup completed in {time.time() - llm_start_time:.2f} seconds")
        
        # Use the dynamically set output path
        output_path = Path(config["paths"]["output_file_prompt"])
        output_path.parent.mkdir(parents=True, exist_ok=True)
        
        # Generate code
        code_start_time = time.time()
        generated_code = generate_code(llm, messages, str(output_path), config)
        logger.info(f"Code generation and saving completed in {time.time() - code_start_time:.2f} seconds")
        
        logger.info(f"Total prompt inference time: {time.time() - start_time:.2f} seconds")
        return generated_code
        
    except Exception as e:
        logger.error(f"Error in prompt inference pipeline: {str(e)}")
        raise

if __name__ == "__main__":
    try:
        # Load configuration and setup logging
        config = load_config()
        logger = logging.getLogger(__name__)
        
        start_time = time.time()
        generated_code = run_prompt_inference(config)
        logger.info(f"Total prompt inference time: {time.time() - start_time:.2f} seconds")
        
        output_path = Path(config["paths"]["output_file_prompt"]) 
        ground_truth_path = Path(config["paths"]["data"]["ground_truth"])
        evaluate_code(output_path, ground_truth_path)
        logger.info("Generation Results: %s", generated_code)
        print("Generation Results:", generated_code)
    except Exception as e:
        logging.error(f"Error running prompt inference: {str(e)}")
        print(f"Error running prompt inference: {str(e)}")