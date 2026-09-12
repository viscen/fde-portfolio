package com.jmn.rag.rag.embed;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地哈希向量化兜底实现（字符二元组特征哈希 + L2 归一化）
 *
 * <p>当前公司中转站未开通 embeddings 接口（503），此实现保证 RAG 全链路离线可跑。
 * 属词面级语义，中文短文档检索效果可用；接口开通后将 rag.embedding.mode 切换为 openai 即可。</p>
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Component
public class LocalHashingEmbedder implements EmbeddingClient {

	/** 向量维度 */
	public static final int DIM = 512;

	@Override
	public float[] embed(String text) {
		int[] counts = new int[DIM];
		String clean = text == null ? "" : text.replaceAll("\\s+", "");
		for (int i = 0; i < clean.length() - 1; i++) {
			String bigram = clean.substring(i, i + 2);
			int bucket = Math.floorMod(bigram.hashCode(), DIM);
			counts[bucket] += 1;
			// 一元组降权叠加，增强短文本区分度
			int unigram = Math.floorMod(clean.substring(i, i + 1).hashCode(), DIM);
			counts[unigram] += 1;
		}
		float[] vec = new float[DIM];
		double sum = 0;
		for (int i = 0; i < DIM; i++) {
			vec[i] = counts[i];
			sum += vec[i] * vec[i];
		}
		double norm = Math.sqrt(sum);
		if (norm > 0) {
			for (int i = 0; i < DIM; i++) {
				vec[i] /= norm;
			}
		}
		return vec;
	}

	@Override
	public List<float[]> embedBatch(List<String> texts) {
		List<float[]> rst = new ArrayList<>(texts.size());
		for (String text : texts) {
			rst.add(embed(text));
		}
		return rst;
	}

	@Override
	public int dimension() {
		return DIM;
	}

	@Override
	public String name() {
		return "local:" + DIM + "d-charbigram";
	}
}
