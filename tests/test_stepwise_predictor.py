import pytest
from unittest.mock import Mock, patch, mock_open
from stepwise import StepwisePredictor

@pytest.fixture
def mock_config():
    return {
        "model": {"temperature": 0.2},
        "stepwise": {
            "output_dir": "test_predictions/stepwise",
            "test_case": """
            | Step | Action | Expected Result |
            |------|--------|----------------|
            | 1 | Open browser | Browser opens |
            | 2 | Navigate to URL | Page loads |
            """
        },
        "paths": {
            "prompts": {
                "dir": "test_prompts"
            }
        }
    }

@pytest.fixture
def predictor(mock_config):
    with patch('stepwise.create_llm_client') as mock_llm:
        mock_llm.return_value = Mock()
        predictor = StepwisePredictor(mock_config)
        return predictor

def test_init(mock_config):
    """Test initialization of StepwisePredictor"""
    with patch('stepwise.create_llm_client'), \
         patch('os.makedirs'):
        predictor = StepwisePredictor(mock_config)
        assert predictor.config == mock_config
        assert predictor.output_dir == "test_predictions/stepwise"

def test_load_prompts(predictor):
    """Test _load_prompts method"""
    mock_prompt_content = "Test prompt ${model.temperature}"
    
    with patch('builtins.open', mock_open(read_data=mock_prompt_content)), \
         patch('os.path.exists', return_value=True):
        prompts = predictor._load_prompts()
        assert isinstance(prompts, dict)
        # Check if variable substitution worked
        assert "0.2" in prompts.get("planning", "")

def test_process_prompt_variables(predictor):
    """Test _process_prompt_variables method"""
    test_prompt = "Temperature: ${model.temperature}"
    processed = predictor._process_prompt_variables(test_prompt)
    assert processed == "Temperature: 0.2"

def test_parse_steps(predictor):
    """Test parse_steps method"""
    test_prompt = """
    | Step | Action | Expected Result |
    |------|--------|----------------|
    | 1 | Open browser | Browser opens |
    | 2 | Navigate to URL | Page loads |
    """
    steps = predictor.parse_steps(test_prompt)
    assert len(steps) == 2
    assert "Open browser" in steps[0]
    assert "Navigate to URL" in steps[1]

def test_generate_single_plan(predictor):
    """Test _generate_single_plan method"""
    test_step = "Open browser"
    
    # Mock LLM response
    predictor.llm.chat.completions.create.return_value.choices[0].message.content = "1. Initialize browser\n2. Launch browser"
    
    plan = predictor._generate_single_plan(test_step, 0)
    assert isinstance(plan, str)
    assert "Initialize browser" in plan

def test_parse_plan_into_substeps(predictor):
    """Test _parse_plan_into_substeps method"""
    test_plan = """
    1. First step
    2. Second step
    3. Third step
    """
    substeps = predictor._parse_plan_into_substeps(test_plan)
    assert len(substeps) == 3
    assert "First step" in substeps[0]

@patch('os.makedirs')
@patch('builtins.open', new_callable=mock_open)
def test_generate_code_for_substeps(mock_file, mock_makedirs, predictor):
    """Test _generate_code_for_substeps method"""
    test_step = "Open browser"
    test_plan = "1. Initialize browser"
    test_substeps = ["Initialize browser"]
    
    # Mock LLM response
    predictor.llm.chat.completions.create.return_value.choices[0].message.content = "```python\nfrom selenium import webdriver\nbrowser = webdriver.Chrome()\n```"
    
    code = predictor._generate_code_for_substeps(test_step, test_plan, test_substeps, 0)
    assert isinstance(code, str)
    assert "webdriver" in code

def test_combine_substeps(predictor):
    """Test _combine_substeps method"""
    test_steps = ["Open browser", "Navigate to URL"]
    test_codes = ["browser = webdriver.Chrome()", "browser.get(url)"]
    
    # Mock LLM response
    predictor.llm.chat.completions.create.return_value.choices[0].message.content = "```python\nfrom selenium import webdriver\nbrowser = webdriver.Chrome()\nbrowser.get(url)\n```"
    
    combined = predictor._combine_substeps(test_steps, test_codes)
    assert isinstance(combined, str)
    assert "webdriver" in combined

@pytest.mark.integration
def test_predict_integration(predictor):
    """Integration test for predict method"""
    with patch('os.makedirs'), \
         patch('builtins.open', mock_open()):
        # Mock all necessary LLM responses
        predictor.llm.chat.completions.create.return_value.choices[0].message.content = "```python\ntest_code = 'example'\n```"
        
        result = predictor.predict()
        assert isinstance(result, str)
        assert "test_code" in result

def test_error_handling(mock_config):
    """Test error handling when config is invalid"""
    invalid_config = {}
    with pytest.raises(ValueError):
        predictor = StepwisePredictor(invalid_config)
        predictor.predict() 