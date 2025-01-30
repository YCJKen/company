package com.wustl.company.service;

import com.wustl.company.dto.UserLoginDTO;
import com.wustl.company.dto.UserRegisterDTO;
import com.wustl.company.dto.UserUpdateDTO;
import com.wustl.company.entity.User;

public interface UserService {
    User register(UserRegisterDTO registerDTO);
    User login(UserLoginDTO loginDTO);
    User updateUser(Integer userId, UserUpdateDTO updateDTO);
} 