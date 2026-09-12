package com.jmn.rag.rag.embed;

import java.util.List;

/**
 * 向量化客户端抽象：支持 OpenAI 兼容 embeddings 接口与本地兜底实现切换
 *
 * @author viscen(徐文程) 2026年09月12日
 */
public interface EmbeddingClient {

	/** 单条文本向量化 */
	float[] embed(String text);

	/** 批量文本向量化 */
	List<float[]> embedBatch(List<String> texts);

	/** 向量维度 */
	int dimension();

	/** 实现名称（local / openai） */
	String name();
}
