package com.itheima.reggie.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.itheima.reggie.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author xwzStart
 * @create 2022-03-01 16:53
 */
@Slf4j
@Configuration
//通过以下两个注解来指定接口文档的信息
@EnableSwagger2
@EnableKnife4j
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
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

    }


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
                .excludePathPatterns("/user/login")// 不拦截用户登录资源
                .excludePathPatterns("/doc.html")// 不拦截接口文档资源
                .excludePathPatterns("/webjars/**")// 不拦截接口文档资源
                .excludePathPatterns("/swagger-resources")// 不拦截接口文档资源
                .excludePathPatterns("/v2/api-docs");// 不拦截接口文档资源
    }

    /**
     * 定义接口文档信息
     * @return
     */
    @Bean
    public Docket createRestApi() {
        // 文档类型
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.itheima.reggie.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("瑞吉外卖")
                .version("1.0")
                .description("瑞吉外卖接口文档")
                .build();
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