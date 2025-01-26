package com.wustl.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wustl.company.dto.OfferCreateDTO;
import com.wustl.company.entity.Offer;
import com.wustl.company.mapper.OfferMapper;
import com.wustl.company.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {
    private final OfferMapper offerMapper;

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
} 