package com.example.demo.Repository;

import com.example.demo.Entitys.HoaDonChiTiet;
import com.example.demo.Entitys.ImeiDaBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImeiDaBanRepository extends JpaRepository<ImeiDaBan, BigInteger> {
    List<ImeiDaBan> findAllByIdHoaDonCT(HoaDonChiTiet hoaDonChiTiet);
    Optional<ImeiDaBan> findByMaImei(String maImei);
    Optional<ImeiDaBan> findByMaImeiAndTrangThai(String maImei, Integer trangThai);
    long countByIdHoaDonCT(HoaDonChiTiet idHoaDonCT);
}
