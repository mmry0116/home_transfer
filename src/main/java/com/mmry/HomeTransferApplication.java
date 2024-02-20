package com.mmry;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

import javax.servlet.jsp.JspApplicationContext;

//@PropertySource(value = "classpath:application-test.properties")
@PropertySource(value = "file:${user.home}\\application-test.properties",ignoreResourceNotFound = true,encoding = "UTF-8")
@MapperScan("com.mmry.dao")
@SpringBootApplication(exclude = {MultipartAutoConfiguration.class})
public class HomeTransferApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(HomeTransferApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(HomeTransferApplication.class, args);
    }

}
