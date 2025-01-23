package com.wustl.company.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("Users")
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;
    
    private String name;
    private String email;
    private String password;
    private String targetIndustry;
    private String preferredLocation;
    private Float workLifeBalanceWeight;
    private Float salaryWeight;
    private Float workingHoursWeight;
    private Float overtimeHoursWeight;
} 