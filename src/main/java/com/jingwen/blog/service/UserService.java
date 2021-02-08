package com.jingwen.blog.service;

import com.jingwen.blog.po.User;

public interface UserService {

    User checkUser(String userName, String password);
}
