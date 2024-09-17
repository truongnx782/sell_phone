package com.example.demo.Repository;

import com.example.demo.Entitys.DiaChi;
import com.example.demo.Entitys.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, BigInteger> {
    @Query(value = "select dc from DiaChi dc  where dc.idKhachHang=:id and  dc.trangThai=:status   order by dc.id desc ")
    List<DiaChi> findAllBy(@Param("id") BigInteger id,@Param("status") Integer status);

    @Query(value = "select dc from DiaChi dc  where dc.idKhachHang=:id and ( dc.trangThai=:status or dc.trangThai=:status2  ) order by dc.id desc ")
    List<DiaChi> findAllBy(@Param("id") BigInteger id,@Param("status") Integer status,@Param("status2") Integer status2);

    @Query(value = "select dc from DiaChi dc  where dc.trangThai=:status  order by dc.id desc ")
    List<DiaChi> findAllBy(@Param("status") Integer status);
    Optional<DiaChi> findByIdKhachHangAndTrangThai(BigInteger idKhachHang, Integer trangThai);

    @Query(value = "select dc from DiaChi dc  where dc.idKhachHang=:id and  dc.trangThai=3 order by  dc.id asc ")
    List<DiaChi> findByIdKhachHangAndTrangThai3(@Param("id") BigInteger id);

    @Query(value = "select dc from DiaChi dc  where dc.idKhachHang=:id and  (dc.trangThai=3 or dc.trangThai=1) order by  dc.id asc ")
    List<DiaChi> findByIdKhachHangAndTrangThai1OrTrangThai3(@Param("id") BigInteger id);

    @Query(value = "select dc from DiaChi dc  where dc.idKhachHang=:khachHangID and  dc.diaChiCuThe=:diaChiCuThe order by  dc.id asc ")
    List<DiaChi> findByDiaChiCuTheAndKhachHangId(@Param("diaChiCuThe")String diaChiCuThe, @Param("khachHangID")BigInteger khachHangID);
}
