package com.example.demo.Repository;

import com.example.demo.Entitys.GioHang;
import com.example.demo.Entitys.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, BigInteger> {

    @Query("SELECT g FROM GioHangChiTiet g WHERE g.idGioHang.id = :idGioHang")
    List<GioHangChiTiet> getGioHangChiTietsByIdGioHang(@Param("idGioHang") BigInteger idGioHang);


}
