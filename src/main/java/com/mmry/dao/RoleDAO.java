package com.mmry.dao;

import com.mmry.bean.Role;

import java.util.List;

public interface RoleDAO {
    List<Role> listRoles(Integer uid);
}
