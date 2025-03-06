import yaml
import glob
import os
import logging
from pathlib import Path
from typing import Dict, Optional
from functools import lru_cache
import time

def setup_logging(config: dict) -> logging.Logger:
    """
    Setup logging configuration with directory creation
    
    Args:
        config: Configuration dictionary containing logging settings
    Returns:
        logging.Logger: Configured logger instance
    """
    # Create logs directory if it doesn't exist
    log_path = Path(config["logging"]["file"])
    log_path.parent.mkdir(parents=True, exist_ok=True)
    
    logging.basicConfig(
        level=config["logging"]["level"],
        format=config["logging"]["format"],
        filename=log_path,
        filemode='a'
    )
    return logging.getLogger(__name__)

@lru_cache(maxsize=1)
def load_config(config_path: str = "config/config.yaml") -> Dict:
    """
    Load and cache configuration from YAML file
    
    Args:
        config_path: Path to configuration file
        
    Returns:
        Dict: Configuration dictionary
    """
    start_time = time.time()
    try:
        with open(config_path, "r") as f:
            config = yaml.safe_load(f)
            # Setup logging immediately after loading config
            setup_logging(config)
            logging.getLogger(__name__).info(f"Config loaded in {time.time() - start_time:.2f} seconds")
            return config
    except Exception as e:
        print(f"Error loading config from {config_path}: {str(e)}")
        raise

@lru_cache(maxsize=50)
def load_file_content(file_path: str) -> str:
    """
    Load and cache file content
    
    Args:
        file_path: Path to file
        
    Returns:
        str: File content
    """
    with open(file_path, "r") as f:
        return f.read()

def load_prompt(prompt_path: str, few_shot_path: Optional[str] = None, input_path: Optional[str] = None) -> str:
    """
    Load prompt from file(s).
    
    Args:
        prompt_path: Path to the main prompt file
        input_path: Optional path to additional input file
    
    Returns:
        str: Combined prompt text
    """
    start_time = time.time()
    logger = logging.getLogger(__name__)
    
    try:
        # Use cached file loading
        prompt = load_file_content(prompt_path)
            
        if input_path:
            input_text = load_file_content(input_path)
            prompt += "\n" + input_text
        
        if few_shot_path:
            few_shot_text = load_file_content(few_shot_path)
            prompt += "\n" + few_shot_text
        
        # Add standard output format
        prompt += "\n\n```Output: java\n\n```"
        
        logger.debug(f"Prompt loaded in {time.time() - start_time:.2f} seconds")
        return prompt
        
    except Exception as e:
        logger.error(f"Error loading prompt from {prompt_path}: {str(e)}")
        raise

@lru_cache(maxsize=5)
def get_test_case_files(base_path: str, file_types_tuple: tuple = None) -> Dict[str, str]:
    """
    Get all test case file paths recursively from directory.
    
    Args:
        base_path: Base directory to search
        file_types_tuple: Tuple of supported file extensions (from config)
    
    Returns:
        Dict[str, str]: Dictionary mapping test case names to file paths
    """
    if file_types_tuple is None:
        file_types = [".java", ".txt"]
    else:
        file_types = list(file_types_tuple)
        
    test_cases = {}
    base_path = Path(base_path)
    
    try:
        for file_type in file_types:
            files = list(base_path.rglob(f"*{file_type}"))
            for file in files:
                test_cases[file.stem] = str(file)
                
        return test_cases
        
    except Exception as e:
        logging.error(f"Error getting test case files from {base_path}: {str(e)}")
        raise

def format_java_prompt(base_prompt: str) -> str:
    """
    Format prompt for Java test case generation.
    
    Args:
        base_prompt: Base prompt text to format
        
    Returns:
        str: Formatted prompt for Java code generation
    """
    return f"""Based on the following context, generate a complete Java test case. Focus on using the custom framework:

    {base_prompt}

    Generate ONLY the Java code without any explanations or markdown formatting. 
    The response should start directly with the Java code and follow these guidelines:
    - Include all necessary imports
    - Follow Java best practices and naming conventions
    - Include proper error handling
    - Add clear documentation and comments
    """

def validate_file_size(file_path: str, max_size: int) -> bool:
    """
    Validate if file size is within limits.
    
    Args:
        file_path: Path to file to check
        max_size: Maximum allowed size in bytes
        
    Returns:
        bool: True if file size is valid
    """
    try:
        return os.path.getsize(file_path) <= max_size
    except Exception as e:
        logging.error(f"Error checking file size for {file_path}: {str(e)}")
        return False

if __name__ == "__main__":
    # Load configuration and setup logging
    start_time = time.time()
    config = load_config()
    logger = logging.getLogger(__name__)
    logger.info(f"Config loaded in {time.time() - start_time:.2f} seconds")
    
    # Get test cases with validation
    test_case_start_time = time.time()
    # Convert list to tuple for caching
    file_types_tuple = tuple(config["validation"]["supported_file_types"])
    test_cases = get_test_case_files(
        config["paths"]["data"]["test_cases"],
        file_types_tuple
    )
    logger.info(f"Test cases loaded in {time.time() - test_case_start_time:.2f} seconds")
    
    logger.info(f"Found {len(test_cases)} test cases")
    for name, path in test_cases.items():
        if validate_file_size(path, config["validation"]["max_test_case_size"]):
            logger.info(f"Valid test case: {name} -> {path}")
        else:
            logger.warning(f"Invalid test case (too large): {name} -> {path}")

