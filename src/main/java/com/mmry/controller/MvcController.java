package com.mmry.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class MvcController implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/transfer/fileupload3.do").setViewName("/transfer/fileupload3");
        registry.addViewController("/login.do").setViewName("setting/login");
        // registry.addViewController("/transfer/download.do").setViewName("/transfer/download");
        registry.addViewController("/transfer/download2.do").setViewName("/transfer/download2");
    }



}
