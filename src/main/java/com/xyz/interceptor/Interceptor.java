package com.xyz.interceptor;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.aliyuncs.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

// 全局拦截器
@Component
public class Interceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(Interceptor.class);

    // 预处理回调
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = getIpAddress(request);
        String url = request.getRequestURI();
        // in日志
        comeIn(ip, url);
        // 验证签名前打印参数
        requestparam(request);
        // 跳过某些url
        if (url.contains("/file/readFile/")) {
            return true;
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

    // 获取请求的ip
    private static String getIpAddress(HttpServletRequest request) {
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            } else {
                return XFor;
            }
        }
        XFor = Xip;
        if (StrUtil.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            return XFor;
        }
        if (StrUtil.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }

}
