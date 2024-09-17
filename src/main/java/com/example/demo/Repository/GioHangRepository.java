package com.example.demo.Repository;

import com.example.demo.Entitys.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.math.BigInteger;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, BigInteger> {

    @Query("SELECT g FROM GioHang g WHERE g.idKhachHang.id = :idKhachHang")
    GioHang getGioHangByIdKhachHang(@Param("idKhachHang") BigInteger idKhachHang);
}
