package com.wustl.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    private String targetIndustry;
    private String preferredLocation;
    private Float workLifeBalanceWeight;
    private Float salaryWeight;
    private Float workingHoursWeight;
    private Float overtimeHoursWeight;
} 