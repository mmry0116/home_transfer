package com.mmry.config;

import com.mmry.bean.User;
import com.mmry.dao.UserDAO;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;

@Configuration
public class MyUserDetailsPasswordService implements UserDetailsPasswordService, UserDetailsService {
    @Resource
    UserDAO userDAO;

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
       Integer result =  userDAO.updatePassword(((User) user).getId(),newPassword);
       if (result==1)
           //如果密码更新成功则将user的密码设置为newPassword
           ((User) user).setPassword(newPassword);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.user(username);
        System.out.println(username + "从数据库查询出的结果:" + user);
        if (user == null) throw new UsernameNotFoundException(username);
        return user;
    }
}
