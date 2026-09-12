package com.jmn.agent.chat.service;

import com.jmn.agent.comm.exception.ServiceErrorException;
import com.jmn.agent.chat.tools.PowerTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 智能对话服务：多轮会话记忆 + Function Calling 工具调用
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Service
@Slf4j
public class ChatService {

	/** 系统提示词 */
	private static final String SYSTEM_PROMPT = """
			你是电力营销领域的智能助手"电小二"，服务对象为供电局客户经理与营业厅人员。
			回答规则：
			1. 涉及负荷、电费、停电等实时数据时，必须调用提供的工具查询，不要凭空编造数字；
			2. 工具返回的结果要用面向客户的语气转述，不要直接暴露原始字段名；
			3. 与电力营销无关的问题，礼貌说明你的业务范围。
			""";

	private final ChatClient chatClient;
	private final ChatMemory chatMemory;

	public ChatService(ChatClient.Builder chatClientBuilder, PowerTools powerTools) {
		this.chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build();
		this.chatClient = chatClientBuilder
				.defaultSystem(SYSTEM_PROMPT)
				.defaultTools(powerTools)
				.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
				.build();
	}

	/**
	 * 同步对话：按会话ID维护多轮上下文，自动触发工具调用
	 */
	public String send(String sessionId, String message) {
		try {
			return chatClient.prompt()
					.user(message)
					.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
					.call()
					.content();
		} catch (Exception e) {
			log.error("对话调用失败 sessionId={}", sessionId, e);
			throw new ServiceErrorException("模型调用失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 流式对话（SSE）
	 */
	public Flux<String> stream(String sessionId, String message) {
		return chatClient.prompt()
				.user(message)
				.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
				.stream()
				.content();
	}

	/**
	 * 清空指定会话的历史
	 */
	public void clearHistory(String sessionId) {
		chatMemory.clear(sessionId);
		log.info("会话历史已清空 sessionId={}", sessionId);
	}
}
