package com.mmry.config;

import com.alibaba.fastjson.JSON;

import com.mmry.exception.KaptchaNoMathException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private static String FORM_KATPCHA_KEY = "kaptcha";
    private String kaptchaParameter = FORM_KATPCHA_KEY;

    public String getKaptchaParameter() {
        return kaptchaParameter;
    }

    public void setKaptchaParameter(String kaptchaParameter) {

        this.kaptchaParameter = kaptchaParameter;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //判断是否是post方式请求
        if (!request.getMethod().equals("POST"))
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        //如果不是json类型参数请求 则调用父类的attemptAuthentication去处理
//        if (!request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE))
//            return super.attemptAuthentication(request, response);
        //获取参数
        String username, password, rememberMe = null;
        try {
//            Map<String, String> userInfo = new ObjectMapper().readValue(request.getInputStream(), Map.class);
//            username = userInfo.get(getUsernameParameter());
//            password = userInfo.get(getPasswordParameter());
//            String kaptcha = userInfo.get(getKaptchaParameter());
//            rememberMe = userInfo.get(AbstractRememberMeServices.DEFAULT_PARAMETER);
            String sessionKaptcha = (String) request.getSession().getAttribute("kaptcha");
            String dataJson = request.getParameter("data");
            Map jasnMap = JSON.parseObject(dataJson, Map.class);
            username = (String) jasnMap.get(getUsernameParameter());
            password = (String) jasnMap.get(getPasswordParameter());
            String kaptcha = (String) jasnMap.get(getKaptchaParameter());
            rememberMe = (String) jasnMap.get(AbstractRememberMeServices.DEFAULT_PARAMETER);
            //将rememberMe值传递给rememberService抽象类AbstractRememberMeServices的 去验证
            request.setAttribute(AbstractRememberMeServices.DEFAULT_PARAMETER, rememberMe);
            if (((kaptcha == null || kaptcha.equals("")) || sessionKaptcha == null || !kaptcha.equalsIgnoreCase(sessionKaptcha))) {
                throw new KaptchaNoMathException("验证码不匹配!");
            }
            System.out.println("name: " + username + "\tpwd: " + password + "\t是否记住我" + rememberMe);
            System.out.println("sessionKaptcha: " + sessionKaptcha + "\t" + "kaptcha: " + kaptcha);
        } catch (KaptchaNoMathException e) {
            response.setStatus(601);//自定义状态码 密码用户名不匹配
            throw e;
        } /*catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        //调用authentication
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username,
                password);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }


}
