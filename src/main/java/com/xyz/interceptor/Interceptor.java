package com.xyz.interceptor;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.xyz.config.SystemConfig;
import com.xyz.util.ConstantUtil;
import com.xyz.util.ResponseUtil;
import com.xyz.util.SignatureUtil;
import com.xyz.util.dto.DataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

// 全局拦截器
@Component
public class Interceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(Interceptor.class);

    // 预处理回调
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = ConstantUtil.getIp(request);
        String url = request.getRequestURI();
        // in日志
        comeIn(ip, url);
        // 验证签名前打印参数
        requestparam(request);
        // 跳过某些url
        if (url.contains("/file/readFile/")) {
            return true;
        }
        // 前端分离的数据安全验证
        if (SystemConfig.IS_SIGN) {
            // 来自客户端的请求
            String sign = request.getParameter(SystemConfig.SIGN_KEY);
            String timestamp = request.getParameter(SystemConfig.TIMESTAMP_KEY);
            String nonce = request.getParameter(SystemConfig.NONCE_KEY);
            if (StrUtil.isEmpty(sign) || StrUtil.isEmpty(timestamp) || StrUtil.isEmpty(nonce)) {
                ResponseUtil.outWithJson(response, DataResult.build9200("签名参数缺失"));
                comeOut();
                return false;
            }
            if (SystemConfig.IS_SIGN_PAST) {
                long nowTime = new Date().getTime();
                long requestTime = Long.parseLong(timestamp);
                if (nowTime - requestTime < SystemConfig.SIGN_PAST_TIME * 1000) {
                    ResponseUtil.outWithJson(response, DataResult.build9200("签名过期"));
                    comeOut();
                    return false;
                }
            }
            boolean flag = SignatureUtil.checkSignature(sign, timestamp, nonce);
            if (!flag) {
                ResponseUtil.outWithJson(response, DataResult.build9200("签名错误"));
                comeOut();
                return false;
            }
        }
        return true;
    }

    // 后处理回调，preHandle结果为true时调用
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    // 最终回调，preHandle结果为true时调用
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        comeOut();
    }

    // 打印请求参数
    private void requestparam(HttpServletRequest req) {
        Map<String, String[]> map = req.getParameterMap();
        Set<Map.Entry<String, String[]>> keSet = map.entrySet();
        for (Map.Entry<String, String[]> maps : keSet) {
            String key = maps.getKey();
            String[] value = maps.getValue();
            logger.info(key + "=" + Arrays.toString(value));
        }
    }

    // come in
    private void comeIn(String ip, String url) {
        logger.info(new DateTime().toMsStr() + "-----Come In-----" + ip + ">>>" + url);
    }

    // come out
    private void comeOut() {
        logger.info(new DateTime().toMsStr() + "-----Come Out-----");
    }

}
