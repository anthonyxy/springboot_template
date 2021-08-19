package com.xyz.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.xyz.util.dto.AliSMSResponse;
import com.xyz.util.dto.SMSInfo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;

@Component
public class AliSMSUtil {

    private static final Logger logger = LoggerFactory.getLogger(AliSMSUtil.class);

    @Autowired
    private RedisUtil redis;

    private static final String ALI_ACCESS_KEY = "LTAIoEUv1u2vHTU";
    private static final String ALI_ACCESS_KEY_SECRET = "ll6jTPKD42sZM5VoBQrve6Repq1Ps";

    // 阿里云短信签名
    private static final String SIGN_NAME = "趣排排";
    // 阿里云短信模板代码-登录
    private static final String TEMPLATE_LOGIN = "SMS_17250078";
    // 阿里云短信模板代码-绑定
    private static final String TEMPLATE_AUTHENTICATION = "SMS_17254079";

    // redis存储标志
    private static final String REDIS_MAKE = "SMS";
    // 短信验证码一天可以接收的次数
    private static final int DAY_CODE_MAX = 10;
    // 短信验证码有效期（秒）
    private static final int PERIOD_VALIDITY = 300;
    // 发送短信验证码隔离时间（秒）
    private static final int ISOLATE_TIME = 59;

    private boolean sendAliSMS(String phoneNumber, String VerificationCode, String operationType) {
        DefaultProfile profile = DefaultProfile.getProfile("default", ALI_ACCESS_KEY, ALI_ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "default");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", SIGN_NAME);
        request.putQueryParameter("TemplateCode", operationType);
        request.putQueryParameter("TemplateParam", "{code:" + VerificationCode + "}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            AliSMSResponse asr = JSONUtil.toBean(response.getData(), AliSMSResponse.class);
            // 输出日志
            if ("OK".equals(asr.getCode())) { // 成功
                switch (operationType) {
                    case TEMPLATE_LOGIN:
                        System.out.println(phoneNumber + "登录验证码发送成功>>>" + VerificationCode);
                        logger.info(phoneNumber + "登录验证码发送成功");
                        break;
                    case TEMPLATE_AUTHENTICATION:
                        System.out.println(phoneNumber + "身份验证验证码发送成功>>>" + VerificationCode);
                        logger.info(phoneNumber + "身份验证验证码发送成功");
                        break;
                }
                return true;
            } else { // 失败
                switch (operationType) {
                    case TEMPLATE_LOGIN:
                        logger.error(phoneNumber + "登录验证码发送失败>>>错误码" + asr.getCode() + ">>>" + asr.getMessage());
                        break;
                    case TEMPLATE_AUTHENTICATION:
                        logger.error(phoneNumber + "身份验证验证码发送失败>>>错误码" + asr.getCode() + ">>>" + asr.getMessage());
                        break;
                }
                return false;
            }
        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String sendCode(String phoneNumber, String code, int type) throws Exception {
        // 验证参数
        if (phoneNumber.length() != 11) {
            return "请输入正确的手机号";
        }
        // redis的key
        String key = REDIS_MAKE + phoneNumber;
        SMSInfo oldSMS = redis.getObject(key, SMSInfo.class);
        // 之前没有发送验证码或者没有发送这一类型的验证码
        if (oldSMS != null && oldSMS.getType() == type) {
            // 是否重复发送
            boolean isolate = System.currentTimeMillis() - oldSMS.getSendTime() < 1000L * ISOLATE_TIME;
            if (oldSMS.getIsUse() == 0 && isolate) {
                return "一分钟内请不要重复发送验证码";
            }
            if (oldSMS.getTotal() >= DAY_CODE_MAX) {
                return "您今天的手机号验证码使用次数已用完";
            }
        }
        // 发送
//        boolean isSuccess = false;
//        switch (type) {
//            case 1:
//                isSuccess = sendAliSMS(phoneNumber, code, TEMPLATE_LOGIN);
//                break;
//            case 2:
//                isSuccess = sendAliSMS(phoneNumber, code, TEMPLATE_AUTHENTICATION);
//                break;
//        }
//        if (!isSuccess) {
//            return "系统错误，请联系客服";
//        }
        // 保存到Redis
        SMSInfo newSMS = new SMSInfo();
        newSMS.setCode(code);
        newSMS.setIsUse(0);
        newSMS.setPhoneNumber(phoneNumber);
        newSMS.setSendTime(System.currentTimeMillis());
        if (oldSMS != null) {
            newSMS.setTotal(oldSMS.getTotal() + 1);
        } else {
            newSMS.setTotal(1);
        }
        newSMS.setType(type);
        redis.setObject(key, newSMS, getDayEndDiffSecond(), TimeUnit.SECONDS);
        return "success";
    }

    public String verifyCode(String phoneNumber, String code, int type) throws Exception {
        String key = REDIS_MAKE + phoneNumber;
        SMSInfo SMS = redis.getObject(key, SMSInfo.class);
        if (null == SMS) {
            return "短信验证码未发送成功";
        }
        if (type != SMS.getType()) {
            return "该验证码不可使用，请重新获取";
        }
        if (SMS.getIsUse() == 1) {
            return "该验证码已过期，请重新获取";
        }
        boolean isTimeOut = System.currentTimeMillis() - SMS.getSendTime() > (1000L * PERIOD_VALIDITY);
        if (isTimeOut) {
            return "该验证码已过期，请重新获取";
        }
        if (!code.equals(SMS.getCode())) {
            return "验证不正确，请重新输入";
        }
        SMS.setIsUse(1);
        redis.setObject(key, SMS, getDayEndDiffSecond(), TimeUnit.SECONDS);
        return "success";
    }

    private long getDayEndDiffSecond() {
        Date date = new Date();
        long endTime = DateUtil.endOfDay(date).getTime();
        long dayEndDiff = endTime - date.getTime();
        return dayEndDiff / 1000;
    }

}
