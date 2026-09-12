package com.jmn.rag.rag.model;

import lombok.Data;

import java.util.List;

/**
 * RAG 问答结果
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Data
public class RagAnswer {

	/** 用户问题 */
	private String question;

	/** 助手回答（含引用编号） */
	private String answer;

	/** 引用列表 */
	private List<Reference> references;

	@Data
	public static class Reference {

		/** 分块ID */
		private String chunkId;

		/** 来源文档 */
		private String docName;

		/** 文本预览 */
		private String preview;

		public static Reference of(String chunkId, String docName, String preview) {
			Reference ref = new Reference();
			ref.setChunkId(chunkId);
			ref.setDocName(docName);
			ref.setPreview(preview);
			return ref;
		}
	}
}
