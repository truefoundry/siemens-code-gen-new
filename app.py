import streamlit as st
from dotenv import load_dotenv
from utils import load_config
from rag_llamaindex import create_index, generate_response
from prompt_inference import run_prompt_inference
import os
from llama_index.core import VectorStoreIndex
load_dotenv()

def sidebar(config):
    st.header("Configuration")
    with st.expander("LLM Settings", expanded=True):
        model = st.selectbox(
            "Model",
            options=["gpt-4o"],
            index=0
        )
        temperature = st.slider(
            "Temperature",
            min_value=0.0,
            max_value=1.0,
            value=0.3,
            step=0.1
        )
        config["llm"]["model"] = model
        config["system"]["temperature"] = temperature

    with st.expander("Approach Settings", expanded=True):
        generation_type = st.selectbox(
            "Approach", 
            options=["Prompt", "RAG"],
            index=0
        )
        config["generation_type"] = generation_type

    # RAG Settings if RAG is selected
    if generation_type == "RAG":
        with st.expander("RAG Settings", expanded=True):
            embedding_model = st.selectbox(
                "Embedding Model",
                options=["text-embedding-3-large", "text-embedding-3-small"],
                index=0
            )
            similarity_top_k = st.slider(
                "Similarity Top K",
                min_value=1,
                max_value=20,
                value=8,
                step=1
            )

            config["llm"]["embedding_model"] = embedding_model
            config["llm"]["similarity_top_k"] = similarity_top_k
    return config

def upload_files():
    uploaded_files = st.file_uploader("Upload files", type=["txt"], accept_multiple_files=True)
    
    # Create a directory if not there for uploaded files
    os.makedirs("uploaded_files", exist_ok=True)
    
    # Get list of existing files in the uploaded_files directory
    existing_files = set(os.listdir("uploaded_files"))
    
    # Get list of current uploaded file names
    current_files = set()
    
    # Save new uploaded files
    if uploaded_files:
        for uploaded_file in uploaded_files:
            if uploaded_file is not None:
                file_name = uploaded_file.name
                current_files.add(file_name)
                with open(f"uploaded_files/{file_name}", "wb") as f:
                    f.write(uploaded_file.getbuffer())
    
    # Remove files that are no longer in the uploader
    files_to_remove = existing_files - current_files
    for file_name in files_to_remove:
        file_path = os.path.join("uploaded_files", file_name)
        if os.path.exists(file_path):
            os.remove(file_path)
    
    return uploaded_files

def generate_code_and_display(config: dict, index: VectorStoreIndex):
    """
    Generate code using either RAG or Prompt method and display results
    
    Args:
        config: Dictionary containing configuration parameters
        uploaded_files: List of uploaded files
        index: VectorStoreIndex for RAG generation
    """
    try:
        if config["generation_type"] == "RAG":
            response, source_texts, source_names = generate_response(config, index)
            #st.write(response)
            #st.write(source_texts)
            #st.write(source_names)
            return response, source_texts, source_names
        elif config["generation_type"] == "Prompt":
            response, results = run_prompt_inference(config)
            #st.write(response)
            #st.write(results)
            return response, results
        st.success("Code generation complete!")
    except Exception as e:
        st.error(f"Error generating code: {str(e)}")

def update_config_paths(config, uploaded_file, input_prompt_path):
    config["paths"]["input_prompt_path"] = input_prompt_path
    config["paths"]["output_file_rag"] = f"data/rag/{uploaded_file.replace('.txt', '.java')}"
    config["paths"]["output_file_prompt"] = f"data/prompt_inference/{uploaded_file.replace('.txt', '.java')}"
    config["paths"]["ground_truth_file"] = f"Formatted_data/Ground_Truths/{uploaded_file.replace('.txt', '.java')}"
    return config

def main():
    config = load_config()
    st.title("Test Case Generation")

    # Initialize session state for index if it doesn't exist
    if 'rag_index' not in st.session_state:
        st.session_state.rag_index = None

    # Sidebar for configuration
    with st.sidebar:
        config = sidebar(config)
    
    # Main content
    uploaded_files = upload_files()

    # Create index
    if config["generation_type"] == "RAG":
        if st.button("Create Index"):
            with st.spinner("Creating index..."):
                try:
                    st.session_state.rag_index = create_index(config)
                    st.success("Index created successfully!")
                except Exception as e:
                    st.error(f"Error creating index: {str(e)}")

    if uploaded_files:
        # Run experiments
        if config["generation_type"] == "RAG":
            if st.session_state.rag_index is not None:
                if st.button("Generate Code"):
                    with st.spinner("Generating code..."):
                        for uploaded_file in os.listdir("uploaded_files"):
                            st.write(f"Generating code for {uploaded_file}")
                            input_prompt_path = f"uploaded_files/{uploaded_file}"     
                            config = update_config_paths(config, uploaded_file, input_prompt_path)
                            response, source_texts, source_names = generate_code_and_display(config, st.session_state.rag_index)
                            st.write(response.response)
                            #st.write(source_texts)
                            st.write(f"Reference texts: {source_names}")
                        
            else:
                st.warning("Please create an index first before generating code.")
        elif config["generation_type"] == "Prompt":
            if st.button("Generate Code"):
                with st.spinner("Generating code..."):
                        for uploaded_file in os.listdir("uploaded_files"):
                            st.write(f"Generating code for {uploaded_file}")
                            input_prompt_path = f"uploaded_files/{uploaded_file}"
                            config = update_config_paths(config, uploaded_file, input_prompt_path)
                            response, results = run_prompt_inference(config)
                            st.write(f"Code generated for {uploaded_file}")
                            st.write(response)
                        st.success("Code generation complete!")

if __name__ == "__main__":
    main()