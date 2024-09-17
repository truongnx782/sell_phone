package com.example.demo.Repository;

import com.example.demo.Entitys.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;

@Repository
public interface KhachHangRepo extends JpaRepository<KhachHang, BigInteger> {
    KhachHang findBySdt(String sdt);
}
