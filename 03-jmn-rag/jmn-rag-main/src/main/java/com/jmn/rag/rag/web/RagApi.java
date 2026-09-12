package com.jmn.rag.rag.web;

import com.jmn.rag.comm.base.RstObj;
import com.jmn.rag.comm.constant.AppConstant;
import com.jmn.rag.rag.embed.EmbeddingClient;
import com.jmn.rag.rag.model.RagAnswer;
import com.jmn.rag.rag.service.IngestService;
import com.jmn.rag.rag.service.RagService;
import com.jmn.rag.rag.store.VectorStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 知识库RAG问答接口
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@RestController
@RequestMapping(AppConstant.VERSION_API_PREFIX + "/rag")
@Tag(name = "知识库RAG问答")
public class RagApi {

	@Autowired
	private IngestService ingestService;

	@Autowired
	private RagService ragService;

	@Autowired
	private VectorStore vectorStore;

	@Autowired
	private EmbeddingClient embeddingClient;

	public record AskReq(@NotBlank(message = "问题不能为空") String question) {
	}

	@Operation(summary = "全量重建知识库（扫描文档目录→分块→向量化→入库）")
	@RequestMapping(value = "/ingest", method = RequestMethod.POST)
	public RstObj<Map<String, Object>> ingest() {
		int count = ingestService.ingestAll();
		return RstObj.newOk(Map.of("chunkCount", count));
	}

	@Operation(summary = "知识库问答（带引用编号）")
	@RequestMapping(value = "/ask", method = RequestMethod.POST)
	public RstObj<RagAnswer> ask(@RequestBody AskReq req) {
		return RstObj.newOk(ragService.ask(req.question()));
	}

	@Operation(summary = "知识库状态")
	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public RstObj<Map<String, Object>> status() {
		return RstObj.newOk(Map.of(
				"chunkCount", vectorStore.size(),
				"embedding", embeddingClient.name()));
	}
}
