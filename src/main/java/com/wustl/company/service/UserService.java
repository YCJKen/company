package com.wustl.company.service;

import com.wustl.company.dto.UserLoginDTO;
import com.wustl.company.dto.UserRegisterDTO;
import com.wustl.company.dto.UserUpdateDTO;
import com.wustl.company.entity.User;
import com.wustl.company.entity.UserFriend;

import java.util.List;

public interface UserService {
    User register(UserRegisterDTO registerDTO);
    User login(UserLoginDTO loginDTO);
    User updateUser(Integer userId, UserUpdateDTO updateDTO);
    List<User> searchUsers(String keyword);
    void sendFriendRequest(Integer userId, Integer friendId);
    void handleFriendRequest(Integer userId, Integer friendId, boolean accept);
    List<UserFriend> getFriendRequests(Integer userId);
    List<User> getFriends(Integer userId);
} 