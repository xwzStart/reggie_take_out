package com.itheima.reggie.config;

import com.itheima.reggie.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * @author xwzStart
 * @create 2022-03-01 16:53
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

   /* */
    /**
     * 配置拦截器
     * @param registry
     */
    @Autowired
    private LoginInterceptor loginIntegerceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginIntegerceptor)
                .addPathPatterns("/**") // 拦截所有资源
                .excludePathPatterns("/employee/login")// 不拦截controller的登录方法
                .excludePathPatterns("/employee/logout")// 不拦截controller的退出方法
                .excludePathPatterns("/backend/**")// 不拦截静态资源
                .excludePathPatterns("/front/**")// 不拦截静态资源
                .excludePathPatterns("/common/**")// 不拦截静态资源
                .excludePathPatterns("/user/sendMsg")// 不拦截发送短信资源
                .excludePathPatterns("/user/login");// 不拦截用户登录资源
    }

    /**
     * 扩展消息转换器
     *
     */
 /*   @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2CborHttpMessageConverter messageconverter = new MappingJackson2CborHttpMessageConverter();
        //设置对象转换器,底层使用Jackson将java对象转为json
        messageconverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageconverter);
    }*/
}