package com.mmry.config;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : 明明如月
 * @date : 2024/5/12 9:56
 */
public class CROSPostInterceptor implements HandlerInterceptor {
//    long start = System.currentTimeMillis();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
//        start = System.currentTimeMillis();

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
//        System.out.println("Interceptor cost=" + (System.currentTimeMillis() - start));
//        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
//        response.setHeader("Access-Control-Allow-Credentials", "true");

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

}
