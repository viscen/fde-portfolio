"""多模型统一调用 Hello：OpenAI 兼容接口 + 流式输出（与 Java 版等价的 Python 侧演示）。

FDE 岗位 Python 是通用语，此文件同时作为转型计划里"Python 补课"的起点。
"""
import os

from openai import OpenAI


def main() -> None:
    client = OpenAI(
        api_key=os.environ.get("LLM_API_KEY", "sk-demo"),
        # DeepSeek 为 OpenAI 兼容接口；换通义改 https://dashscope.aliyuncs.com/compatible-mode/v1
        base_url=os.environ.get("LLM_BASE_URL", "https://api.deepseek.com"),
    )

    stream = client.chat.completions.create(
        model=os.environ.get("LLM_MODEL", "deepseek-chat"),
        messages=[{"role": "user", "content": "用一句话解释什么是FDE（前向部署）工程师"}],
        stream=True,
    )
    print("模型回答：", end="", flush=True)
    for chunk in stream:
        delta = chunk.choices[0].delta.content
        if delta:
            print(delta, end="", flush=True)
    print()


if __name__ == "__main__":
    main()
