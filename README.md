# FDE 作品集（fde-portfolio）

面向 FDE / AI 解决方案岗位求职的可展示作品集。公司代码不可外带，本项目全部为脱敏重建，面试时用 GitHub + 演示视频替代"公司项目不能演示"的问题。

**详细文档见 [docs/代码说明与用户手册.md](docs/代码说明与用户手册.md)**（代码结构、接口手册、实测样例、部署手册、踩坑记录）。

## 项目一览（2026-09 已推进一个月）

| 序号 | 目录 | 项目 | 技术点 | 状态 |
|---|---|---|---|---|
| 01 | 01-llm-hello | 多模型调用 Hello | OpenAI 兼容接口、SSE 流式、Java/Python 双栈 | ✅ |
| 02 | 02-jmn-agent | 智能体服务（电力助手"电小二"） | **Function Calling、多轮会话记忆、SSE 流式、工具注册** | ✅ 已测试 |
| 03 | 03-jmn-rag | 知识库 RAG 问答（电力营销知识库） | **文档分块、向量化双实现（本地兜底/OpenAI兼容切换）、余弦检索、带引用生成** | ✅ 已测试 |
| 04 | （规划中） | ChatBI-Lite | 语义层建模（数据集/指标/维度）、NL2SQL、H2 | ⬜ 下月 |
| 05 | （规划中） | 评测 Playbook | 评测集、badcase 归类、prompt 迭代 | ⬜ 下月 |

> 02/03 两个工程采用 tripod3 同款工程规范：三层 parent（jmn-xxx-parent + comm + main）、properties 配置、
> RstObj 统一返回、XxxApi 业务域分包、assembly tar.gz 打包（conf 外置 + classpath 启动脚本）、knife4j 接口文档。

## 快速体验

环境变量：`LLM_API_KEY`、`LLM_BASE_URL`（不带/v1）、`LLM_MODEL`（详见手册 4.1）。

```bat
:: 智能体（Function Calling + 多轮）
cd 02-jmn-agent && mvn install -DskipTests
cd jmn-agent-main && mvn spring-boot:run
:: 浏览器打开 http://localhost:8801/agent/be/doc.html

:: RAG 问答
cd 03-jmn-rag && mvn install -DskipTests
cd jmn-rag-main && mvn spring-boot:run
:: 先 POST http://localhost:8802/rag/be/api/rag/ingest 入库，再 /api/rag/ask 问答
```

## 面试叙事锚点

- 02 对应"工具调用/Agent 编排"（JD 高频）；电力领域工具集模拟真实营销系统对接
- 03 对应"RAG 全链路"；重点讲 embeddings 不可用时的**本地兜底策略**与 VectorStore 接口升级路径（Milvus/pgvector）
- 两套工程展示工程化素养：统一返回封装、全局异常、knife4j 文档、conf 外置打包（生产交付视角）
