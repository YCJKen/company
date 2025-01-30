package com.wustl.company.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String name;
    private String targetIndustry;
    private String preferredLocation;
    private BigDecimal salaryScore;
    private Integer workingHoursScore;
    private Integer workLifeBalanceScore;
    private Integer overtimeHoursScore;
} 