package com.mmry.dao;

import com.mmry.bean.User;
import org.apache.ibatis.annotations.Param;

public interface UserDAO {
    User user(String username);

    Integer updatePassword(@Param("id") Integer id,@Param("password") String password);
}
