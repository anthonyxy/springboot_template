package com.xyz.util.dto;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.json.JSONUtil;

public class DataResult implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(DataResult.class);

	private static final long serialVersionUID = 1L;

	// 9100 请求成功
	// 9200 参数错误，数据验证出现问题
	// 9250 用户操作出错（前端可以直接msg用来提醒用户）
	// 9300 未登录
	// 9400 登录过期
	// 9500 系统繁忙（系统出错）
	private Integer code;

	// 返回参数描述
	private String msg;

	// 返回的具体数据
	private Object info;

	public static DataResult build100() {
		DataResult dr = new DataResult();
		dr.setCode(9100);
		dr.setMsg("请求成功");
		log.info(dr.toString(dr));
		return dr;
	}

	public static DataResult build100(Object info) {
		DataResult dr = new DataResult();
		dr.setCode(9100);
		dr.setMsg("请求成功");
		dr.setInfo(info);
		log.info(dr.toString(dr));
		return dr;
	}

	public static DataResult build200(String msg) {
		DataResult dr = new DataResult();
		dr.setCode(9200);
		if (msg == null) {
			dr.setMsg("传入参数错误");
		} else {
			dr.setMsg(msg);
		}
		log.info(dr.toString(dr));
		return dr;
	}

	public static DataResult build250(String msg) {
		DataResult dr = new DataResult();
		dr.setCode(9250);
		dr.setMsg(msg);
		log.info(dr.toString(dr));
		return dr;
	}

	public static DataResult build300() {
		DataResult dr = new DataResult();
		dr.setCode(9300);
		dr.setMsg("您还未登录，请先登录");
		log.info(dr.toString(dr));
		return dr;
	}

	public static DataResult build400() {
		DataResult dr = new DataResult();
		dr.setCode(9400);
		dr.setMsg("登录已过期，请重新登录");
		log.info(dr.toString(dr));
		return dr;
	}

	public static DataResult build500() {
		DataResult dr = new DataResult();
		dr.setCode(9500);
		dr.setMsg("系统繁忙，请稍后再试");
		log.info(dr.toString(dr));
		return dr;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getInfo() {
		return info;
	}

	public void setInfo(Object info) {
		this.info = info;
	}

	public String toString(DataResult dr) {
		return "DataResult>>>" + JSONUtil.toJsonStr(dr);
	}

}
