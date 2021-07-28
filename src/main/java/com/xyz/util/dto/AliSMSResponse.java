package com.xyz.util.dto;

import java.io.Serializable;

// 阿里短信返回数据，用于查看报错
public class AliSMSResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String Code;

	private String Message;

	private String RequestId;

	private String BizId;

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getRequestId() {
		return RequestId;
	}

	public void setRequestId(String requestId) {
		RequestId = requestId;
	}

	public String getBizId() {
		return BizId;
	}

	public void setBizId(String bizId) {
		BizId = bizId;
	}

}
