package com.example.demo.Repository;

import com.example.demo.Entitys.PhieuGG_KH;
import com.example.demo.Entitys.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface PhieuGG_KHRepo extends JpaRepository<PhieuGG_KH, BigInteger> {
    @Query("SELECT p FROM PhieuGG_KH p WHERE p.voucherID = :idVoucher")
    List<PhieuGG_KH> findPhieuGG_KHSByVoucherID(@Param("idVoucher") BigInteger idVoucher);
}
