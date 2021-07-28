package com.xyz.service;

import com.xyz.util.dto.DataResult;

public interface UserService {

    // 用户登录短信验证码
    DataResult getCode(String phoneNumber) throws Exception;

    // 用户登录（第一次登录为注册）
    DataResult login(String phoneNumber, String code) throws Exception;

    // 用户登出
    DataResult logout(Long userId) throws Exception;

}
