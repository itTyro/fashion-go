package com.lzl.service;

import com.lzl.dto.UserLoginDTO;
import com.lzl.entity.User;

public interface UserService {

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);
}
