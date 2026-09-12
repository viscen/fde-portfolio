package com.jmn.rag.rag.embed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OpenAI 兼容 embeddings 客户端（基于 Spring AI EmbeddingModel）
 *
 * <p>中转站 embeddings 接口开通后，将 rag.embedding.mode 配置为 openai 启用。</p>
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Component
@Slf4j
public class OpenAiEmbeddingClient implements EmbeddingClient {

	private final EmbeddingModel embeddingModel;

	public OpenAiEmbeddingClient(EmbeddingModel embeddingModel) {
		this.embeddingModel = embeddingModel;
	}

	@Override
	public float[] embed(String text) {
		return embeddingModel.embed(text);
	}

	@Override
	public List<float[]> embedBatch(List<String> texts) {
		return embeddingModel.embed(texts);
	}

	@Override
	public int dimension() {
		return embeddingModel.dimensions();
	}

	@Override
	public String name() {
		return "openai";
	}
}
