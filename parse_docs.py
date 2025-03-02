from bs4 import BeautifulSoup
from typing import Dict
import glob
import os
import tiktoken

def extract_text_from_soup(soup: BeautifulSoup) -> str:
    """
    Extract text content from BeautifulSoup object between title and meta description
    
    Args:
        soup: BeautifulSoup object containing parsed HTML
        
    Returns:
        str: Extracted text content
    """
    text = soup.get_text()
    title = soup.title.get_text()
    text = text[text.index(title):]
    return text, title

def parse_html_file(file_path: str) -> str:
    """
    Parse HTML file and extract text content
    
    Args:
        file_path: Path to HTML file to parse
        
    Returns:
        str: Extracted text content from HTML
    """
    with open(file_path, 'r', encoding='utf-8') as file:
        html_doc = file.read()
    soup = BeautifulSoup(html_doc, 'html.parser')
    text, title = extract_text_from_soup(soup)
    return text, title

def parse_html_files(directory: str = 'core') -> list[str]:
    """
    Parse all HTML files in a directory and extract text content
    
    Args:
        directory: Root directory to search for HTML files
        
    Returns:
        list[str]: List of extracted text content from HTML files
    """
    files = glob.glob(f'{directory}/**/*.html', recursive=True)
    texts = []
    titles = []
    for file in files:
        text, title = parse_html_file(file)
        texts.append(text)
        titles.append(title)
    return texts, titles

def save_texts_to_files(texts: list[str], titles: list[str], output_dir: str = 'extracted_texts') -> None:
    """
    Save extracted texts to files in the specified output directory
    
    Args:
        texts: List of text content extracted from HTML files
        titles: List of titles for the text files
        output_dir: Directory to save the text files in
    """
    os.makedirs(output_dir, exist_ok=True)
    for text, title in zip(texts, titles):
        with open(f'{output_dir}/{title}.txt', 'w', encoding='utf-8') as file:
            file.write(text)

def count_tokens(texts: list[str], model: str = "gpt-4") -> tuple[list[int], int]:
    """
    Count tokens in a list of texts using tiktoken
    
    Args:
        texts: List of text strings to count tokens for
        model: Name of model to use for tokenization
        
    Returns:
        tuple[list[int], int]: List of token counts per text and total token count
    """
    enc = tiktoken.encoding_for_model(model)
    token_counts = []
    for text in texts:
        tokens = enc.encode(text)
        token_counts.append(len(tokens))
    total_tokens = sum(token_counts)
    return token_counts, total_tokens

# Example usage
if __name__ == "__main__":
    texts, titles = parse_html_files(directory='test_reference')
    save_texts_to_files(texts, titles, output_dir='extracted_text_reference')
    token_counts, total_tokens = count_tokens(texts, model="gpt-4o")
    print(f"Total tokens: {total_tokens}")



