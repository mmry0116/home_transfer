package com.mmry.controller;

import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class KaptchaController {
    @Autowired
    private Producer producer;


    @RequestMapping("/kaptcha.do")
    @ResponseBody
    public void kapttcha(HttpServletResponse response, HttpSession session) {
        //生成验证文字
        String text = producer.createText();
        //生成图片
        BufferedImage image = producer.createImage(text);
        //将验证码信息存入session
        session.setAttribute("kaptcha", text);
        //将验证码存入数据库
        System.out.println("kaptcha:" + text);
        System.out.println("sessionid:" + session.getId());
        //设置ContentType 让浏览器以png接收数据
        response.setContentType("image/png");

//        response.setHeader( "Set-Cookie",   "_u=xxxx; Path=/Login; SameSite=None; Secure=true")
//        response.setHeader( "Set-Cookie",   "_u=xxxx; Path=/Login; SameSite=None; Secure=true")

        //将图片输出到浏览器
        try {
            ImageIO.write(image, "png", response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
