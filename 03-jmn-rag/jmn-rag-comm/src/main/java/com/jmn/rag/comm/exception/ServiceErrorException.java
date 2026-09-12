package com.jmn.rag.comm.exception;

/**
 * 业务错误异常（服务端错误，记录日志）
 *
 * @author viscen(徐文程) 2026年09月12日
 */
public class ServiceErrorException extends RuntimeException {

	public ServiceErrorException(String message) {
		super(message);
	}

	public ServiceErrorException(String message, Throwable cause) {
		super(message, cause);
	}
}
