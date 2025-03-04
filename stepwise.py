from utils import load_config, load_prompt, setup_logging
from prompt_inference import get_messages, generate_code, create_llm_client
from rag_llamaindex import create_index, generate_response
import os
import glob
import re
from pathlib import Path
import logging
from langchain_openai import ChatOpenAI
from typing import List

from dotenv import load_dotenv

load_dotenv()

class StepwisePredictor:
    """
    A class to handle stepwise code prediction from test cases.
    Implements a chain of thought process to break down and generate code.
    """
    
    def __init__(self, config: dict):
        """
        Initialize the StepwisePredictor with configuration.
        
        Args:
            config (dict): Configuration dictionary containing settings for LLM, 
                          logging, output directories, and prompt templates
        
        Attributes:
            config (dict): Stored configuration dictionary
            llm: Language model client for generating text
            logger: Logging utility for tracking execution
            output_dir (str): Directory to store prediction outputs
            prompts (dict): Dictionary of loaded prompt templates
        """
        self.config = config
        self.llm = create_llm_client(config)
        self.logger = setup_logging(config) if "logging" in config else None
        self.output_dir = config.get("stepwise", {}).get("output_dir", "predictions/stepwise")
        os.makedirs(self.output_dir, exist_ok=True)
        self.prompts = self._load_prompts()
        
    def _load_prompts(self) -> dict:
        """
        Load and process all prompt templates from files or config.
        
        Attempts to load prompts in the following order:
        1. From the config dictionary if available
        2. From the exact filename in the prompts directory
        3. From any file matching the prompt type in the prompts directory
        
        Returns:
            dict: Dictionary mapping prompt types to their template strings
                  with variables processed
        
        Prompt types include:
            - planning: For generating step plans
            - code_generation: For generating code from plans
            - execution: For simulating code execution
            - refinement: For refining generated code
            - prompt: Base template for all steps
            - few_shot_examples: Examples to guide the model
        """
        # Define prompt types and their corresponding files
        prompt_types = {
            "planning": "planning_steps.txt",
            "code_generation": "code_generation_steps.txt", 
            "execution": "execution_steps.txt",
            "refinement": "refinement_steps.txt",
            "prompt": "base_step.txt",
            "few_shot_examples": "few_shot_step.txt"
        }
        
        prompts_dir = self.config.get("paths", {}).get("prompts", {}).get("dir", "prompts")
        prompts = {}
        
        for prompt_type, file_name in prompt_types.items():
            try:
                if "stepwise" in self.config and prompt_type in self.config["stepwise"]:
                    prompts[prompt_type] = self.config["stepwise"][prompt_type]
                    if self.logger:
                        self.logger.info(f"Loaded {prompt_type} prompt from config")
                else:
                    prompt_path = os.path.join(prompts_dir, file_name)
                    if os.path.exists(prompt_path):
                        with open(prompt_path, 'r') as f:
                            prompts[prompt_type] = f.read()
                        if self.logger:
                            self.logger.info(f"Loaded {prompt_type} prompt from {prompt_path}")
                    else:
                        possible_paths = glob.glob(f"{prompts_dir}/*{prompt_type}*.txt")
                        if possible_paths:
                            with open(possible_paths[0], 'r') as f:
                                prompts[prompt_type] = f.read()
                            if self.logger:
                                self.logger.info(f"Loaded {prompt_type} prompt from {possible_paths[0]}")
                        else:
                            if self.logger:
                                self.logger.warning(f"Prompt file for {prompt_type} not found, using empty prompt")
                            prompts[prompt_type] = ""
            except Exception as e:
                if self.logger:
                    self.logger.error(f"Error loading {prompt_type} prompt: {str(e)}")
                prompts[prompt_type] = ""
        
        # Process variables in prompts
        for prompt_type, prompt_text in prompts.items():
            if prompt_text:
                prompts[prompt_type] = self._process_prompt_variables(prompt_text)
                
        return prompts
    
    def _process_prompt_variables(self, prompt_text: str) -> str:
        """
        Process variables in prompt text using config values.
        
        Replaces variables in the format ${variable.path} with their 
        corresponding values from the config dictionary.
        
        Args:
            prompt_text (str): The prompt template containing variables
            
        Returns:
            str: Processed prompt with variables replaced by their values
        
        Example:
            If config has {"model": {"temperature": 0.7}} and prompt contains
            "${model.temperature}", it will be replaced with "0.7"
        """
        variables = re.findall(r'\${(.*?)}', prompt_text)
        
        for var in variables:
            value = self.config
            for key in var.split('.'):
                if key in value:
                    value = value[key]
                else:
                    value = f"${{{var}}}"
                    break
                    
            if isinstance(value, str):
                prompt_text = prompt_text.replace(f"${{{var}}}", value)
                
        return prompt_text
    
    def parse_steps(self, prompt: str) -> List[str]:
        """
        Parse test case steps from the prompt string.
        
        Extracts structured test steps from a formatted prompt string,
        typically in a table format with step number, action, and expected result.
        
        Args:
            prompt (str): The test case prompt containing steps in tabular format
            
        Returns:
            List[str]: List of formatted step strings, each containing the step number,
                      action description, and expected result
        
        Raises:
            Exception: If parsing fails due to unexpected format
        """
        if self.logger:
            self.logger.info("Parsing test case steps from prompt")
            
        try:
            lines = prompt.split('\n')
            steps = []
            
            for line in lines[2:]:
                if '|' not in line:
                    continue
                    
                parts = [part.strip() for part in line.split('|') if part.strip()]
                if len(parts) >= 3:
                    steps.append({
                        'step': parts[0],
                        'action': parts[1].replace('<br>', '\n'),
                        'expected_result': parts[2].replace('<br>', '\n')
                    })
                    
            formatted_steps = [
                f"Step {step['step']}:\n{step['action']}\nExpected Result: {step['expected_result']}"
                for step in steps
            ]
            
            if self.logger:
                self.logger.info(f"Successfully parsed {len(formatted_steps)} test case steps")
                
            return formatted_steps
            
        except Exception as e:
            if self.logger:
                self.logger.error(f"Error parsing test case steps: {str(e)}")
            raise
    
    def generate_plans(self, steps: List[str]) -> List[str]:
        """
        Generate detailed plans for each test case step.
        
        Creates a plan for each step that breaks down the implementation
        approach into logical substeps.
        
        Args:
            steps (List[str]): List of parsed test case steps
            
        Returns:
            List[str]: List of generated plans corresponding to each step
            
        Side effects:
            Saves each plan to a text file in the output directory
        """
        plans = []
        for i, step in enumerate(steps):
            if self.logger:
                self.logger.info(f"Creating plan for step {i+1}: {step[:50]}...")
                
            plan_text = generate_single_plan(step, i, self.llm, self.prompts, self.config)
            plans.append(plan_text)
            
            plan_output_path = os.path.join(self.output_dir, f"plan_{i+1}.txt")
            with open(plan_output_path, "w") as f:
                f.write(f"Step: {step}\n\nPlan:\n{plan_text}")
                
        return plans
    
    def generate_code(self, steps: List[str], plans: List[str]) -> str:
        """
        Generate and refine code from steps and plans.
        
        Implements a multi-stage code generation process:
        1. Generate code for each substep within each plan
        2. Combine substep code into coherent step implementations
        3. Refine the step implementations for correctness
        4. Combine steps into a complete test case
        5. Optimize the final code
        
        Args:
            steps (List[str]): List of parsed test case steps
            plans (List[str]): List of generated plans for each step
            
        Returns:
            str: The final optimized code implementation
            
        Side effects:
            Saves intermediate code artifacts to the output directory
        """
        # Generate code for each substep
        substeps_code = []
        for i, (step, plan) in enumerate(zip(steps, plans)):
            if self.logger:
                self.logger.info(f"Generating code for step {i+1}: {step[:50]}...")
                
            substeps = parse_plan_into_substeps(plan)
            step_code = generate_code_for_substeps(
                step, plan, substeps, i, self.llm, self.prompts, 
                self.config, self.output_dir, self.logger
            )
            substeps_code.append(step_code)
            
        # Combine and refine code
        step_code = combine_substeps(steps, substeps_code, self.llm, self.prompts, 
                                   self.config, self.output_dir, self.logger)
        refined_code = refine_steps(steps, step_code, self.llm, self.prompts, 
                                  self.config, self.output_dir, self.logger)
        combined_code = combine_into_test_case(steps, refined_code, self.llm, 
                                             self.prompts, self.config, self.output_dir, self.logger)
        final_code = optimize_final_code(combined_code, self.llm, self.prompts, 
                                       self.config, self.output_dir, self.logger)
        
        return final_code
    
    def predict(self) -> str:
        """
        Execute the complete stepwise prediction process.
        
        Orchestrates the end-to-end process of:
        1. Parsing test case steps from the configuration
        2. Generating implementation plans for each step
        3. Generating code based on the plans
        4. Saving the final code to the specified output path
        
        Returns:
            str: The final generated code implementation
            
        Raises:
            ValueError: If test case is not specified in config
            Exception: If any step in the prediction process fails
            
        Side effects:
            Creates output directory if it doesn't exist
            Saves final code to the specified output path
        """
        if self.logger:
            self.logger.info("Starting stepwise prediction process")
            
        try:
            # Get test case from config
            test_case = self.config.get("stepwise", {}).get("test_case", "")
            if not test_case:
                raise ValueError("Test case not specified in config")
            
            # Execute prediction pipeline
            steps = self.parse_steps(test_case)
            plans = self.generate_plans(steps)
            final_code = self.generate_code(steps, plans)
            
            # Save final code
            output_path = self.config.get("stepwise", {}).get(
                "output_path", os.path.join(self.output_dir, "final_code.py")
            )
            os.makedirs(os.path.dirname(output_path), exist_ok=True)
            with open(output_path, "w") as f:
                f.write(final_code)
                
            if self.logger:
                self.logger.info("Stepwise prediction process completed successfully")
                
            return final_code
            
        except Exception as e:
            if self.logger:
                self.logger.error(f"Error in stepwise prediction: {str(e)}")
            raise

