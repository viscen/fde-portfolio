package com.jmn.rag.rag.service;

import com.jmn.rag.rag.embed.EmbeddingClient;
import com.jmn.rag.rag.model.DocChunk;
import com.jmn.rag.rag.store.VectorStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 知识库入库服务：扫描文档目录 → 分块 → 向量化 → 入库
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Service
@Slf4j
public class IngestService {

	@Autowired
	private EmbeddingClient embeddingClient;

	@Autowired
	private VectorStore vectorStore;

	@Value("${rag.data-dir:./data/docs}")
	private String dataDir;

	@Value("${rag.chunk-size:500}")
	private int chunkSize;

	/** 支持的文档后缀 */
	private static final List<String> SUFFIXES = List.of(".md", ".txt");

	/**
	 * 全量重建知识库
	 *
	 * @return 入库分块数
	 */
	public int ingestAll() {
		List<DocChunk> chunks = readAndChunk();
		if (chunks.isEmpty()) {
			log.warn("知识库目录为空: {}", dataDir);
			vectorStore.rebuild(chunks);
			return 0;
		}
		List<String> texts = chunks.stream().map(DocChunk::getText).toList();
		List<float[]> vectors = embeddingClient.embedBatch(texts);
		for (int i = 0; i < chunks.size(); i++) {
			chunks.get(i).setVector(normalize(vectors.get(i)));
		}
		vectorStore.rebuild(chunks);
		return chunks.size();
	}

	/** 读取目录下全部文档并分块 */
	private List<DocChunk> readAndChunk() {
		List<DocChunk> chunks = new ArrayList<>();
		Path dir = Path.of(dataDir);
		if (!Files.isDirectory(dir)) {
			return chunks;
		}
		try (Stream<Path> files = Files.list(dir)) {
			List<Path> docs = files.filter(p -> {
						String name = p.getFileName().toString().toLowerCase();
						return SUFFIXES.stream().anyMatch(name::endsWith);
					}).sorted().toList();
			for (Path doc : docs) {
				String name = doc.getFileName().toString();
				String content = Files.readString(doc, StandardCharsets.UTF_8);
				List<String> pieces = chunk(content);
				for (int i = 0; i < pieces.size(); i++) {
					chunks.add(DocChunk.of(name, i, pieces.get(i)));
				}
				log.info("文档分块完成: {} -> {} 块", name, pieces.size());
			}
		} catch (IOException e) {
			throw new RuntimeException("读取知识库目录失败: " + dataDir, e);
		}
		return chunks;
	}

	/** 段落优先分块：按空行切段，超长段硬切，相邻段合并至 chunkSize */
	private List<String> chunk(String content) {
		String[] paragraphs = content.split("\\n\\s*\\n");
		List<String> pieces = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		for (String para : paragraphs) {
			String p = para.trim();
			if (p.isEmpty()) {
				continue;
			}
			while (p.length() > chunkSize) {
				if (current.length() > 0) {
					pieces.add(current.toString());
					current.setLength(0);
				}
				pieces.add(p.substring(0, chunkSize));
				p = p.substring(chunkSize);
			}
			if (current.length() + p.length() + 1 > chunkSize) {
				pieces.add(current.toString());
				current.setLength(0);
			}
			if (current.length() > 0) {
				current.append("\n");
			}
			current.append(p);
		}
		if (current.length() > 0) {
			pieces.add(current.toString());
		}
		return pieces;
	}

	/** 向量 L2 归一化 */
	private float[] normalize(float[] vec) {
		double sum = 0;
		for (float v : vec) {
			sum += (double) v * v;
		}
		double norm = Math.sqrt(sum);
		if (norm == 0) {
			return vec;
		}
		float[] rst = new float[vec.length];
		for (int i = 0; i < vec.length; i++) {
			rst[i] = (float) (vec[i] / norm);
		}
		return rst;
	}
}
