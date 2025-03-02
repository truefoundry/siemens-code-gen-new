import os
import evaluate
from typing import Dict, Union, List, Any
import Levenshtein
from codebleu import calc_codebleu
import code_bert_score
import nltk
import logging

# Enable code evaluation from Huggingface
os.environ["HF_ALLOW_CODE_EVAL"] = "1"

# Suppress NLTK download messages
logging.getLogger('nltk').setLevel(logging.ERROR)
nltk.download('wordnet', quiet=True)
nltk.download('punkt_tab', quiet=True)
nltk.download('omw-1.4', quiet=True)

def run_code_eval(test_cases: List[str], candidates: List[List[str]]) -> Dict:
    """
    Run code evaluation using Huggingface's code_eval
    """
    code_eval = evaluate.load("code_eval")
    pass_at_k, results = code_eval.compute(references=test_cases, predictions=candidates, k=[1])
    return pass_at_k

def compute_codebleu_score(reference: str, prediction: str, language: str = "python") -> Dict:
    """
    Compute CodeBLEU score between reference and prediction
    """
    result = calc_codebleu(
        references=[reference], 
        predictions=[prediction], 
        lang=language,
        weights=(0.25, 0.25, 0.25, 0.25),
        tokenizer=None
    )
    return result

def compute_codebert_score(reference: str, prediction: str, language: str = "java"):
    """
    Compute CodeBERT scores between reference and prediction
    """
    pred_results = code_bert_score.score(cands=[prediction], refs=[reference], lang=language)
    precision, recall, f1, f3 = pred_results
    return {
        'precision': precision.item(),
        'recall': recall.item(),
        'f1': f1.item(),
        'f3': f3.item()
    }

def calculate_code_metrics(reference_code: str, generated_code: str) -> Dict[str, Any]:
    """
    Calculate various code similarity metrics
    """
    # Initialize metrics
    bleu = evaluate.load('bleu')
    rouge = evaluate.load('rouge')
    meteor = evaluate.load('meteor')
    chrf = evaluate.load('chrf')
    
    results = {}
    
    # Compute ROUGE scores
    try:
        rouge_result = rouge.compute(
            predictions=[generated_code],
            references=[reference_code]
        )
        results['rouge'] = {k: float(v) for k, v in rouge_result.items()}
    except Exception as e:
        print(f"Error computing rouge: {e}")
        results['rouge'] = None
    
    # Compute METEOR score
    try:
        meteor_result = meteor.compute(
            predictions=[generated_code],
            references=[reference_code]
        )
        results['meteor'] = meteor_result
    except Exception as e:
        print(f"Error computing meteor: {e}")
        results['meteor'] = None
    
    # Compute CHRF score
    try:
        chrf_result = chrf.compute(
            predictions=[generated_code],
            references=[reference_code]
        )
        results['chrf'] = chrf_result
    except Exception as e:
        print(f"Error computing chrf: {e}")
        results['chrf'] = None


    # Add Levenshtein distance
    try:
        distance = Levenshtein.distance(reference_code, generated_code)
        similarity = 1 - (distance / max(len(reference_code), len(generated_code)))
        results['levenshtein_similarity'] = float(similarity)
    except Exception as e:
        print(f"Error computing Levenshtein similarity: {e}")
        results['levenshtein_similarity'] = None
    
    return results

def format_metrics_output(results: Dict) -> None:
    """
    Format and print metrics in a readable format
    """
    print(f"Levenshtein Similarity: {results['levenshtein_similarity']:.4f}")
    print(f"ROUGE-L: {results['rouge']['rougeL']:.4f}")
    print(f"ROUGE-1/2: {results['rouge']['rouge1']:.4f} / {results['rouge']['rouge2']:.4f}")
    print(f"METEOR: {results['meteor']['meteor']:.4f}")
    print(f"CHRF: {results['chrf']['score']:.2f}")

def format_codebleu_output(result: Dict) -> None:
    """
    Format and print CodeBLEU metrics
    """
    print(f"CodeBLEU score: {result['codebleu']:.4f}")
    print(f"ngram_match_score: {result['ngram_match_score']:.4f}")
    print(f"weighted_ngram_match_score: {result['weighted_ngram_match_score']:.4f}")
    print(f"syntax_match_score: {result['syntax_match_score']:.4f}")
    print(f"dataflow_match_score: {result['dataflow_match_score']:.4f}")

def format_codebert_output(results: Dict) -> None:
    """
    Format and print CodeBERT metrics
    """
    print(f"Precision: {results['precision']:.4f}")
    print(f"Recall: {results['recall']:.4f}")
    print(f"F1: {results['f1']:.4f}")
    print(f"F3: {results['f3']:.4f}")

def evaluate_code(reference_path: str, generated_path: str, language: str = "java") -> None:
    """
    Main function to evaluate code using multiple metrics
    """
    # Read input files
    reference_code = open(reference_path, "r").read()
    generated_code = open(generated_path, "r").read()
    
    # Calculate basic metrics
    results = calculate_code_metrics(reference_code, generated_code)
    format_metrics_output(results)
    
    # Calculate CodeBLEU
    codebleu_results = compute_codebleu_score(reference_code, generated_code, language)
    format_codebleu_output(codebleu_results)
    
    # Calculate CodeBERT
    codebert_results = compute_codebert_score(reference_code, generated_code, language)
    format_codebert_output(codebert_results)
    return {"base_metrics": results, "codebleu_metrics": codebleu_results, "codebert_metrics": codebert_results}

if __name__ == "__main__":
    # Example usage of main evaluation function
    directory = "data"
    evaluate_code(reference_path="data/generated/842.java", generated_path="data/generated/TC_05_842_predictions_prompt.java", language="java") # 0.60
    evaluate_code(reference_path="data/generated/842.java", generated_path="data/generated/TC_05_842_predictions_rag.java", language="java") # 0.62
    evaluate_code(reference_path="data/generated/842.java", generated_path="data/generated/response_qwen.java", language="java") # 0.65