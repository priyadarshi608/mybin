from openai import OpenAI

client = OpenAI()

completion = client.chat.completions.create(
    model="gpt-4o",
    messages=[
        {"role": "user", "content": "Say hello!"}
    ]
)

print(completion.choices[0].message.content)
