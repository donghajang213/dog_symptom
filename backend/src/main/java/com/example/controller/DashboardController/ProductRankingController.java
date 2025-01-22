package com.example.controller.DashboardController;

import com.example.entity.DashboardEntity.ProductRankingEntity;
import com.example.service.DashboardService.ProductRankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 상품 랭킹 컨트롤러
 */
@RestController
@RequestMapping("/ranking/product")
public class ProductRankingController {

    private final ProductRankingService productRankingService;

    public ProductRankingController(ProductRankingService productRankingService) {
        this.productRankingService = productRankingService;
    }

    @GetMapping
    public List<ProductRankingEntity> getProductRankings() {
        return productRankingService.getAllRankings();
    }
}