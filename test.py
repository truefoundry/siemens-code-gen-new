
from langchain.schema import HumanMessage, SystemMessage
from langchain_community.chat_models import ChatOpenAI
from dotenv import load_dotenv

load_dotenv()

llm = ChatOpenAI(
    model="truefoundry-self-hosted/qwen2-5-coder-32b-instr-38f88",
    temperature=0.7,
    max_tokens=256,
    model_kwargs={
        "top_p": 0.8,
        "presence_penalty": 0,
        "frequency_penalty": 0
    },
    streaming=True,
    api_key=os.getenv("TFY_API_KEY_INTERNAL"),
    base_url=os.getenv("TFY_BASE_URL"),
    extra_headers={
        "X-TFY-METADATA": '{"tfy_log_request":"true"}',
    }
)

messages = [
    SystemMessage(content="You are an AI bot."),
    HumanMessage(content="Enter your prompt here"),
]

stream = llm.stream(input=messages)
for chunk in stream:
    print(chunk.content)

import requests
from dotenv import load_dotenv
import os

load_dotenv()
api_key = os.getenv("TFY_API_KEY_INTERNAL")
base_url = os.getenv("TFY_BASE_URL")

try:
    response = requests.post(
        base_url + "/api/llm/chat/completions",
        headers={
            "Authorization": f"Bearer {api_key}",
            "X-TFY-METADATA": '{"tfy_log_request":"true"}',
        },
        json={
            "messages": [
                {"role": "system", "content": "You are an AI bot."},
                {"role": "user", "content": "Enter your prompt here"},
            ],
            "model": "truefoundry-self-hosted/qwen2-5-coder-32b-instr-38f88",
            "temperature": 0.7,
            "max_tokens": 256,
            "top_p": 0.8,
            "top_k": 50,
            "repetition_penalty": 1,
            "frequency_penalty": 0,
            "presence_penalty": 0,
            "stop": ["</s>"]
        }
    )
    response.raise_for_status()
    data = response.json()
    output = data['choices'][0]['message']['content']
    print(output)
    
except requests.exceptions.HTTPError as http_error:
    print("HTTP error occurred:", http_error)


from langchain.schema import HumanMessage, SystemMessage
from langchain_communi   import ChatOpenAI

llm = ChatOpenAI(
    model="openai-main/o3-mini",
    temperature=0.7,
    max_tokens=256,
    model_kwargs={
        "top_p": 0.8,
        "presence_penalty": 0,
        "frequency_penalty": 0
    },
    streaming=True,
    api_key=os.getenv("TFY_API_KEY_INTERNAL"),
    base_url=os.getenv("TFY_BASE_URL"),
    extra_headers={
        "X-TFY-METADATA": '{"tfy_log_request":"true"}',
    }
)

messages = [
    SystemMessage(content="You are an AI bot."),
    HumanMessage(content="Enter your prompt here"),
]

stream = llm.stream(input=messages)
for chunk in stream:
    print(chunk.content)



from openai import OpenAI

# Click on "Generate API Key" button to create one now
client = OpenAI(api_key=os.getenv("TFY_API_KEY_EO"), base_url=os.getenv("TFY_BASE_URL"))
stream = client.chat.completions.create(
    messages = [
            {"role": "system", "content": "You are an AI bot."},
            {"role": "user", "content": "Enter your prompt here"},
    ],
    model= "openai-main/o3-mini",
    stream=True,
    frequency_penalty=0,
    presence_penalty=0,
    stop=["</s>"],
    extra_headers={
        "X-TFY-METADATA": '{"tfy_log_request":"true"}'
    }
)
for chunk in stream:
    if chunk.choices and len(chunk.choices) > 0 and chunk.choices[0].delta.content is not None:
        print(chunk.choices[0].delta.content, end="")


from langchain.schema import HumanMessage, SystemMessage
from langchain_openai import ChatOpenAI

llm = ChatOpenAI(
    model="openai-main/o3-mini",
    streaming=True,
    api_key=os.getenv("TFY_API_KEY_EO"),
    base_url=os.getenv("TFY_BASE_URL"),
    extra_headers={
        "X-TFY-METADATA": '{"tfy_log_request":"true"}',
    }
)

messages = [
    SystemMessage(content="You are an AI bot."),
    HumanMessage(content="Enter your prompt here"),
]

stream = llm.stream(input=messages)
for chunk in stream:
    print(chunk.content)