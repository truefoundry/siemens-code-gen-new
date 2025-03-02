from langchain.schema import HumanMessage, SystemMessage
from langchain_community.chat_models import ChatOpenAI
from dotenv import load_dotenv
import os
from evals import evaluate_code
from utils import load_prompt, format_java_prompt, load_config
import yaml
load_dotenv(".env")

def create_llm_client(config: dict) -> ChatOpenAI:
    """
    Create and configure the LLM client using config parameters.
    
    Args:
        config: Dictionary containing LLM configuration
        
    Returns:
        ChatOpenAI: Configured LLM client
    """
    return ChatOpenAI(
        model=f"openai-main/{config['llm']['model']}",
        temperature=config['system']['temperature'],
        max_tokens=config['system']['max_tokens'],
        model_kwargs={
            "top_p": config['system']['top_p'],
            "presence_penalty": config['system']['presence_penalty'],
            "frequency_penalty": config['system']['frequency_penalty']
        },
        streaming=True,
        api_key=os.getenv("TFY_API_KEY"),
        base_url=os.getenv("TFY_BASE_URL"),
        extra_headers={
            "X-TFY-METADATA": '{"tfy_log_request":"true"}',
        }
    )

def get_messages(prompt: str, system_prompt: str) -> list:
    """
    Create the message list for the LLM.
    
    Args:
        prompt: The prepared prompt text
    
    Returns:
        list: List of messages for the LLM
    """
    return [
        SystemMessage(content=system_prompt),
        HumanMessage(content=prompt),
    ]
def generate_code(llm: ChatOpenAI, messages: list, output_path: str) -> str:
    """
    Generate code using LLM.
    
    Args:
        llm: LLM client
        messages: List of messages for the LLM
        
    Returns:
        str: Generated code content
    """
    generated_code = llm.invoke(messages).content
    with open(output_path, "w") as file:
        file.write(generated_code)
    return generated_code

def run_prompt_inference(config: dict) -> tuple:
    """
    Run the prompt-based code generation pipeline.
    
    Args:
        config: Configuration dictionary
        
    Returns:
        tuple: (generated_code, results, codebleu_score)
    """
    
    # Execute pipeline
    prompt = load_prompt(config['paths']['base_prompt_path'], 
                        config['paths']['input_prompt_path'])
    llm = create_llm_client(config)
    messages = get_messages(prompt, config['system']['prompt'])
    generated_code = generate_code(llm, messages, config['paths']['output_file_prompt'])
    results = evaluate_code(config['paths']['output_file_prompt'], 
                          config['paths']['ground_truth_file'])

    # Access CodeBLEU score from the correct location in results
    #codebleu_score = results['codebleu_metrics']['codebleu']
    #print(f"CodeBLEU Score: {results[1]['codebleu']:.3f}")
    return generated_code, results

if __name__ == "__main__":
    # Load configuration using utils function
    config = load_config()
    run_prompt_inference(config)