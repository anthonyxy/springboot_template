package com.xyz.aop;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.domain.Account;
import com.xyz.annotation.Login;
import com.xyz.annotation.Login.Type;
import com.xyz.annotation.RmLogin;
import com.xyz.config.SystemConfig;
import com.xyz.util.ParamCheckUtil;
import com.xyz.util.RedisUtil;
import com.xyz.util.ResponseUtil;
import com.xyz.util.dto.DataResult;
import com.xyz.util.dto.LoginInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * Login注解用户登录校验
 */
@Component // 声明这是一个组件
@Aspect // 声明这是一个切面Bean
@Order(1) // 优先级
public class LoginAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoginAspect.class);

    @Autowired
    private RedisUtil redis;

    // 配置切入点，该方法无方法体，主要为方便同类中其他方法使用此处配置的切入点
    @Pointcut("execution(* com.xyz.contorller..*.*(..))")
    public void packet() {
    }

    // 配置环绕通知
    @Around("packet()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        // 如果排除了登录，就不需要检验
        RmLogin rmLogin = methodSignature.getMethod().getAnnotation(RmLogin.class);
        if (null != rmLogin) {
            return joinPoint.proceed();
        }

        // 获取注解配置
        Login login;
        Login masterLogin = methodSignature.getMethod().getAnnotation(Login.class);
        Login slaveLogin = joinPoint.getTarget().getClass().getAnnotation(Login.class);
        // 如果都没有login注解，就不需要检验
        if (null == masterLogin && null == slaveLogin) {
            return joinPoint.proceed();
        }
        // 优先采用方法上的注解
        if (null != masterLogin) {
            login = masterLogin;
        } else {
            login = slaveLogin;
        }

        // 获取request response对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        if (login.getType().equals(Type.PARAM)) { // 从参数中直接获取（异步）
            String userId = request.getParameter(login.value());
            if (StrUtil.isEmpty(userId)) {
                if (login.isRequired()) {
                    logger.warn("用户登录权限接口没有传入userId参数");
                    ResponseUtil.outWithJson(response, DataResult.build9300());
                    return null;
                }
            }
            if (!ParamCheckUtil.isPinteger(userId)) {
                logger.warn("userId格式错误");
                ResponseUtil.outWithJson(response, DataResult.build9200("userId格式错误"));
                return null;
            }
        } else if (login.getType().equals(Type.HEAD)) { // 从请求头中获取（异步）
            String token = request.getHeader(SystemConfig.HEAD_TOKEN);
            if (login.isRequired()) { // token为空但必须登陆
                if (StrUtil.isEmpty(token)) { // token为空
                    logger.warn("用户登录权限接口Cookie中无token");
                    ResponseUtil.outWithJson(response, DataResult.build9300());
                    return null;
                } else { // token不为空
                    String tokenValue = redis.get(token);
                    if (StrUtil.isEmpty(tokenValue)) { // token不为空但redis无
                        logger.warn("用户登录权限接口请求头中的token不在Redis");
                        ResponseUtil.outWithJson(response, DataResult.build9400());
                        return null;
                    } else {
                        redis.setOutTime(token, SystemConfig.LOGIN_OUT_TIME, TimeUnit.DAYS);
                        // 注入Account，可开启以下权限控制
                        // Account account = JSONUtil.toBean(loginId, Account.class);
                        // if (login.role().length() > 0) {
                        //     if (login.role().indexOf(account.getRole().toString()) == -1) {
                        //         logger.warn("登录用户访问了权限不足的接口");
                        //         ResponseUtil.outWithJson(response, DataResult.build9250("权限不足"));
                        //         return null;
                        //     }
                        // }
                        // if (login.isUse()) {
                        //     Object[] args = joinPoint.getArgs();
                        //     args[login.paramIndex()] = account;
                        //     return joinPoint.proceed(args);
                        // }
                        // ==================================
                        // 注入LoginInfo
                        if (login.isUse()) {
                            Object[] args = joinPoint.getArgs();
                            LoginInfo li = new LoginInfo();
                            li.setLoginId(Long.parseLong(tokenValue));
                            li.setLoginStatus(1);
                            args[login.paramIndex()] = li;
                            return joinPoint.proceed(args);
                        }
                    }
                }
            }

        } else if (login.getType().equals(Type.COOKIE)) { // 从cookie的Value中获取（同步+异步都有的项目使用）
            String token = null;
            String cookie = request.getHeader("Cookie");
            if (StrUtil.isNotBlank(cookie)) {
                String[] cookieTokens = cookie.split("=");
                if (cookieTokens.length == 2) {
                    token = cookieTokens[1];
                }
            }
            String tokenValue = null;
            int loginStatus = 0;
            if (login.isRequired()) { // token为空但必须登陆
                if (StrUtil.isEmpty(token)) { // token为空
                    logger.warn("用户登录权限接口Cookie中无token");
                } else {
                    tokenValue = redis.get(token);
                    if (StrUtil.isEmpty(tokenValue)) { // token不为空但redis无
                        logger.warn("用户登录权限接口请Cookie中的token不在Redis");
                        loginStatus = 2;
                    } else {
                        redis.setOutTime(token, SystemConfig.LOGIN_OUT_TIME, TimeUnit.DAYS);
                        loginStatus = 1;
                    }
                }
            }
            if (login.isUse()) {
                if (StrUtil.isNotEmpty(request.getHeader("x-requested-with"))) { // 异步直接返回
                    if (loginStatus == 0) {
                        ResponseUtil.outWithJson(response, DataResult.build9300());
                        return null;
                    }
                    if (loginStatus == 2) {
                        ResponseUtil.outWithJson(response, DataResult.build9400());
                        return null;
                    }
                }
                LoginInfo li = new LoginInfo();
                if (StrUtil.isNotEmpty(tokenValue)) {
                    li.setLoginId(Long.parseLong(tokenValue));
                }
                li.setLoginStatus(loginStatus);
                Object[] args = joinPoint.getArgs();
                args[login.paramIndex()] = li;
                return joinPoint.proceed(args);
            }
        }
        return joinPoint.proceed();
    }

    /*
     * 配置前置通知,使用在方法aspect()上注册的切入点 同时接受JoinPoint切入点对象,可以没有该参数
     *
     * @Before("packet()") public void before(JoinPoint joinPoint){
     *
     * }
     *
     * //配置后置通知
     *
     * @After("packet()") public void after(JoinPoint joinPoint){
     *
     * }
     *
     * //配置后置返回通知
     *
     * @AfterReturning("packet()") public void afterReturn(JoinPoint joinPoint){
     *
     * }
     *
     * //配置抛出异常后通知
     *
     * @AfterThrowing(pointcut="packet()", throwing="ex") public void
     * afterThrow(JoinPoint joinPoint, Exception ex){ ex.printStackTrace(); }
     *
     */

}
