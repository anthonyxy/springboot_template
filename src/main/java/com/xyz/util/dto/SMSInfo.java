package com.xyz.util.dto;

// 短信发送及验证
public class SMSInfo {

	// 手机号
	private String phoneNumber;

	// 验证码
	private String code;

	// 1登录 2身份验证
	private int type;

	// 是否被使用
	private int isUse;

	// 发送时间
	private long sendTime;

	// 当天发送数量
	private int total;

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIsUse() {
		return isUse;
	}

	public void setIsUse(int isUse) {
		this.isUse = isUse;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
