package com.jmn.agent.comm.exception;

/**
 * 业务警告异常（提示级，不打断流程，前端以 toast 呈现）
 *
 * @author viscen(徐文程) 2026年09月12日
 */
public class ServiceAlertException extends RuntimeException {

	public ServiceAlertException(String message) {
		super(message);
	}

	public ServiceAlertException(String message, Throwable cause) {
		super(message, cause);
	}
}
