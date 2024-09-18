package com.mmry.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//@Configuration
public class MvcController implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/transfer/fileupload3.do").setViewName("/transfer/fileupload3");
        registry.addViewController("/login.do").setViewName("/setting/login");
        // registry.addViewController("/transfer/download.do").setViewName("/transfer/download");
        registry.addViewController("/transfer/download2.do").setViewName("/transfer/download2");
    }



}
