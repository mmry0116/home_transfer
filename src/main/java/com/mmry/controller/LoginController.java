package com.mmry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class LoginController {
    //@Resource
    PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

//    @RequestMapping("/")
    public void toIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("ContextPath" + request.getContextPath());
        System.out.println("ServletPath" + request.getServletPath());
        response.sendRedirect(request.getContextPath() + "/login.do");
    }

    @RequestMapping("/cros.do")
    @ResponseBody
    public void crosTest(HttpServletResponse response) {
        try {
            PrintWriter writer = response.getWriter();
            System.out.println("crosTest...");
            writer.write("crosTest");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//
//    @RequestMapping("/login.do")
//    @ResponseBody
//    public String doLogin(HttpServletResponse response) {
//        System.out.println("logindo..............");
//        return "/setting/login";
//    }

}
