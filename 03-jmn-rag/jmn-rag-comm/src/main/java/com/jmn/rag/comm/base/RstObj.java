package com.jmn.rag.comm.base;

import lombok.Data;

/**
 * 统一返回封装
 *
 * <p>state=200 成功 / 300 警告 / 500 失败，风格对齐 tripod 平台 RstObj</p>
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Data
public class RstObj<T> {

	/** 200 成功 */
	public static final int STATE_OK = 200;
	/** 300 警告 */
	public static final int STATE_ALERT = 300;
	/** 500 失败 */
	public static final int STATE_ERROR = 500;

	/** 状态码 */
	private int state;
	/** 提示消息 */
	private String msg;
	/** 返回数据 */
	private T rst;

	public RstObj() {
	}

	public RstObj(int state, String msg, T rst) {
		this.state = state;
		this.msg = msg;
		this.rst = rst;
	}

	public static <T> RstObj<T> newOk() {
		return new RstObj<>(STATE_OK, "操作成功", null);
	}

	public static <T> RstObj<T> newOk(T rst) {
		return new RstObj<>(STATE_OK, "操作成功", rst);
	}

	public static <T> RstObj<T> newAlert(String msg) {
		return new RstObj<>(STATE_ALERT, msg, null);
	}

	public static <T> RstObj<T> newError(String msg) {
		return new RstObj<>(STATE_ERROR, msg, null);
	}

	/** 是否成功 */
	public boolean successful() {
		return this.state == STATE_OK;
	}
}
