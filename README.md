# FDE 作品集（fde-portfolio）

面向 FDE / AI 解决方案岗位求职的可展示作品集。公司代码不可外带，本项目全部为脱敏重建，面试时用 GitHub + 演示视频替代"公司项目不能演示"的问题。

## 规划（对应 16 周转型计划 Phase 1）

| 序号 | 项目 | 技术点 | 状态 |
|---|---|---|---|
| 01 | llm-hello：多模型统一调用（Java/Python 双版） | OpenAI 兼容接口、SSE 流式、环境变量化 | ✅ 脚手架已建 |
| 02 | rag-power-docs：电力规程/政策文档问答 | 文档解析、切分、Embedding、pgvector/Milvus、Rerank、引用溯源 | ⬜ 第 4-5 周 |
| 03 | chatbi-lite：语义层问数（复刻公司 ChatBI 思路） | 语义建模（数据集/指标/维度）、NL→SQL、图表 | ⬜ 第 6 周 |
| 04 | agent-tools：工具调用 Agent | Function Calling 循环、MCP、ReAct | ⬜ 第 6 周 |
| 05 | eval-playbook：评测与 badcase 治理 | 评测集、指标、prompt 迭代 | ⬜ 第 7 周 |

## 第一步：跑通 01-llm-hello（转型计划 Day 7 任务）

### 准备一个模型入口（二选一）

**方式 A：DeepSeek API（推荐，最便宜且 OpenAI 兼容）**
1. https://platform.deepseek.com 注册 → 充值 ¥10 → 创建 API Key
2. Windows 设置环境变量（PowerShell）：
```powershell
setx LLM_API_KEY "sk-你的key"
setx LLM_BASE_URL "https://api.deepseek.com"
setx LLM_MODEL "deepseek-chat"
# 重开终端生效
```

**方式 B：本地 Ollama（离线可跑）**
```powershell
winget install Ollama.Ollama
ollama pull qwen2.5:7b
```
然后改用 Ollama 接入（见 01-llm-hello/java-spring-ai 内注释：把 openai starter 换成 ollama starter，base-url http://localhost:11434）。

### 跑 Java 版（Spring AI）

```powershell
cd 01-llm-hello/java-spring-ai
mvn spring-boot:run
# 期望输出：一行关于 FDE 的中文回答
```

### 跑 Python 版

```powershell
cd 01-llm-hello/python
pip install -r requirements.txt
python main.py
# 期望输出：流式逐字打印回答
```

### 跑通之后（Day 7 收尾）

1. `git init && git add . && git commit -m "chore: init portfolio with llm-hello"` 
2. GitHub 建同名仓库 push 上去（建议 public，脱敏无虞）
3. 在本 README 顶部加一行：`> 面试演示视频：xx.mp4`（第 2 周做完 RAG 后录）

## 面试叙事锚点

- 01 对应公司 tripod-llm 统一大模型组件经验（多模型接入/流式/Function Calling）
- 02-05 逐步补齐 JD 高频要求：RAG → Agent/MCP → 评测（见 01-JD核对表 频次排序）