# Utility functions for StepwisePredictor

def generate_single_plan(step: str, step_index: int, llm, prompts: dict, config: dict) -> str:
    """
    Generate a detailed implementation plan for a single test case step.
    
    Args:
        step (str): The test case step to plan for
        step_index (int): The index of the step in the sequence
        llm: Language model client for generating text
        prompts (dict): Dictionary of prompt templates
        config (dict): Configuration dictionary
        
    Returns:
        str: A detailed plan breaking down the implementation approach
             into logical substeps
    """
    # Prepare the planning prompt
    planning_prompt = prompts.get("planning", "")
    if not planning_prompt:
        planning_prompt = "Create a detailed plan for implementing the following test step:\n{step}"
    
    # Replace variables in the prompt
    planning_prompt = planning_prompt.replace("{step}", step)
    planning_prompt = planning_prompt.replace("{step_index}", str(step_index + 1))
    
    # Add few-shot examples if available
    few_shot = prompts.get("few_shot_examples", "")
    if few_shot:
        planning_prompt = f"{few_shot}\n\n{planning_prompt}"
    
    # Generate the plan using the language model
    messages = [
        {"role": "system", "content": "You are an expert software developer creating detailed implementation plans."},
        {"role": "user", "content": planning_prompt}
    ]
    
    response = get_messages(llm, messages, config.get("model", {}).get("temperature", 0.2))
    
    return response

