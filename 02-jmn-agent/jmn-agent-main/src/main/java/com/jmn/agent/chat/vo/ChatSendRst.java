package com.jmn.agent.chat.vo;

import lombok.Data;

/**
 * 对话响应
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Data
public class ChatSendRst {

	/** 会话ID */
	private String sessionId;

	/** 助手回答 */
	private String answer;

	public static ChatSendRst of(String sessionId, String answer) {
		ChatSendRst rst = new ChatSendRst();
		rst.setSessionId(sessionId);
		rst.setAnswer(answer);
		return rst;
	}
}
