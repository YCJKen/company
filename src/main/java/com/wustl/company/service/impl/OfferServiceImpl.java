package com.wustl.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wustl.company.dto.OfferCreateDTO;
import com.wustl.company.dto.OfferRecommendDTO;
import com.wustl.company.entity.Offer;
import com.wustl.company.entity.User;
import com.wustl.company.mapper.OfferMapper;
import com.wustl.company.mapper.UserMapper;
import com.wustl.company.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {
    private final OfferMapper offerMapper;
    private final UserMapper userMapper;

    @Override
    public Offer createOffer(OfferCreateDTO createDTO, Integer userId) {
        Offer offer = Offer.builder()
                .userId(userId)
                .companyName(createDTO.getCompanyName())
                .salary(createDTO.getSalary())
                .salaryCurrency(createDTO.getSalaryCurrency())
                .jobTitle(createDTO.getJobTitle())
                .industry(createDTO.getIndustry())
                .location(createDTO.getLocation())
                .workLifeBalanceScore(createDTO.getWorkLifeBalanceScore())
                .salarySatisfactionScore(createDTO.getSalarySatisfactionScore())
                .workingHoursScore(createDTO.getWorkingHoursScore())
                .overtimeHoursScore(createDTO.getOvertimeHoursScore())
                .offerDate(createDTO.getOfferDate())
                .build();

        offerMapper.insert(offer);
        return offer;
    }

    @Override
    public List<Offer> getUserOffers(Integer userId) {
        LambdaQueryWrapper<Offer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Offer::getUserId, userId)
                   .orderByDesc(Offer::getOfferDate);
        return offerMapper.selectList(queryWrapper);
    }

    @Override
    public void deleteOffer(Long offerId, Integer userId) {
        LambdaQueryWrapper<Offer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Offer::getOfferId, offerId)
                   .eq(Offer::getUserId, userId);
        offerMapper.delete(queryWrapper);
    }

    @Override
    public IPage<Offer> getRecommendedOffers(Integer userId, Integer pageNum, Integer pageSize) {
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        log.info("User found: {}", user);  // 添加用户信息日志

        // 构建用户偏好DTO
        OfferRecommendDTO userPreference = OfferRecommendDTO.builder()
                .targetIndustry(user.getTargetIndustry())
                .preferredLocation(user.getPreferredLocation())
                .workLifeBalanceScore(user.getWorkLifeBalanceScore())
                .salary(user.getSalaryScore().doubleValue())  // 使用期望薪资
                .workingHoursScore(user.getWorkingHoursScore())
                .overtimeHoursScore(user.getOvertimeHoursScore())
                .build();

        log.info("User Preference built: {}", userPreference);

        // 获取所有Offer
        List<Offer> allOffers = offerMapper.selectList(null);
        log.info("Found {} offers", allOffers.size());

        // 计算每个Offer的相似度并排序
        List<Offer> sortedOffers = allOffers.stream()
                .map(offer -> {
                    log.info("Processing offer: {}", offer);  // 添加处理日志
                    
                    OfferRecommendDTO offerDTO = OfferRecommendDTO.builder()
                            .targetIndustry(offer.getIndustry())
                            .preferredLocation(offer.getLocation())
                            .workLifeBalanceScore(offer.getWorkLifeBalanceScore())
                            .salary(offer.getSalary().doubleValue())  // 使用实际薪资
                            .workingHoursScore(offer.getWorkingHoursScore())
                            .overtimeHoursScore(offer.getOvertimeHoursScore())
                            .build();

                    log.info("Offer DTO built: {}", offerDTO);
                    
                    double similarity = userPreference.calculateSimilarity(offerDTO);
                    log.info("Calculated similarity for offer {}: {}", offer.getOfferId(), similarity);
                    
                    Offer offerWithSimilarity = Offer.builder()
                            .offerId(offer.getOfferId())
                            .userId(offer.getUserId())
                            .companyName(offer.getCompanyName())
                            .salary(offer.getSalary())
                            .salaryCurrency(offer.getSalaryCurrency())
                            .jobTitle(offer.getJobTitle())
                            .industry(offer.getIndustry())
                            .location(offer.getLocation())
                            .workLifeBalanceScore(offer.getWorkLifeBalanceScore())
                            .salarySatisfactionScore(offer.getSalarySatisfactionScore())
                            .workingHoursScore(offer.getWorkingHoursScore())
                            .overtimeHoursScore(offer.getOvertimeHoursScore())
                            .offerDate(offer.getOfferDate())
                            .similarity(similarity)
                            .build();
                    
                    log.info("Built offer with similarity: {}", offerWithSimilarity);
                    return offerWithSimilarity;
                })
                .sorted((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()))
                .collect(Collectors.toList());

        // 创建分页对象
        Page<Offer> page = new Page<>(pageNum, pageSize);
        
        // 计算总记录数和当前页的数据
        int total = sortedOffers.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<Offer> pageRecords = sortedOffers.subList(start, end);

        // 设置分页信息
        page.setTotal(total);
        page.setRecords(pageRecords);
        
        return page;
    }
} 