def parse_plan_into_substeps(plan: str) -> List[str]:
    """
    Parse a plan into individual substeps for implementation.
    
    Args:
        plan (str): The detailed implementation plan
        
    Returns:
        List[str]: List of individual substeps extracted from the plan
    """
    # Look for numbered steps in the plan
    substeps = []
    
    # Try to find numbered steps (1., 2., etc.)
    numbered_pattern = re.compile(r'(?:^|\n)(\d+\.?\s+[^\n]+)')
    matches = numbered_pattern.findall(plan)
    
    if matches:
        substeps = [match.strip() for match in matches]
    else:
        # Try to find steps marked with "Step X:"
        step_pattern = re.compile(r'(?:^|\n)(?:Step|Substep)\s+\d+:?\s+([^\n]+)')
        matches = step_pattern.findall(plan)
        if matches:
            substeps = [match.strip() for match in matches]
        else:
            # If no structured steps found, split by newlines and filter empty lines
            substeps = [line.strip() for line in plan.split('\n') if line.strip()]
    
    return substeps

def generate_code_for_substeps(
    step: str, 
    plan: str, 
    substeps: List[str], 
    step_index: int, 
    llm, 
    prompts: dict, 
    config: dict, 
    output_dir: str, 
    logger=None
) -> str:
    """
    Generate code for each substep in a plan.
    
    Args:
        step (str): The original test case step
        plan (str): The detailed implementation plan
        substeps (List[str]): List of substeps extracted from the plan
        step_index (int): The index of the step in the sequence
        llm: Language model client for generating text
        prompts (dict): Dictionary of prompt templates
        config (dict): Configuration dictionary
        output_dir (str): Directory to store outputs
        logger: Optional logger for tracking execution
        
    Returns:
        str: Combined code for all substeps
        
    Side effects:
        Saves code for each substep to a file in the output directory
    """
    code_generation_prompt = prompts.get("code_generation", "")
    if not code_generation_prompt:
        code_generation_prompt = "Generate Python code to implement the following step:\n{substep}\n\nContext:\n{step}\n\nPlan:\n{plan}"
    
    all_code = []
    
    for i, substep in enumerate(substeps):
        if logger:
            logger.info(f"Generating code for substep {i+1}/{len(substeps)} of step {step_index+1}")
        
        # Prepare the prompt for this substep
        substep_prompt = code_generation_prompt.replace("{substep}", substep)
        substep_prompt = substep_prompt.replace("{step}", step)
        substep_prompt = substep_prompt.replace("{plan}", plan)
        substep_prompt = substep_prompt.replace("{step_index}", str(step_index + 1))
        substep_prompt = substep_prompt.replace("{substep_index}", str(i + 1))
        
        # Generate code using the language model
        messages = [
            {"role": "system", "content": "You are an expert Python developer. Generate clean, efficient, and well-documented code."},
            {"role": "user", "content": substep_prompt}
        ]
        
        response = get_messages(llm, messages, config.get("model", {}).get("temperature", 0.2))
        
        # Extract code blocks from the response
        code_blocks = re.findall(r'```(?:python)?\s*(.*?)```', response, re.DOTALL)
        
        if code_blocks:
            code = code_blocks[0].strip()
        else:
            # If no code blocks found, use the entire response
            code = response.strip()
        
        all_code.append(code)
        
        # Save the code for this substep
        substep_output_path = os.path.join(output_dir, f"step_{step_index+1}_substep_{i+1}_code.py")
        with open(substep_output_path, "w") as f:
            f.write(f"# Code for Step {step_index+1}, Substep {i+1}:\n# {substep}\n\n{code}")
    
    # Combine all substep code
    combined_code = "\n\n".join(all_code)
    
    # Save the combined code for this step
    step_output_path = os.path.join(output_dir, f"step_{step_index+1}_code.py")
    with open(step_output_path, "w") as f:
        f.write(f"# Code for Step {step_index+1}:\n# {step}\n\n{combined_code}")
    
    return combined_code

