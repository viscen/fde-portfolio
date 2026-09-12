package com.jmn.rag.comm.exception;

import com.jmn.rag.comm.base.RstObj;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 全局异常处理
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@ControllerAdvice
@Hidden
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ServiceAlertException.class)
	@ResponseBody
	public RstObj<String> alert(ServiceAlertException e, HttpServletResponse response) {
		return RstObj.newAlert(e.getMessage());
	}

	@ExceptionHandler(ServiceErrorException.class)
	@ResponseBody
	public RstObj<String> error(ServiceErrorException e, HttpServletResponse response) {
		log.error("", e);
		return RstObj.newError(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public RstObj<String> error(Exception e, HttpServletResponse response) {
		log.error("", e);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return RstObj.newError("请求异常");
	}
}
