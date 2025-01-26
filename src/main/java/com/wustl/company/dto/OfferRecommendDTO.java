package com.wustl.company.dto;

import lombok.Data;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Builder
public class OfferRecommendDTO {
    private String targetIndustry;
    private String preferredLocation;
    private Integer workLifeBalanceScore;
    private Double salary;  // 修改为 Double 类型存储具体薪资
    private Integer workingHoursScore;
    private Integer overtimeHoursScore;
    
    // 调整权重分配
    private static final double INDUSTRY_WEIGHT = 0.4;  // 行业权重 40%
    private static final double LOCATION_WEIGHT = 0.3;  // 地点权重 30%
    private static final double SALARY_WEIGHT = 0.2;    // 薪资权重 20%
    private static final double OTHER_WEIGHT = 0.1;     // 其他因素权重 10%
    
    // 薪资差异阈值
    private static final double SALARY_EXCELLENT_DIFF = 2000.0;  // 优秀匹配阈值
    private static final double SALARY_GOOD_DIFF = 3000.0;      // 良好匹配阈值
    private static final double SALARY_MAX_DIFF = 5000.0;       // 最大可接受差异
    
    private static final Logger log = LoggerFactory.getLogger(OfferRecommendDTO.class);
    
    public double calculateSimilarity(OfferRecommendDTO other) {
        if (other == null) return 0.0;
        
        double similarity = 0.0;
        log.info("Starting similarity calculation...");
        
        // 1. 行业匹配度 (40%)
        if (this.targetIndustry != null && other.targetIndustry != null) {
            log.info("Comparing industries: {} vs {}", this.targetIndustry, other.targetIndustry);
            if (this.targetIndustry.equals(other.targetIndustry)) {
                similarity += INDUSTRY_WEIGHT;
                log.info("Industry match: +{}", INDUSTRY_WEIGHT);
            }
        }
        
        // 2. 地点匹配度 (30%)
        if (this.preferredLocation != null && other.preferredLocation != null) {
            log.info("Comparing locations: {} vs {}", this.preferredLocation, other.preferredLocation);
            if (this.preferredLocation.equals(other.preferredLocation)) {
                similarity += LOCATION_WEIGHT;
                log.info("Location match: +{}", LOCATION_WEIGHT);
            }
        }
        
        // 3. 薪资匹配度 (20%)
        if (this.salary != null && other.salary != null) {
            double salaryDiff = Math.abs(this.salary - other.salary);
            double salaryMatch;
            
            if (salaryDiff <= SALARY_EXCELLENT_DIFF) {
                // 差异在2000以内，完全匹配
                salaryMatch = 1.0;
                log.info("Salary difference {} <= {}, excellent match", salaryDiff, SALARY_EXCELLENT_DIFF);
            } else if (salaryDiff <= SALARY_GOOD_DIFF) {
                // 差异在3000以内，良好匹配
                salaryMatch = 0.7;
                log.info("Salary difference {} <= {}, good match", salaryDiff, SALARY_GOOD_DIFF);
            } else if (salaryDiff <= SALARY_MAX_DIFF) {
                // 差异在5000以内，一般匹配
                salaryMatch = 0.4;
                log.info("Salary difference {} <= {}, acceptable match", salaryDiff, SALARY_MAX_DIFF);
            } else {
                // 差异太大，较差匹配
                salaryMatch = 0.1;
                log.info("Salary difference {} > {}, poor match", salaryDiff, SALARY_MAX_DIFF);
            }
            
            double salaryContribution = SALARY_WEIGHT * salaryMatch;
            similarity += salaryContribution;
            log.info("Salary match: {}, contribution: {}", salaryMatch, salaryContribution);
        }
        
        // 4. 其他评分的相似度 (10%)
        double otherScoresSimilarity = 0.0;
        int validScores = 0;
        
        // 工作生活平衡评分
        if (this.workLifeBalanceScore != null && other.workLifeBalanceScore != null) {
            double diff = Math.abs(this.workLifeBalanceScore - other.workLifeBalanceScore);
            double match = 1.0 - (diff / 100.0);
            otherScoresSimilarity += match;
            validScores++;
            log.info("Work-life balance diff: {}, match: {}", diff, match);
        }
        
        // 工作时长评分
        if (this.workingHoursScore != null && other.workingHoursScore != null) {
            double diff = Math.abs(this.workingHoursScore - other.workingHoursScore);
            double match = 1.0 - (diff / 100.0);
            otherScoresSimilarity += match;
            validScores++;
            log.info("Working hours diff: {}, match: {}", diff, match);
        }
        
        // 加班时长评分
        if (this.overtimeHoursScore != null && other.overtimeHoursScore != null) {
            double diff = Math.abs(this.overtimeHoursScore - other.overtimeHoursScore);
            double match = 1.0 - (diff / 100.0);
            otherScoresSimilarity += match;
            validScores++;
            log.info("Overtime hours diff: {}, match: {}", diff, match);
        }
        
        // 计算其他评分的平均相似度
        if (validScores > 0) {
            double avgOtherScore = otherScoresSimilarity / validScores;
            double otherContribution = OTHER_WEIGHT * avgOtherScore;
            similarity += otherContribution;
            log.info("Other scores avg: {}, contribution: {}", avgOtherScore, otherContribution);
        }
        
        log.info("Final similarity (before clamping): {}", similarity);
        
        // 确保相似度在 0-1 之间
        double clampedSimilarity = Math.max(0.0, Math.min(1.0, similarity));
        log.info("Final clamped similarity: {}", clampedSimilarity);
        
        return clampedSimilarity;
    }
} 