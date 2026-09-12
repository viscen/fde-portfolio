package com.jmn.rag.rag.store;

import com.jmn.rag.rag.model.DocChunk;

import java.util.List;

/**
 * 向量存储抽象
 *
 * @author viscen(徐文程) 2026年09月12日
 */
public interface VectorStore {

	/** 全量重建（先清空再写入） */
	void rebuild(List<DocChunk> chunks);

	/** 相似度检索：返回最相近的 topK 个分块（相似度降序） */
	List<DocChunk> search(float[] queryVector, int topK);

	/** 已存分块数 */
	int size();

	/** 持久化到磁盘（重启可复用，免重复向量化） */
	void save();

	/** 从磁盘加载 */
	void load();
}