def combine_substeps(
    steps: List[str], 
    substeps_code: List[str], 
    llm, 
    prompts: dict, 
    config: dict, 
    output_dir: str, 
    logger=None
) -> str:
    """
    Combine code from substeps into coherent step implementations.
    
    Args:
        steps (List[str]): List of test case steps
        substeps_code (List[str]): List of code generated for each step's substeps
        llm: Language model client for generating text
        prompts (dict): Dictionary of prompt templates
        config (dict): Configuration dictionary
        output_dir (str): Directory to store outputs
        logger: Optional logger for tracking execution
        
    Returns:
        str: Combined and coherent code for all steps
        
    Side effects:
        Saves the combined code to a file in the output directory
    """
    if logger:
        logger.info("Combining substep code into coherent step implementations")
    
    # Prepare the context for combining code
    context = "Test Case Steps:\n"
    for i, step in enumerate(steps):
        context += f"Step {i+1}: {step}\n\n"
    
    context += "\nCode for each step:\n"
    for i, code in enumerate(substeps_code):
        context += f"--- Step {i+1} Code ---\n{code}\n\n"
    
    # Prepare the prompt for combining code
    combine_prompt = prompts.get("execution", "")
    if not combine_prompt:
        combine_prompt = "Combine the following code snippets into a coherent implementation:\n\n{context}\n\nEnsure the code is well-structured, removes redundancies, and maintains all functionality."
    
    combine_prompt = combine_prompt.replace("{context}", context)
    
    # Generate combined code using the language model
    messages = [
        {"role": "system", "content": "You are an expert Python developer. Combine code snippets into clean, efficient, and well-structured implementations."},
        {"role": "user", "content": combine_prompt}
    ]
    
    response = get_messages(llm, messages, config.get("model", {}).get("temperature", 0.2))
    
    # Extract code blocks from the response
    code_blocks = re.findall(r'```(?:python)?\s*(.*?)```', response, re.DOTALL)
    
    if code_blocks:
        combined_code = code_blocks[0].strip()
    else:
        # If no code blocks found, use the entire response
        combined_code = response.strip()
    
    # Save the combined code
    combined_output_path = os.path.join(output_dir, "combined_steps_code.py")
    with open(combined_output_path, "w") as f:
        f.write(f"# Combined Code for All Steps\n\n{combined_code}")
    
    return combined_code

