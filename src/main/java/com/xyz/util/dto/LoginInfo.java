package com.xyz.util.dto;

import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class LoginInfo implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(LoginInfo.class);

    private static final long serialVersionUID = 1L;

    private Long loginId;

    private int loginStatus; // 登录状态，0：未登录，1：已登录，2：登陆过期

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "loginId=" + loginId +
                ", loginStatus=" + loginStatus +
                '}';
    }
}
