package com.xyz.aop;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.xyz.annotation.ObjectParam;
import com.xyz.annotation.Param;
import com.xyz.annotation.Param.ParamType;
import com.xyz.util.ParamCheckUtil;
import com.xyz.util.ResponseUtil;
import com.xyz.util.dto.DataResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


@Component // 声明这是一个组件
@Aspect // 声明这是一个切面Bean
@Order(2)
public class ParamAspect {
    private static final Logger logger = LoggerFactory.getLogger(ParamAspect.class);

    // 配置切入点，该方法无方法体，主要为方便同类中其他方法使用此处配置的切入点
    @Pointcut("execution(* com.xyz.contorller..*.*(..))")
    public void packet() {
    }

    // 配置环绕通知
    @Around("packet()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取request和response对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();


        Object[] args = joinPoint.getArgs();

        // 获取代理切入点和入参对象
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String[] parameterNames = methodSignature.getParameterNames();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                String arg = null == args[i] ? null : args[i].toString();
                if (param.isRequired() || StrUtil.isNotEmpty(arg)) { // 非必传且前端没传，就跳过
                    if (arg == null) { // 必传但没传
                        logger.warn(parameterNames[i] + "缺失");
                        if (param.warn().length() == 0) {
                            ResponseUtil.outWithJson(response, DataResult.build9200(parameterNames[i] + "缺失"));
                        } else {
                            ResponseUtil.outWithJson(response, DataResult.build9250(param.warn()));
                        }
                        return null;
                    }
                    if (!dataValidation(param, arg)) {
                        logger.warn(parameterNames[i] + "数据有误");
                        if (param.warn().length() == 0) {
                            ResponseUtil.outWithJson(response, DataResult.build9200(parameterNames[i] + "数据有误"));
                        } else {
                            ResponseUtil.outWithJson(response, DataResult.build9250(param.warn()));
                        }
                        return null;
                    }
                }
                continue;// 如果有了@Param注解，就不检验@ObjectParam了
            }

            ObjectParam objectParam = parameter.getAnnotation(ObjectParam.class);
            if (objectParam != null) {
                Field[] field = parameter.getType().getDeclaredFields();
                for (Field value : field) {
                    Param oParam = value.getAnnotation(Param.class);
                    if (oParam != null) {
                        String oName = request.getParameter(value.getName());
                        if (oParam.isRequired() || StrUtil.isNotEmpty(oName)) { // 非必传且前端没传，就跳过
                            if (null == oName) { // 必传但没传
                                logger.warn(value.getName() + "缺失");
                                if (oParam.warn().length() == 0) {
                                    ResponseUtil.outWithJson(response, DataResult.build9200(value.getName() + "缺失"));
                                } else {
                                    ResponseUtil.outWithJson(response, DataResult.build9250(oParam.warn()));
                                }
                                return null;
                            }
                            if (!dataValidation(oParam, oName)) {
                                logger.warn(value.getName() + "数据有误");
                                if (oParam.warn().length() == 0) {
                                    ResponseUtil.outWithJson(response,
                                            DataResult.build9200(value.getName() + "数据有误"));
                                } else {
                                    ResponseUtil.outWithJson(response, DataResult.build9250(oParam.warn()));
                                }
                                return null;
                            }
                        }
                    }
                }
            }

        }
        return joinPoint.proceed();

    }

    private static boolean dataValidation(Param param, String arg) {
        if (param.type() == ParamType.MOBILE) {
            return ParamCheckUtil.isMobile(arg);
        } else if (param.type() == ParamType.POSITIVE_INTEGER) {
            return ParamCheckUtil.isPinteger(arg);
        } else if (param.type() == ParamType.DATE) {
            String regulation = ParamType.DATE.value();
            if (param.rule().length() > 0) {
                regulation = param.rule();
            }
            return ParamCheckUtil.isFormatDate(arg, regulation);
        } else if (param.type() == ParamType.EMAIL) {
            return ParamCheckUtil.isEmail(arg);
        } else if (param.type() == ParamType.QUALIFIER) {
            return ParamCheckUtil.isInclude(arg, param.rule());
        } else if (param.type() == ParamType.PLAIN) {
            if (param.rule().length() > 0) {
                return ReUtil.isMatch(param.rule(), arg);
            }
        }
        return true;
    }

}