def refine_steps(
    steps: List[str], 
    code: str, 
    llm, 
    prompts: dict, 
    config: dict, 
    output_dir: str, 
    logger=None
) -> str:
    """
    Refine the combined step implementations for correctness and efficiency.
    
    Args:
        steps (List[str]): List of test case steps
        code (str): Combined code from all steps
        llm: Language model client for generating text
        prompts (dict): Dictionary of prompt templates
        config (dict): Configuration dictionary
        output_dir (str): Directory to store outputs
        logger: Optional logger for tracking execution
        
    Returns:
        str: Refined code implementation
        
    Side effects:
        Saves the refined code to a file in the output directory
    """
    if logger:
        logger.info("Refining step implementations for correctness and efficiency")
    
    # Prepare the context for refining code
    context = "Test Case Steps:\n"
    for i, step in enumerate(steps):
        context += f"Step {i+1}: {step}\n\n"
    
    context += f"\nCurrent Implementation:\n{code}\n\n"
    
    # Prepare the prompt for refining code
    refine_prompt = prompts.get("refinement", "")
    if not refine_prompt:
        refine_prompt = "Refine the following code implementation to ensure it correctly implements all test case steps:\n\n{context}\n\nFocus on:\n1. Correctness - Does it implement all steps correctly?\n2. Error handling - Are edge cases handled?\n3. Code quality - Is the code clean and maintainable?\n4. Performance - Can any operations be optimized?"
    
    refine_prompt = refine_prompt.replace("{context}", context)
    
    # Generate refined code using the language model
    messages = [
        {"role": "system", "content": "You are an expert Python developer. Refine code implementations for correctness, robustness, and efficiency."},
        {"role": "user", "content": refine_prompt}
    ]
    
    response = get_messages(llm, messages, config.get("model", {}).get("temperature", 0.2))
    
    # Extract code blocks from the response
    code_blocks = re.findall(r'```(?:python)?\s*(.*?)```', response, re.DOTALL)
    
    if code_blocks:
        refined_code = code_blocks[0].strip()
    else:
        # If no code blocks found, use the entire response
        refined_code = response.strip()
    
    # Save the refined code
    refined_output_path = os.path.join(output_dir, "refined_code.py")
    with open(refined_output_path, "w") as f:
        f.write(f"# Refined Code Implementation\n\n{refined_code}")
    
    return refined_code

