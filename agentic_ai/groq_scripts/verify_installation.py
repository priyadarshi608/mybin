from groq import Groq
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()
api_key = os.getenv("GROQ_API_KEY")
if not api_key:
    raise ValueError("GROQ_API_KEY not found in .env file")

# Initialize Groq client
client = Groq(api_key=api_key)

# Test Llama 4 Scout (correct ID)
try:
    response = client.chat.completions.create(
        model="meta-llama/llama-4-scout-17b-16e-instruct",  # Prefixed ID
        messages=[{"role": "user", "content": "Hello, Llama 4! Write a simple Python 'print(\"Success!\")'."}],
        max_tokens=50
    )
    print("Success! Response:", response.choices[0].message.content)
except Exception as e:
    print(f"Error: {e}")