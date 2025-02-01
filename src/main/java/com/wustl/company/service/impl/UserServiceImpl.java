package com.wustl.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wustl.company.dto.UserLoginDTO;
import com.wustl.company.dto.UserRegisterDTO;
import com.wustl.company.dto.UserUpdateDTO;
import com.wustl.company.entity.User;
import com.wustl.company.entity.UserFriend;
import com.wustl.company.exception.BusinessException;
import com.wustl.company.mapper.UserFriendMapper;
import com.wustl.company.mapper.UserMapper;
import com.wustl.company.service.UserService;
import com.wustl.company.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserFriendMapper userFriendMapper;

    @Override
    public User register(UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, registerDTO.getName());
        if (userMapper.selectOne(queryWrapper) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建新用户
        User user = User.builder()
                .name(registerDTO.getName())  // 使用 username 而不是 name
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .targetIndustry(registerDTO.getTargetIndustry())  // 使用 industry
                .preferredLocation(registerDTO.getPreferredLocation())  // 使用 location
                .workLifeBalanceScore(registerDTO.getWorkLifeBalanceScore())
                .salaryScore(registerDTO.getSalaryScore())
                .workingHoursScore(registerDTO.getWorkingHoursScore())
                .overtimeHoursScore(registerDTO.getOvertimeHoursScore())
                .build();
        
        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(UserLoginDTO loginDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, loginDTO.getEmail());
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("邮箱或密码错误");
        }
        
        // 生成token
        String token = jwtUtil.generateToken(user.getUserId());
        user.setPassword(null);
        // 设置token
        user.setToken(token);
        return user;
    }

    @Override
    public User updateUser(Integer userId, UserUpdateDTO updateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 使用 Builder 模式更新用户信息
        User updatedUser = User.builder()
                .userId(user.getUserId())  // 保持原有ID
                .name(updateDTO.getName())
                .email(user.getEmail())    // 保持原有邮箱
                .password(user.getPassword())  // 保持原有密码
                .targetIndustry(updateDTO.getTargetIndustry())
                .preferredLocation(updateDTO.getPreferredLocation())
                .salaryScore(updateDTO.getSalaryScore().intValue())
                .workingHoursScore(updateDTO.getWorkingHoursScore())
                .workLifeBalanceScore(updateDTO.getWorkLifeBalanceScore())
                .overtimeHoursScore(updateDTO.getOvertimeHoursScore())
                .build();
        
        userMapper.updateById(updatedUser);
        
        // 清除密码后返回
        updatedUser.setPassword(null);
        return updatedUser;
    }

    @Override
    public List<User> searchUsers(String keyword) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(User::getName, keyword)
                   .or()
                   .like(User::getEmail, keyword);
        return userMapper.selectList(queryWrapper);
    }
    
    @Override
    @Transactional
    public void sendFriendRequest(Integer userId, Integer friendId) {
        // 检查是否已经是好友
        LambdaQueryWrapper<UserFriend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFriend::getUserId, userId)
                   .eq(UserFriend::getFriendId, friendId);
        if (userFriendMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("已经发送过好友请求");
        }
        
        // 创建好友请求
        UserFriend friendRequest = UserFriend.builder()
                .userId(userId)
                .friendId(friendId)
                .status(0)  // 待确认
                .build();
        userFriendMapper.insert(friendRequest);
    }
    
    @Override
    @Transactional
    public void handleFriendRequest(Integer userId, Integer friendId, boolean accept) {
        LambdaQueryWrapper<UserFriend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFriend::getUserId, friendId)
                   .eq(UserFriend::getFriendId, userId)
                   .eq(UserFriend::getStatus, 0);
        
        UserFriend friendRequest = userFriendMapper.selectOne(queryWrapper);
        if (friendRequest == null) {
            throw new BusinessException("好友请求不存在");
        }
        
        // 更新请求状态
        friendRequest.setStatus(accept ? 1 : 2);  // 1-已确认，2-已拒绝
        userFriendMapper.updateById(friendRequest);
        
        if (accept) {
            // 创建双向好友关系
            UserFriend reverseFriend = UserFriend.builder()
                    .userId(userId)
                    .friendId(friendId)
                    .status(1)  // 已确认
                    .build();
            userFriendMapper.insert(reverseFriend);
        }
    }
    
    @Override
    public List<UserFriend> getFriendRequests(Integer userId) {
        LambdaQueryWrapper<UserFriend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFriend::getFriendId, userId)
                   .eq(UserFriend::getStatus, 0)
                   .orderByDesc(UserFriend::getCreatedAt);
                   
        List<UserFriend> requests = userFriendMapper.selectList(queryWrapper);
        
        // 填充发送请求的用户信息
        requests.forEach(request -> {
            User friend = userMapper.selectById(request.getUserId());
            if (friend != null) {
                friend.setPassword(null);
                request.setFriend(friend);
            }
        });
        
        return requests;
    }
    
    @Override
    public List<User> getFriends(Integer userId) {
        LambdaQueryWrapper<UserFriend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFriend::getUserId, userId)
                   .eq(UserFriend::getStatus, 1);
                   
        return userFriendMapper.selectList(queryWrapper)
                .stream()
                .map(friend -> {
                    User user = userMapper.selectById(friend.getFriendId());
                    if (user != null) {
                        user.setPassword(null);
                    }
                    return user;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
} 