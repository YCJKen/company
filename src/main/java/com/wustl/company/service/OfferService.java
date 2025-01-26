package com.wustl.company.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wustl.company.dto.OfferCreateDTO;
import com.wustl.company.dto.OfferRecommendDTO;
import com.wustl.company.entity.Offer;

import java.util.List;

public interface OfferService {
    Offer createOffer(OfferCreateDTO createDTO, Integer userId);
    List<Offer> getUserOffers(Integer userId);
    void deleteOffer(Long offerId, Integer userId);
    
    // 新增推荐方法
    IPage<Offer> getRecommendedOffers(Integer userId, Integer pageNum, Integer pageSize);
} 