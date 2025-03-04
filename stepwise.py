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
            config (dict): Configuration dictionary containing all necessary settings
        """
        self.config = config
        self.llm = create_llm_client(config)
        self.logger = setup_logging(config) if "logging" in config else None
        self.output_dir = config.get("stepwise", {}).get("output_dir", "predictions/stepwise")
        os.makedirs(self.output_dir, exist_ok=True)
        self.prompts = self._load_prompts()
        
    def _load_prompts(self) -> dict:
        """Load and process all prompt templates."""
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
        """Process variables in prompt text using config values."""
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
        """Parse test case steps from the prompt string."""
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
        """Generate detailed plans for each step."""
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
        """Generate and refine code from steps and plans."""
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
        
        Returns:
            str: The final generated code
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

# Usage
if __name__ == "__main__":
    config = load_config()
    predictor = StepwisePredictor(config)
    final_code = predictor.predict()



