package com.wustl.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wustl.company.dto.UserLoginDTO;
import com.wustl.company.dto.UserRegisterDTO;
import com.wustl.company.entity.User;
import com.wustl.company.mapper.UserMapper;
import com.wustl.company.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User register(UserRegisterDTO registerDTO) {
        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, registerDTO.getEmail());
        if (userMapper.selectOne(queryWrapper) != null) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 使用Builder模式创建新用户
        User user = User.builder()
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .targetIndustry(registerDTO.getTargetIndustry())
                .preferredLocation(registerDTO.getPreferredLocation())
                .workLifeBalanceScore(registerDTO.getWorkLifeBalanceScore())
                .salaryScore(registerDTO.getSalaryScore())
                .workingHoursScore(registerDTO.getWorkingHoursScore())
                .overtimeHoursScore(registerDTO.getOvertimeHoursScore())
                .build();

        userMapper.insert(user);
        user.setPassword(null); // 返回前清除密码
        return user;
    }

    @Override
    public User login(UserLoginDTO loginDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, loginDTO.getEmail());
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("邮箱或密码错误");
        }
        
        user.setPassword(null); // 返回前清除密码
        return user;
    }
} 