package com.jmn.rag.rag.service;

import com.jmn.rag.comm.exception.ServiceErrorException;
import com.jmn.rag.rag.embed.EmbeddingClient;
import com.jmn.rag.rag.model.DocChunk;
import com.jmn.rag.rag.model.RagAnswer;
import com.jmn.rag.rag.store.VectorStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RAG 问答服务：检索增强生成（带引用编号）
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Service
@Slf4j
public class RagService {

	private static final String PROMPT_TEMPLATE = """
			你是电力营销领域的知识库问答助手。请仅依据下面的参考资料回答用户问题。
			规则：
			1. 回答时在依据的句子末尾标注引用编号，如 [1]、[2]；
			2. 参考资料中没有的内容，明确回答"知识库中未收录该问题"，不要编造；
			3. 用简洁的中文回答。

			参考资料：
			%s

			用户问题：%s
			""";

	@Autowired
	private EmbeddingClient embeddingClient;

	@Autowired
	private VectorStore vectorStore;

	@Autowired
	private ChatClient.Builder chatClientBuilder;

	@Value("${rag.top-k:4}")
	private int topK;

	private ChatClient chatClient;

	@jakarta.annotation.PostConstruct
	public void init() {
		this.chatClient = chatClientBuilder.build();
	}

	/**
	 * 检索 + 生成
	 */
	public RagAnswer ask(String question) {
		if (vectorStore.size() == 0) {
			throw new ServiceErrorException("知识库为空，请先调用 /api/rag/ingest 完成入库");
		}
		float[] queryVec = embeddingClient.embed(question);
		List<DocChunk> hits = vectorStore.search(queryVec, topK);

		StringBuilder context = new StringBuilder();
		for (int i = 0; i < hits.size(); i++) {
			DocChunk chunk = hits.get(i);
			context.append("[").append(i + 1).append("] （来源：").append(chunk.getDocName())
					.append("）\n").append(chunk.getText()).append("\n\n");
		}
		String prompt = PROMPT_TEMPLATE.formatted(context, question);
		String answer = chatClient.prompt().user(prompt).call().content();

		RagAnswer rst = new RagAnswer();
		rst.setQuestion(question);
		rst.setAnswer(answer);
		rst.setReferences(hits.stream()
				.map(c -> RagAnswer.Reference.of(c.getId(), c.getDocName(), preview(c.getText())))
				.toList());
		return rst;
	}

	private String preview(String text) {
		return text.length() <= 80 ? text : text.substring(0, 80) + "...";
	}
}
