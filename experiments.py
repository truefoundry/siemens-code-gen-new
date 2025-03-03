from rag_llamaindex import create_index, generate_response
from llama_index.core import VectorStoreIndex
from prompt_inference import run_prompt_inference
from evals import evaluate_code
import os
import glob 
import yaml
import logging
from pathlib import Path
from datetime import datetime
from typing import Dict, Tuple
from utils import (
    load_config, 
    load_prompt, 
    get_test_case_files, 
    format_java_prompt,
    validate_file_size,
    setup_logging
)

def setup_experiment_directories(config: dict):
    """Create all necessary directories for experiments"""
    directories = [
        Path(config["paths"]["data"]["root"]),
        Path(config["paths"]["data"]["rag_output"]),
        Path(config["paths"]["data"]["prompt_output"]),
        Path(config["paths"]["data"]["ground_truth"]),
        Path(config["logging"]["file"]).parent,
        Path(config["paths"]["data"]["root"]) / "experiment_results"
    ]
    
    for directory in directories:
        directory.mkdir(parents=True, exist_ok=True)

def setup_experiment_logging(config: dict) -> logging.Logger:
    """Setup experiment-specific logging"""
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    log_dir = Path(config["logging"]["file"]).parent
    log_file = log_dir / f"experiment_{timestamp}.log"
    
    # Ensure log directory exists
    log_dir.mkdir(parents=True, exist_ok=True)
    
    logging.basicConfig(
        level=config["logging"]["level"],
        format=config["logging"]["format"],
        filename=log_file,
        filemode='w'
    )
    return logging.getLogger(__name__)

def run_experiment(config: dict, index: VectorStoreIndex, test_case_name: str, test_case_path: str) -> tuple:
    """
    Run a single experiment comparing prompt-based and RAG-based code generation
    
    Args:
        config: Configuration dictionary
        index: VectorStoreIndex for RAG-based generation
        test_case_name: Name of the test case
        test_case_path: Path to test case file
    
    Returns:
        tuple: (prompt_results, rag_results, prompt_code, rag_code)
    """
    logger = logging.getLogger(__name__)
    logger.info(f"Starting experiment for test case: {test_case_name}")
    
    try:
        # Setup paths for this experiment
        rag_output = Path(config["paths"]["data"]["rag_output"]) / f"{test_case_name}_rag.java"
        prompt_output = Path(config["paths"]["data"]["prompt_output"]) / f"{test_case_name}_prompt.java"
        ground_truth = Path(config["paths"]["data"]["ground_truth"]) / f"{test_case_name}.java"
        
        # Create output directories
        rag_output.parent.mkdir(parents=True, exist_ok=True)
        prompt_output.parent.mkdir(parents=True, exist_ok=True)
        
        # Run both approaches
        prompt_code, prompt_results = run_prompt_inference(config)
        rag_code, rag_source_texts, rag_source_names = generate_response(config, index)
        
        # Evaluate results
        prompt_eval = evaluate_code(str(prompt_output), str(ground_truth))
        rag_eval = evaluate_code(str(rag_output), str(ground_truth))
        
        logger.info(f"Experiment completed for {test_case_name}")
        return prompt_eval, rag_eval, prompt_code, rag_code
        
    except Exception as e:
        logger.error(f"Error in experiment for {test_case_name}: {str(e)}")
        raise

def run_experiments(config: dict) -> Tuple[Dict, Dict]:
    """
    Run experiments for multiple test cases
    
    Args:
        config: Configuration dictionary
    
    Returns:
        Tuple[Dict, Dict]: Results for prompt-based and RAG-based approaches
    """
    logger = setup_experiment_logging(config)
    logger.info("Starting experiment suite")
    
    prompt_results = {}
    rag_results = {}
    
    try:
        # Create index once for all experiments
        index = create_index(config)
        
        # Get test cases
        test_cases = get_test_case_files(
            config["paths"]["data"]["test_cases"],
            config["validation"]["supported_file_types"]
        )
        
        # Filter already processed test cases
        processed_files = set()
        for output_dir in [config["paths"]["data"]["rag_output"], 
                         config["paths"]["data"]["prompt_output"]]:
            processed_files.update(Path(output_dir).glob("*.java"))
        
        test_cases = {
            name: path for name, path in test_cases.items()
            if not any(name in str(f) for f in processed_files)
        }
        
        # Run experiments for each test case
        for test_case_name, test_case_path in test_cases.items():
            if validate_file_size(test_case_path, config["validation"]["max_test_case_size"]):
                try:
                    prompt_eval, rag_eval, prompt_code, rag_code = run_experiment(
                        config, index, test_case_name, test_case_path
                    )
                    prompt_results[test_case_name] = prompt_eval
                    rag_results[test_case_name] = rag_eval
                    
                except Exception as e:
                    logger.error(f"Failed experiment for {test_case_name}: {str(e)}")
                    prompt_results[test_case_name] = {"error": str(e)}
                    rag_results[test_case_name] = {"error": str(e)}
            else:
                logger.warning(f"Skipping {test_case_name} - exceeds size limit")
                
        return prompt_results, rag_results
        
    except Exception as e:
        logger.error(f"Error in experiment suite: {str(e)}")
        raise

def save_experiment_results(prompt_results: Dict, rag_results: Dict, config: dict):
    """Save experiment results to file"""
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    results_dir = Path(config["paths"]["data"]["root"]) / "experiment_results"
    results_dir.mkdir(parents=True, exist_ok=True)
    
    results = {
        "timestamp": timestamp,
        "prompt_results": prompt_results,
        "rag_results": rag_results,
        "config": config
    }
    
    output_file = results_dir / f"results_{timestamp}.yaml"
    with open(output_file, "w") as f:
        yaml.dump(results, f)

if __name__ == "__main__":
    try:
        # Load configuration and setup directories
        config = load_config()
        setup_experiment_directories(config)
        
        # Setup logging
        logger = setup_experiment_logging(config)
        logger.info("Starting test generation process")
        
        # Run experiments
        prompt_results, rag_results = run_experiments(config)
        
        # Save results
        save_experiment_results(prompt_results, rag_results, config)
        
        # Print summary
        logger.info("\nExperiment Results Summary:")
        for test_case in prompt_results.keys():
            logger.info(f"\nTest Case: {test_case}")
            logger.info(f"Prompt-based results: {prompt_results[test_case]}")
            logger.info(f"RAG-based results: {rag_results[test_case]}")
            
    except Exception as e:
        logging.error(f"Error running experiments: {str(e)}")
        print(f"Error running experiments: {str(e)}")
