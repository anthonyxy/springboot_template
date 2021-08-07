package com.xyz.contorller;

import com.xyz.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xyz.annotation.Login;
import com.xyz.annotation.Param;
import com.xyz.annotation.Param.ParamType;
import com.xyz.util.dto.DataResult;

@RestController
@RequestMapping("user")
public class UserContorller {

    private static final Logger logger = LoggerFactory.getLogger(UserContorller.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录短信验证码
     *
     * @param phoneNumber 手机号
     * @return DataResult
     */
    @PostMapping("getCode")
    public DataResult getCode(@Param(type = ParamType.MOBILE) String phoneNumber) {
        try {
            return userService.getCode(phoneNumber);
        } catch (Exception e) {
            logger.error("/user/getCode", e);
            return DataResult.build9500();
        }
    }

    /**
     * 用户登录（第一次登录为注册）
     *
     * @param phoneNumber 手机号
     * @param code        验证码
     * @return DataResult
     */
    @PostMapping("/login")
    public DataResult login(@Param(type = ParamType.MOBILE) String phoneNumber, @Param String code) {
        try {
            return userService.login(phoneNumber, code);
        } catch (Exception e) {
            logger.error("/user/login", e);
            return DataResult.build9500();
        }
    }

    /**
     * 用户登出
     *
     * @return DataResult
     */
    @PostMapping("/logout")
    @Login
    public DataResult logout(Long userId) {
        try {
            return userService.logout(userId);
        } catch (Exception e) {
            logger.error("/user/logout", e);
            return DataResult.build9500();
        }
    }

}
