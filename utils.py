import yaml
import glob
import os
from typing import Tuple, List, Dict

def load_config(config_path: str = "config/config.yaml") -> Dict:
    """Load configuration from YAML file"""
    with open(config_path, "r") as f:
        return yaml.safe_load(f)

def load_prompt(base_prompt_path: str, input_prompt_path: str) -> str:
    """
    Load the base prompt and concatenate with input prompt.
    
    Args:
        base_prompt_path: Path to the main prompt file
        input_prompt_path: Path to the input prompt file
    
    Returns:
        str: Combined prompt text
    """
    with open(base_prompt_path, "r") as f:
        prompt = f.read()
    with open(input_prompt_path, "r") as f:
        input_prompt = f.read()
    prompt += input_prompt
    prompt += "\n\n```Output: java\n\n```"
    return prompt

def get_test_case_files(directory: str) -> Dict[str, str]:
    """Get all test case file paths recursively from directory and return as name:path dict"""
    test_case_file_paths = glob.glob(directory + "/**/*.txt", recursive=True)
    return {os.path.basename(file).split(".")[0]: file for file in test_case_file_paths}

def format_java_prompt(base_prompt: str) -> str:
    """Format prompt for Java test case generation"""
    return f"""Based on the following context, generate a complete Java test case:

    {base_prompt}

    Generate ONLY the Java code without any explanations or markdown formatting. The response should start directly with the Java code:
    """ 

if __name__ == "__main__":
    config = load_config()
    test_case_files = get_test_case_files(config["paths"]["test_case_files"])
    print(test_case_files)

