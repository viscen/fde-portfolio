package com.jmn.rag.rag.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmn.rag.rag.model.DocChunk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 内存向量存储（余弦相似度检索，JSON 文件持久化）
 *
 * <p>知识库规模小（演示级）时够用；上量后可平滑替换为 Milvus/pgvector 实现，
 * VectorStore 接口已预留。</p>
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Component
@Slf4j
public class InMemoryVectorStore implements VectorStore {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final List<DocChunk> chunks = new ArrayList<>();

	@Value("${rag.store-path:./data/vector-store.json}")
	private String storePath;

	@Override
	public synchronized void rebuild(List<DocChunk> list) {
		chunks.clear();
		chunks.addAll(list);
		save();
		log.info("向量库重建完成，共 {} 个分块", chunks.size());
	}

	@Override
	public synchronized List<DocChunk> search(float[] queryVector, int topK) {
		List<Scored> scored = new ArrayList<>(chunks.size());
		for (DocChunk chunk : chunks) {
			scored.add(new Scored(chunk, cosine(queryVector, chunk.getVector())));
		}
		scored.sort(Comparator.comparingDouble((Scored s) -> s.score).reversed());
		List<DocChunk> rst = new ArrayList<>(topK);
		for (int i = 0; i < Math.min(topK, scored.size()); i++) {
			rst.add(scored.get(i).chunk);
		}
		return rst;
	}

	@Override
	public synchronized int size() {
		return chunks.size();
	}

	@Override
	public synchronized void save() {
		try {
			File file = new File(storePath);
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, chunks);
		} catch (Exception e) {
			log.error("向量库持久化失败", e);
		}
	}

	@Override
	public synchronized void load() {
		File file = new File(storePath);
		if (!file.exists()) {
			log.info("向量库文件不存在，跳过加载: {}", storePath);
			return;
		}
		try {
			List<DocChunk> list = objectMapper.readValue(file,
					objectMapper.getTypeFactory().constructCollectionType(List.class, DocChunk.class));
			chunks.clear();
			chunks.addAll(list);
			log.info("向量库加载完成，共 {} 个分块", chunks.size());
		} catch (Exception e) {
			log.error("向量库加载失败", e);
		}
	}

	/** 余弦相似度（向量已归一化时等价于点积，这里仍按标准公式算） */
	private double cosine(float[] a, float[] b) {
		if (a == null || b == null || a.length != b.length) {
			return 0;
		}
		double dot = 0;
		double na = 0;
		double nb = 0;
		for (int i = 0; i < a.length; i++) {
			dot += (double) a[i] * b[i];
			na += (double) a[i] * a[i];
			nb += (double) b[i] * b[i];
		}
		if (na == 0 || nb == 0) {
			return 0;
		}
		return dot / (Math.sqrt(na) * Math.sqrt(nb));
	}

	/** 内部评分结构 */
	private record Scored(DocChunk chunk, double score) {
	}
}
