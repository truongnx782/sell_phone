package com.example.demo.Repository;

import com.example.demo.Entitys.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, BigInteger> {
    List<HoaDonChiTiet> findAllByIdHoaDon(HoaDon hd);
    HoaDonChiTiet findByIdHoaDon(HoaDon hoaDon);

    Optional<HoaDonChiTiet> findByIdHoaDonAndIdChiTietSP(HoaDon hoaDon, ChiTietSanPham chiTietSanPham);
}
