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
    
    @TableField("target_industry")
    private String targetIndustry;
    
    @TableField("preferred_location")
    private String preferredLocation;
    
    @TableField("work_life_balance_score")
    private Integer workLifeBalanceScore;
    
    @TableField("salary_score")
    private Integer salaryScore;
    
    @TableField("working_hours_score")
    private Integer workingHoursScore;
    
    @TableField("overtime_hours_score")
    private Integer overtimeHoursScore;

    // 添加token字段，但不持久化到数据库
    @TableField(exist = false)
    private String token;
} 