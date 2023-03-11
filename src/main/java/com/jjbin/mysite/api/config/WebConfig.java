package com.jjbin.mysite.api.config;

import com.jjbin.mysite.api.config.argumentresolver.AuthArgumentResolver;
import com.jjbin.mysite.api.config.interceptor.LogInterceptor;
import com.jjbin.mysite.api.config.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:5173");
//    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");

        registry.addInterceptor(new AuthInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/join", "/login", "/logout",
                        "/board/watch/*", "/board/*/comments", "/board/list",
                        "/css/**", "/*.ico", "/error", "/docs/index");
    }
}
