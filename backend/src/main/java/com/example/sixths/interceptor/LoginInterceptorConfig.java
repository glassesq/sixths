package com.example.sixths.interceptor;

import com.example.sixths.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/article/**")
                .addPathPatterns("/user/**")
                .excludePathPatterns("/res/**")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/user/login");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String src = System.getProperty("user.dir") + "/statics/";
        registry.addResourceHandler("/res/**")
                .addResourceLocations("file:" + src)
                .setCachePeriod(0);
    }

}
