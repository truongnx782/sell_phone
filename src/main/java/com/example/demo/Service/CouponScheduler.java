package com.example.demo.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CouponScheduler {
    private final CouponService couponService;

    public CouponScheduler(CouponService couponService) {
        this.couponService = couponService;
    }

    @Scheduled(fixedRate = 30000) // Chạy mỗi 1 phút
    public void updateExpiredCoupons() {
        couponService.updateCouponStatus();
    }
}
