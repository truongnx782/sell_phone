package com.example.demo.Service;

import com.example.demo.Entitys.PhieuGiamGia;
import com.example.demo.Repository.PhieuGiamGiaRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CouponService {
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;

    public CouponService(PhieuGiamGiaRepository couponRepository) {
        this.phieuGiamGiaRepository = couponRepository;
    }

    public void updateCouponStatus() {

        // Lấy thời gian hiện tại
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Chuyển đổi LocalDateTime thành Timestamp
        Timestamp ngayHienTai = Timestamp.valueOf(currentDateTime);

        List<PhieuGiamGia> pggHetHan = phieuGiamGiaRepository.findVoucherEnd(ngayHienTai);
        List<PhieuGiamGia> pggBatDau = phieuGiamGiaRepository.findVoucherStart(ngayHienTai);

        if(pggHetHan.size() > 0){
            System.out.println("Có phiếu hết hạn");
            for (PhieuGiamGia p : pggHetHan) {
                p.setTrangThai(2);
                phieuGiamGiaRepository.save(p);
            }
        }

        if(pggBatDau.size() > 0){
            System.out.println("Có phiếu bắt đầu");
            for (PhieuGiamGia p : pggBatDau) {
                p.setTrangThai(1);
                phieuGiamGiaRepository.save(p);
            }
        }

    }
}
