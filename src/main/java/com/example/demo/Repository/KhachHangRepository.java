package com.example.demo.Repository;

import com.example.demo.Entitys.KhachHang;
import com.example.demo.Entitys.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, BigInteger> {

    @Query(value = "select kh from KhachHang kh  order by kh.id desc ")
    Page<KhachHang> findAllBy( Pageable p);


    @Query(value = "SELECT kh FROM KhachHang kh WHERE " +
            "(kh.tenKhachHang LIKE %:search% OR kh.sdt LIKE %:search% OR kh.maKhachHang  LIKE %:search% OR kh.email  LIKE %:search%) " +
            "AND (:status IS NULL OR kh.trangThai =:status) " +
            "ORDER BY kh.id DESC")
    Page<KhachHang> findAllBySearch(@Param("search") String search,
                                    @Param("status") Integer status,
                                    Pageable pageable);


    @Query(value = "SELECT kh FROM KhachHang kh WHERE kh.id = (SELECT MAX(kh2.id) FROM KhachHang kh2)")
    Optional<KhachHang> findMaId();

    boolean existsByEmail(String email);
    boolean existsBySdt(String sdt);

    @Query(value = "select kh from KhachHang kh  where kh.email = :email ")
    List<KhachHang> findKhachHangsByEmail(@Param("email") String email);





}
