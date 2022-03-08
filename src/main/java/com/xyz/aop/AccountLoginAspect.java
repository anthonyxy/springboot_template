package com.xyz.aop;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.xy.config.SystemConfig;
import com.xy.entity.mapper.AccountInfoMapper;
import com.xy.entity.pojo.AccountInfo;
import com.xy.util.RedisUtil;
import com.xy.util.ResponseUtil;
import com.xy.util.dto.DataResult;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

/**
 * 管理员登录校验
 *
 * @anydeer
 */
// 声明这是一个组件
@Component
// 声明这是一个切面Bean
@Aspect
// 优先级
@Order(3)
public class AccountLoginAspect { // 此类用于后台AOP验证，Account结尾的contorller，直接自动注入Account对象

    private static final Logger logger = LoggerFactory.getLogger(AccountLoginAspect.class);

    @Autowired
    private RedisUtil redis;

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    // 配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
    @Pointcut("execution(* com.xyz.contorller..Account*.*(..))")
    public void packet() {
    }

    // 配置环绕通知
    @Around("packet()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取request/response对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();

        // 排除login接口
        if (request.getRequestURI().indexOf("login") != -1) {
            return joinPoint.proceed();
        }

        // 在请求头获取token
        String token = request.getHeader(SystemConfig.ACCOUNT_TOKEN);
        if (StrUtil.isEmpty(token)) {
            logger.warn("后台登录权限接口请求头中无token");
            ResponseUtil.outWithJson(response, DataResult.build300());
            return null;
        }

        // 根据token获取redis中的AccountInfo对象
        String json = redis.get(token);
        AccountInfo ai = null;
        if (StrUtil.isEmpty(json)) {
            logger.warn("后台登录权限接口请求头中的token不在redis");
            ResponseUtil.outWithJson(response, DataResult.build400());
            return null;
        } else {
            ai = JSONUtil.toBean(json, AccountInfo.class);
            if (null == ai) {
                logger.error("后台登录时redis存入了一个token:null");
                ResponseUtil.outWithJson(response, DataResult.build400());
                return null;
            }
        }

        // 登陆过期
        Date lastOperationTime = ai.getLastOperationTime();
        if (System.currentTimeMillis() - lastOperationTime.getTime() > SystemConfig.ACCOUNT_LOGIN_OUT_TIME) {
            ResponseUtil.outWithJson(response, DataResult.build400());
            return null;
        }

        // 修改最后操作时间
        AccountInfo a = new AccountInfo();
        a.setId(ai.getId());
        a.setLastOperationTime(new Date());
        accountInfoMapper.updateByPrimaryKeySelective(a);
        redis.setOutTime(token, SystemConfig.ACCOUNT_LOGIN_OUT_TIME);

        // 注入AccountInfo
        Object[] args = joinPoint.getArgs();
        args[0] = ai;
        return joinPoint.proceed(args);
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
