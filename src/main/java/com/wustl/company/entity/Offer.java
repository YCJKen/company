package com.wustl.company.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("Offers")
public class Offer {
    @TableId(value = "offer_id", type = IdType.AUTO)
    private Long offerId;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("company_name")
    private String companyName;
    
    private BigDecimal salary;
    
    @TableField("salary_currency")
    private String salaryCurrency;
    
    @TableField("job_title")
    private String jobTitle;
    
    private String industry;
    private String location;
    
    @TableField("work_life_balance_score")
    private Integer workLifeBalanceScore;
    
    @TableField("salary_satisfaction_score")
    private Integer salarySatisfactionScore;
    
    @TableField("working_hours_score")
    private Integer workingHoursScore;
    
    @TableField("overtime_hours_score")
    private Integer overtimeHoursScore;
    
    @TableField("offer_date")
    private LocalDate offerDate;
    
    @TableField(exist = false)  // 这个字段不在数据库中
    private Double similarity;
} 