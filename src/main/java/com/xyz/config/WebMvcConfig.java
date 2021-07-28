package com.xyz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.xyz.interceptor.Interceptor;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Autowired
    private Interceptor interceptor;

    // 添加拦截器
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
        super.addInterceptors(registry);
    }

    // 配置静态资源映射路径
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/favicon.ico");
        super.addResourceHandlers(registry);
    }

}
