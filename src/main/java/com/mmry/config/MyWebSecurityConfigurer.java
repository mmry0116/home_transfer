package com.mmry.config;

import com.alibaba.fastjson.JSON;

import org.apache.ibatis.javassist.expr.NewExpr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@Configuration
public class MyWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyUserDetailsPasswordService detailsPasswordService;
    @Autowired
    private DataSource dataSource;


    //使用数据源实现UserDetailsService
    public UserDetailsService userDetailsService() {
        return detailsPasswordService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    //将自定义的AuthenticationManager暴露在工厂
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    LoginFilter loginFilter() throws Exception {
        //如果是自定义UsernamePasswordAuthenticationFilter 那么需要在此filter下自定义登录认证的配置
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setFilterProcessesUrl("/dologin.do");
        loginFilter.setUsernameParameter("user");
        loginFilter.setPasswordParameter("pwd");
        loginFilter.setRememberMeServices(rememberMeServices());//认证成功时使用记住我
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        //前后的分离 自定义认证成功处理Handler
        loginFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                HashMap<String, Object> map = new HashMap<>();
                map.put("msg", "登录成功");
                map.put("用户信息", authentication.getPrincipal());
//                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
//                response.setStatus(HttpStatus.OK.value());
                //向前端返回json字符串数据
                response.getWriter().write(JSON.toJSONString(map));
            }
        });
        //前后的分离 自定义认证失败处理Handler
        loginFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                HashMap<String, Object> map = new HashMap<>();
                //自定义状态码 验证码错误
                map.put("msg", "用户名密码不匹配");
                if (response.getStatus() == 601) {
                    map.put("msg", exception.getMessage());
                }
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                //向前端返回json字符串数据
                response.getWriter().write(JSON.toJSONString(map));
            }
        });
        return loginFilter;
    }

    /**
     * 放行静态资源
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/bootstrap_3.3.0/**");
        web.ignoring().antMatchers("/jquery/**");
        web.ignoring().antMatchers("/File-Upload/**");
        web.ignoring().antMatchers("/picture/**");
    }

    @Bean
    RememberMeServices rememberMeServices() {
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        //  repository.setCreateTableOnStartup(true);//创建表结构 第一次时配置
        return new MySelfPersistentTokenBasedRememberMeServices(UUID.randomUUID().toString(), userDetailsService(), repository);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
//                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()//允许Prefight预检请求
                .mvcMatchers("/login.do").permitAll()
                .mvcMatchers("/kaptcha.do").permitAll()
                .mvcMatchers("/cros.do").permitAll()//测试cros跨域请求
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login.do")
                .and().exceptionHandling()
                //认证异常处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                       /* HashMap<String, Object> map = new HashMap<>();
                        map.put("msg", authException.getMessage());
                        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                     //   response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().write(JSON.toJSONString(map))*/
                        //认证失败跳转登录页面去认证
                        System.out.println("认证失败 跳转登录页面...");
                        response.sendRedirect("/home/login.do");
                    }
                })
                .and().logout().logoutUrl("/logout.do")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        //将kaptcha表中数据删除 一些缓存数据
                        String userInfo = "";
                        if (authentication.isAuthenticated()) {
                            userInfo = authentication.getPrincipal().toString();
                        }
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("msg", "注销成功");
                        map.put("用户信息", userInfo);
                        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                        response.setStatus(HttpStatus.OK.value());
                        response.getWriter().write(JSON.toJSONString(map));
                    }
                })
                //退出登录的请求方式
                .logoutRequestMatcher(new OrRequestMatcher(
                        //httpMethod一定要规范 大写
                        new AntPathRequestMatcher("/logout.do", "GET"),
                        new AntPathRequestMatcher("/logout.do", "POST")
                ))
                .and().rememberMe().rememberMeServices(rememberMeServices())
                .and().cors().configurationSource(configuration())
                .and().csrf().disable();
        //替换Filter为自定义的
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    //跨域配置
//    @Bean
    public CorsConfigurationSource configuration() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(false);
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(Duration.ofHours(1));
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
