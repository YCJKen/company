package com.wustl.company.controller;

import com.wustl.company.common.Result;
import com.wustl.company.common.Results;
import com.wustl.company.dto.UserLoginDTO;
import com.wustl.company.dto.UserRegisterDTO;
import com.wustl.company.dto.UserUpdateDTO;
import com.wustl.company.dto.FriendRequestDTO;
import com.wustl.company.entity.User;
import com.wustl.company.entity.UserFriend;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

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

    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        List<User> users = userService.searchUsers(keyword);
        return Results.success(users);
    }

    @PostMapping("/friends/request")
    public Result<Void> sendFriendRequest(@RequestBody FriendRequestDTO requestDTO, 
                                        HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        userService.sendFriendRequest(userId, requestDTO.getFriendId());
        return Results.success();
    }
    
    @PostMapping("/friends/handle")
    public Result<Void> handleFriendRequest(@RequestBody FriendRequestDTO requestDTO,
                                          @RequestParam boolean accept,
                                          HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        userService.handleFriendRequest(userId, requestDTO.getFriendId(), accept);
        return Results.success();
    }
    
    @GetMapping("/friends/requests")
    public Result<List<UserFriend>> getFriendRequests(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<UserFriend> requests = userService.getFriendRequests(userId);
        return Results.success(requests);
    }
    
    @GetMapping("/friends")
    public Result<List<User>> getFriends(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<User> friends = userService.getFriends(userId);
        return Results.success(friends);
    }
} 