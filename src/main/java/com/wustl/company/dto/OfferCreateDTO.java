package com.wustl.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OfferCreateDTO {
    @NotBlank(message = "公司名称不能为空")
    private String companyName;
    
    @NotNull(message = "薪资不能为空")
    @DecimalMin(value = "0.0", message = "薪资不能为负数")
    private BigDecimal salary;
    
    @NotBlank(message = "薪资货币类型不能为空")
    private String salaryCurrency;
    
    private String jobTitle;
    private String industry;
    private String location;
    
    private Integer workLifeBalanceScore = 50;
    private Integer salarySatisfactionScore = 50;
    private Integer workingHoursScore = 50;
    private Integer overtimeHoursScore = 50;
    
    private LocalDate offerDate;
} 