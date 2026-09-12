package com.jmn.rag.rag.model;

import lombok.Data;

/**
 * 文档分块
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Data
public class DocChunk {

	/** 唯一ID：文档名-序号 */
	private String id;

	/** 所属文档名 */
	private String docName;

	/** 块在文档内的序号 */
	private int seq;

	/** 块文本 */
	private String text;

	/** 向量（不入接口返回） */
	private float[] vector;

	public static DocChunk of(String docName, int seq, String text) {
		DocChunk chunk = new DocChunk();
		chunk.setId(docName + "-" + seq);
		chunk.setDocName(docName);
		chunk.setSeq(seq);
		chunk.setText(text);
		return chunk;
	}
}
