"""多模型统一调用 Hello：OpenAI 兼容接口 + 流式输出（与 Java 版等价的 Python 侧演示）。

FDE 岗位 Python 是通用语，此文件同时作为转型计划里"Python 补课"的起点。
base_url 兼容两种写法：带 /v1（如 https://api.deepseek.com/v1）或不带（如公司中转站根地址）。
"""
import os

from openai import OpenAI


def normalize_base(url: str) -> str:
    """openai SDK 需要 /v1 结尾；没有就补上。"""
    url = url.rstrip("/")
    return url if url.endswith("/v1") else url + "/v1"


def main() -> None:
    client = OpenAI(
        api_key=os.environ.get("LLM_API_KEY", "sk-demo"),
        base_url=normalize_base(os.environ.get("LLM_BASE_URL", "https://api.deepseek.com")),
    )
    model = os.environ.get("LLM_MODEL", "deepseek-chat")

    stream = client.chat.completions.create(
        model=model,
        messages=[{"role": "user", "content": "用一句话解释什么是FDE（前向部署）工程师"}],
        stream=True,
    )
    print(f"[{model}] 模型回答：", end="", flush=True)
    for chunk in stream:
        delta = chunk.choices[0].delta.content
        if delta:
            print(delta, end="", flush=True)
    print()


if __name__ == "__main__":
    main()
