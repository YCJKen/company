package com.wustl.company.service;

import com.wustl.company.dto.OfferCreateDTO;
import com.wustl.company.entity.Offer;

import java.util.List;

public interface OfferService {
    Offer createOffer(OfferCreateDTO createDTO, Integer userId);
    List<Offer> getUserOffers(Integer userId);
    void deleteOffer(Long offerId, Integer userId);
} 