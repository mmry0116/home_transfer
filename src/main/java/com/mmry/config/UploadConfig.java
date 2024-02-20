package com.mmry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class UploadConfig {

    /**
     * Unable to process parts as no multi-part configuration has been provided解决办法
     * 我们在springmvc或者springboot项目中使用commons-fileupload做文件上传，使用MultipartFile的时候，
     * 我们是缺少了multipartResolver配置，如果项目采用的是xml配置文件，我们可以在applicationContext.xml或者spring.xml文件中加入配置：
     */
    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }
}
