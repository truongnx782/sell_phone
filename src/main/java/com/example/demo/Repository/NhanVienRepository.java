package com.example.demo.Repository;

import com.example.demo.Entitys.NhanVien;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, BigInteger> {
    Page<NhanVien> findAllByMaNhanVienOrTenNhanVienContainingOrSdtContaining(String ma, String ten, String sdt, Pageable pageable);
    @Query(value = "SELECT nv FROM NhanVien nv WHERE nv.id = (SELECT MAX(nv2.id) FROM NhanVien nv2)")
    Optional<NhanVien> findMaId();

    @Query(value = "SELECT nv FROM NhanVien nv WHERE " +
            "(nv.tenNhanVien LIKE %:search% OR nv.sdt LIKE %:search% OR nv.maNhanVien  LIKE %:search% OR nv.email  LIKE %:search%) " +
            "AND (:status IS NULL OR nv.trangThai =:status) " +
            "ORDER BY nv.id DESC")
    Page<NhanVien> findAllBySearch(@Param("search") String search,
                                    @Param("status") Integer status,
                                    Pageable pageable);


}