def combine_into_test_case(
    steps: List[str], 
    code: str, 
    llm, 
    prompts: dict, 
    config: dict, 
    output_dir: str, 
    logger=None
) -> str:
    """
    Combine refined code into a complete test case implementation.
    
    Args:
        steps (List[str]): List of test case steps
        code (str): Refined code implementation
        llm: Language model client for generating text
        prompts (dict): Dictionary of prompt templates
        config (dict): Configuration dictionary
        output_dir (str): Directory to store outputs
        logger: Optional logger for tracking execution
        
    Returns:
        str: Complete test case implementation
        
    Side effects:
        Saves the test case implementation to a file in the output directory
    """
    if logger:
        logger.info("Combining refined code into a complete test case")
    
    # Prepare the context for creating a test case
    context = "Test Case Steps:\n"
    for i, step in enumerate(steps):
        context += f"Step {i+1}: {step}\n\n"
    
    context += f"\nRefined Implementation:\n{code}\n\n"
    
    # Prepare the prompt for creating a test case
    test_case_prompt = prompts.get("prompt", "")
    if not test_case_prompt:
        test_case_prompt = "Create a complete test case implementation from the following refined code:\n\n{context}\n\nEnsure the test case:\n1. Has proper setup and teardown\n2. Clearly implements each step\n3. Includes assertions to verify expected results\n4. Has clear documentation"
    
    test_case_prompt = test_case_prompt.replace("{context}", context)
    
    # Generate test case using the language model
    messages = [
        {"role": "system", "content": "You are an expert Python test developer. Create comprehensive test cases that verify all requirements."},
        {"role": "user", "content": test_case_prompt}
    ]
    
    response = get_messages(llm, messages, config.get("model", {}).get("temperature", 0.2))
    
    # Extract code blocks from the response
    code_blocks = re.findall(r'```(?:python)?\s*(.*?)```', response, re.DOTALL)
    
    if code_blocks:
        test_case_code = code_blocks[0].strip()
    else:
        # If no code blocks found, use the entire response
        test_case_code = response.strip()
    
    # Save the test case code
    test_case_output_path = os.path.join(output_dir, "test_case.py")
    with open(test_case_output_path, "w") as f:
        f.write(f"# Complete Test Case Implementation\n\n{test_case_code}")
    
    return test_case_code

def optimize_final_code(
    code: str, 
    llm, 
    prompts: dict, 
    config: dict, 
    output_dir: str, 
    logger=None
) -> str:
    """
    Optimize the final code for correctness, efficiency, and readability.
    
    Args:
        code (str): Complete test case implementation
        llm: Language model client for generating text
        prompts (dict): Dictionary of prompt templates
        config (dict): Configuration dictionary
        output_dir (str): Directory to store outputs
        logger: Optional logger for tracking execution
        
    Returns:
        str: Optimized final code
        
    Side effects:
        Saves the optimized code to a file in the output directory
    """
    if logger:
        logger.info("Optimizing final code")
    
    # Prepare the prompt for optimizing code
    optimize_prompt = "Optimize the following code for correctness, efficiency, and readability:\n\n"
    optimize_prompt += f"```python\n{code}\n```\n\n"
    optimize_prompt += "Focus on:\n"
    optimize_prompt += "1. Correctness - Ensure all functionality works as expected\n"
    optimize_prompt += "2. Performance - Optimize any inefficient operations\n"
    optimize_prompt += "3. Readability - Improve code structure and documentation\n"
    optimize_prompt += "4. Best practices - Follow Python best practices and PEP 8\n"
    
    # Generate optimized code using the language model
    messages = [
        {"role": "system", "content": "You are an expert Java developer. Optimize code for correctness, efficiency, and readability."},
        {"role": "user", "content": optimize_prompt}
    ]
    
    response = get_messages(llm, messages, config.get("model", {}).get("temperature", 0.2))
    
    # Extract code blocks from the response
    code_blocks = re.findall(r'```(?:java)?\s*(.*?)```', response, re.DOTALL)
    
    if code_blocks:
        optimized_code = code_blocks[0].strip()
    else:
        # If no code blocks found, use the entire response
        optimized_code = response.strip()
    
    # Save the optimized code
    optimized_output_path = os.path.join(output_dir, "optimized_code.py")
    with open(optimized_output_path, "w") as f:
        f.write(f"# Optimized Final Code\n\n{optimized_code}")
    
    return optimized_code

# Usage
if __name__ == "__main__":
    config = load_config()
    predictor = StepwisePredictor(config)
    final_code = predictor.predict()



