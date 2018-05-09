package com.bjnlmf.nerc.zhihu.config;

import com.bjnlmf.nerc.zhihu.Intercepter.FileUploadInterceptor;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;


@Configuration
@EnableWebMvc
@ComponentScan(value =

{"com.bjnlmf.nerc.zhihu.controller"}, includeFilters =

{@ComponentScan.Filter(type = FilterType.ANNOTATION, value = {ControllerAdvice.class, 

RestController.class, Controller.class})})
@Import(SwaggerConfig.class)
public class WebConfig extends WebMvcConfigurerAdapter {

    // 配置静态文件
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    //限制图片上传大小
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        //单位:字节,这里设置大小为100M(可以尽量设置大点),实际处理交给自定义拦截器
        resolver.setMaxUploadSize(100*1024*1024);
        return  resolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(fileUploadInterceptor());
    }

    @Bean
    public FileUploadInterceptor fileUploadInterceptor(){
        //初始化拦截器
        return new FileUploadInterceptor();
    }

}
