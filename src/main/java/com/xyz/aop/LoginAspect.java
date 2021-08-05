package com.xyz.aop;

import cn.hutool.core.util.StrUtil;
import com.xyz.annotation.Login;
import com.xyz.annotation.Login.Type;
import com.xyz.annotation.RmLogin;
import com.xyz.config.SystemConfig;
import com.xyz.util.ParamCheckUtil;
import com.xyz.util.RedisUtil;
import com.xyz.util.ResponseUtil;
import com.xyz.util.dto.DataResult;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        Login masterlogin = methodSignature.getMethod().getAnnotation(Login.class);
        Login slavelogin = joinPoint.getTarget().getClass().getAnnotation(Login.class);
        // 如果都没有login注解，就不需要检验
        if (null == masterlogin && null == slavelogin) {
            return joinPoint.proceed();
        }
        // 优先采用方法上的注解
        if (null != masterlogin) {
            login = masterlogin;
        } else {
            login = slavelogin;
        }

        // 获取request response对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        // 从参数中直接获取
        if (login.getType().equals(Type.PARAMID)) {
            String userId = request.getParameter(login.value());
            if (StrUtil.isEmpty(userId)) {
                if (login.isRequisite()) {
                    logger.warn("用户登录权限接口没有传入userId参数");
                    ResponseUtil.outWithJson(response, DataResult.build300());
                    return null;
                }
            }
            if (!ParamCheckUtil.isPinteger(userId)) {
                logger.warn("userId格式错误");
                ResponseUtil.outWithJson(response, DataResult.build200("userId格式错误"));
                return null;
            }
        } else if (login.getType().equals(Type.HEADTOKEN)) { // 从请求头中获取
            String token = request.getHeader(SystemConfig.HEAD_TOKEN);
            if (StrUtil.isEmpty(token)) {
                if (login.isRequisite()) {
                    logger.warn("用户登录权限接口请求头中无token");
                    ResponseUtil.outWithJson(response, DataResult.build300());
                    return null;
                }
            } else {
                String info = redis.get(token);
                if (StrUtil.isEmpty(info)) {
                    if (login.isRequisite()) {
                        logger.warn("用户登录权限接口请求头中的token不在redis");
                        ResponseUtil.outWithJson(response, DataResult.build400());
                        return null;
                    }
                } else {
                    redis.setOutTime(token, SystemConfig.LOGIN_OUT_TIME);
                    // 如果使用Account对象接收可开启以下权限控制，本项目注入userId暂不使用
                    // 这里取出登录时redis存的用户对象
                    // Account acc = JSONUtil.toBean(info, Account.class);
                    // 接口权限控制
                    // if (login.role().length() > 0) {
                    // if (login.role().indexOf(acc.getRole().toString()) == -1) {
                    // logger.warn("登录用户访问了权限不足的接口");
                    // ResponseUtil.outWithJson(response, DataResult.build250("权限不足"));
                    // return null;
                    // }
                    // }
                    // 使用id的话，注入进去
                    if (login.isUse()) {
                        Object[] args = joinPoint.getArgs();
                        args[login.paramIndex()] = Long.parseLong(info);
                        return joinPoint.proceed(args);
                    }
                }
            }
        } else { // 生成token放到cookie中（暂无）

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
