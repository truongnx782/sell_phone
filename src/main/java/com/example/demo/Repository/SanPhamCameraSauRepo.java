package com.example.demo.Repository;

import com.example.demo.Entitys.SanPham;
import com.example.demo.Entitys.SanPham_CameraSau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository

public interface SanPhamCameraSauRepo extends JpaRepository<SanPham_CameraSau, BigInteger> {
    List<SanPham_CameraSau> findBySanPhamId(BigInteger id);

    @Modifying
    @Query("DELETE FROM SanPham_CameraSau s WHERE s.sanPhamId = :id")
    void deleteAllBySanPhamId(@Param("id") BigInteger id);

}
