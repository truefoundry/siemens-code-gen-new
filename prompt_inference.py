from langchain.schema import HumanMessage, SystemMessage
from langchain_community.chat_models import ChatOpenAI
from dotenv import load_dotenv
import os
import logging
from pathlib import Path
from evals import evaluate_code
from utils import load_config, load_prompt, format_java_prompt, validate_file_size, setup_logging

load_dotenv()

def create_llm_client(config: dict) -> ChatOpenAI:
    """
    Create and configure the LLM client using config parameters.
    
    Args:
        config: Dictionary containing LLM configuration
        
    Returns:
        ChatOpenAI: Configured LLM client
    """
    return ChatOpenAI(
        model=config["llm"]["models"]["main"]["name"],
        temperature=config["system"]["model_config"]["temperature"],
        max_tokens=config["system"]["model_config"]["max_tokens"],
        model_kwargs={
            "top_p": config["system"]["model_config"]["top_p"],
            "presence_penalty": config["system"]["model_config"]["presence_penalty"],
            "frequency_penalty": config["system"]["model_config"]["frequency_penalty"],
        },
        streaming=True,
        api_key=os.getenv("TFY_API_KEY_INTERNAL"),
        base_url=os.getenv("TFY_BASE_URL"),
        max_retries=config["system"]["model_config"]["max_retries"]
    )

def get_messages(prompt: str, config: dict) -> list:
    """
    Create the message list for the LLM.
    
    Args:
        prompt: The prepared prompt text
        config: Configuration dictionary
    
    Returns:
        list: List of messages for the LLM
    """
    system_prompt = load_prompt(config["system"]["prompt_paths"]["system_default"])
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
    
    try:
        generated_code = llm.invoke(messages).content
        
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
    
    try:
        # Load and validate prompts
        base_prompt = load_prompt(
            config["system"]["prompt_paths"]["base_case"],
            config["system"]["prompt_paths"]["few_shot_case"],
            config["paths"]["prompts"]["input"], 
        )
        
        # Setup LLM and generate code
        llm = create_llm_client(config)
        messages = get_messages(base_prompt, config)
        
        # Ensure output directory exists
        output_path = Path(config["paths"]["data"]["prompt_output"])
        output_path.parent.mkdir(parents=True, exist_ok=True)
        
        generated_code = generate_code(llm, messages, str(output_path), config)
        
        # # Ensure ground truth directory exists
        # ground_truth_path = Path(config["paths"]["data"]["ground_truth"])
        # if not ground_truth_path.exists():
        #     logger.warning(f"Ground truth directory does not exist: {ground_truth_path}")
        #     ground_truth_path.mkdir(parents=True, exist_ok=True)
            
        # results = evaluate_code(output_path, ground_truth_path)
        
        logger.info("Code generation completed successfully")
        return generated_code
        
    except Exception as e:
        logger.error(f"Error in prompt inference pipeline: {str(e)}")
        raise

if __name__ == "__main__":
    try:
        # Load configuration and setup logging
        config = load_config()
        logger = logging.getLogger(__name__)
        
        generated_code = run_prompt_inference(config)
        output_path = Path(config["paths"]["data"]["prompt_output"]) 
        ground_truth_path = Path(config["paths"]["data"]["ground_truth"])
        evaluate_code(output_path, ground_truth_path)
        logger.info("Generation Results: %s", generated_code)
        print("Generation Results:", generated_code)
    except Exception as e:
        logging.error(f"Error running prompt inference: {str(e)}")
        print(f"Error running prompt inference: {str(e)}")