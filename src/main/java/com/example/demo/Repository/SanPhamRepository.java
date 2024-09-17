package com.example.demo.Repository;

import com.example.demo.Entitys.ChiTietSanPham;

import com.example.demo.Entitys.HeDieuHanh;
import com.example.demo.Entitys.MauSac;
import com.example.demo.Entitys.SanPham;
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
public interface SanPhamRepository extends JpaRepository<SanPham, BigInteger> {

    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;


    @Query(value = "select sp from SanPham sp  order by sp.id desc ")
    Page<SanPham> findBy(Pageable pageable);
    public SanPham findByTenSanPham(String tenSP);

    public SanPham findByMaSanPham(String maSP);


    @Query(value = "SELECT sp FROM SanPham sp WHERE " +
            "(sp.maSanPham LIKE %:search% OR sp.tenSanPham LIKE %:search%) " +
            "AND (:status IS NULL OR sp.trangThai = :status) " +
            "AND (:idHang IS NULL OR sp.idHang.id = :idHang) " +
            "AND (:idHeDieuHanh IS NULL OR sp.idHeDieuHanh.id = :idHeDieuHanh) " +
            "AND (:idManHinh IS NULL OR sp.idManHinh.id = :idManHinh) " +
            "ORDER BY sp.id DESC")
    Page<SanPham> search(@Param("idHang") BigInteger idHang,
                         @Param("idHeDieuHanh") BigInteger idHeDieuHanh,
                         @Param("idManHinh") BigInteger idManHinh,
                         @Param("search") String search,
                         @Param("status") Integer status,
                         Pageable pageable);



    @Query(value = "SELECT nv FROM SanPham nv WHERE nv.id = (SELECT MAX(nv2.id) FROM SanPham nv2)")
    Optional<SanPham> findMaId();

    public SanPham findByTenSanPhamIsLike(String trim);


    public List<SanPham> findByTrangThai(int trangThai);



//    @Query(value = "SELECT sp FROM SanPham sp WHERE " +
//            "(" +
//            "(sp.idCameraSau.ten LIKE %:search% " +
//            "OR sp.idCameraTruoc.ten LIKE %:search% " +
//            "OR sp.idCameraTruoc.ten LIKE %:search% " +
//            "OR sp.idChip.ten LIKE %:search% " +
//            "OR sp.idHang.ten LIKE %:search% " +
//            "OR sp.idHeDieuHanh.ten LIKE %:search% " +
//            "OR sp.idManHinh.ten LIKE %:search% " +
//            "OR sp.idPin.ten LIKE %:search% " +
//            "OR sp.idSim.ten LIKE %:search% " +
//            "OR sp.maSanPham LIKE %:search% " +
//            "OR sp.tenSanPham LIKE %:search%) " +
//            "AND (:status IS NULL OR sp.trangThai =:status)) " +
//            "ORDER BY sp.id DESC")
//    Page<SanPham> search(@Param("idHang") String idHang,@Param("idHeDieuHanh") String idHeDieuHanh,@Param("idManHinh") String idManHinh, @Param("search") String search,
//                         @Param("status") Integer status,
//                         Pageable pageable);

}
