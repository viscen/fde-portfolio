package com.jmn.agent.chat.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 对话请求
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Data
public class ChatSendReq {

	/** 会话ID（同一ID共享多轮上下文） */
	@NotBlank(message = "会话ID不能为空")
	private String sessionId;

	/** 用户消息 */
	@NotBlank(message = "消息内容不能为空")
	private String message;
}
