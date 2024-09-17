package com.example.demo.Repository;

import com.example.demo.Entitys.HinhThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
@Repository
public interface HinhThucThanhToanRepository extends JpaRepository<HinhThucThanhToan, BigInteger> {
    HinhThucThanhToan findByTrangThai(int trangThai);
}
