package com.mmry.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * 全局跨域配置
 * @author : 明明如月
 * @date : 2024/2/17 13:20
 */
@Configuration
public class WebMvcConfig {
//    @Bean
//    FilterRegistrationBean<CorsFilter> corsFilter() {
//        FilterRegistrationBean<org.springframework.web.filter.CorsFilter> registrationBean = new FilterRegistrationBean<>();
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowCredentials(false);
//        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
//        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
//        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
//        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
//        corsConfiguration.setMaxAge(3600l);
//        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
//        corsSource.registerCorsConfiguration("/**", corsConfiguration);//设置跨域请求允许访问服务的的资源
//        registrationBean.setFilter(new org.springframework.web.filter.CorsFilter(corsSource));
//        registrationBean.setOrder(-11);//设置filter执行循序 数字越小越先执行
//        return registrationBean;
//    }
}
