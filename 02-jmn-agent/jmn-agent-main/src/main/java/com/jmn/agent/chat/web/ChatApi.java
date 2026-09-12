package com.jmn.agent.chat.web;

import com.jmn.agent.chat.service.ChatService;
import com.jmn.agent.chat.tools.PowerTools;
import com.jmn.agent.chat.vo.ChatSendReq;
import com.jmn.agent.chat.vo.ChatSendRst;
import com.jmn.agent.comm.base.RstObj;
import com.jmn.agent.comm.constant.AppConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * 智能对话接口
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@RestController
@RequestMapping(AppConstant.VERSION_API_PREFIX + "/chat")
@Tag(name = "智能对话")
public class ChatApi {

	@Autowired
	private ChatService chatService;

	@Operation(summary = "发送消息（同步，自动触发工具调用）")
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public RstObj<ChatSendRst> send(@Valid @RequestBody ChatSendReq req) {
		String answer = chatService.send(req.getSessionId(), req.getMessage());
		return RstObj.newOk(ChatSendRst.of(req.getSessionId(), answer));
	}

	@Operation(summary = "发送消息（SSE 流式输出）")
	@RequestMapping(value = "/stream", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> stream(
			@Parameter(description = "会话ID") @RequestParam("sessionId") String sessionId,
			@Parameter(description = "用户消息") @RequestParam("message") String message) {
		return chatService.stream(sessionId, message);
	}

	@Operation(summary = "清空会话历史")
	@RequestMapping(value = "/history/clear", method = RequestMethod.POST)
	public RstObj<Void> clearHistory(@RequestParam("sessionId") String sessionId) {
		chatService.clearHistory(sessionId);
		return RstObj.newOk();
	}

	@Operation(summary = "可用工具清单")
	@RequestMapping(value = "/tools", method = RequestMethod.GET)
	public RstObj<List<Map<String, String>>> tools() {
		return RstObj.newOk(PowerTools.TOOL_SUMMARIES);
	}
}
