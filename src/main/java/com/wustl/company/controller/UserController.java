package com.wustl.company.controller;

import com.wustl.company.common.Result;
import com.wustl.company.common.Results;
import com.wustl.company.dto.UserLoginDTO;
import com.wustl.company.dto.UserRegisterDTO;
import com.wustl.company.dto.UserUpdateDTO;
import com.wustl.company.entity.User;
import com.wustl.company.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        return Results.success("注册成功", user);
    }

    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        return Results.success("登录成功", user);
    }

    @PutMapping("/update")
    public Result<User> updateUser(@RequestBody UserUpdateDTO updateDTO, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        User updatedUser = userService.updateUser(userId, updateDTO);
        return Results.success(updatedUser);
    }
} 