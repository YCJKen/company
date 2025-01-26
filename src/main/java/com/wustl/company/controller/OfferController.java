package com.wustl.company.controller;

import com.wustl.company.common.Result;
import com.wustl.company.common.Results;
import com.wustl.company.dto.OfferCreateDTO;
import com.wustl.company.entity.Offer;
import com.wustl.company.service.OfferService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {
    private final OfferService offerService;

    @PostMapping
    public Result<Offer> createOffer(@Valid @RequestBody OfferCreateDTO createDTO, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Offer offer = offerService.createOffer(createDTO, userId);
        return Results.success("创建成功", offer);
    }

    @GetMapping
    public Result<List<Offer>> getUserOffers(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<Offer> offers = offerService.getUserOffers(userId);
        return Results.success(offers);
    }

    @DeleteMapping("/{offerId}")
    public Result<Void> deleteOffer(@PathVariable Long offerId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        offerService.deleteOffer(offerId, userId);
        return Results.success("删除成功", null);
    }
} 