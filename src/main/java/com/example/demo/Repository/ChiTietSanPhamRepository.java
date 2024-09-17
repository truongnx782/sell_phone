package com.example.demo.Repository;

import com.example.demo.Entitys.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, BigInteger> {


    public static final int ACTIVE = 1;

    public static final int INACTIVE = 0;

    public List<ChiTietSanPham> findByTrangThai(int trangThai);

    @Query(value = "select ctsp from ChiTietSanPham ctsp where ctsp.idSanPham.id = :idSP order by ctsp.id desc")
    Page<ChiTietSanPham> findByIdSanPham(Pageable pageable, @Param("idSP") BigInteger idSP);

    Page<ChiTietSanPham> findTop10ByOrderByIdDesc(Pageable pageable);

    public void deleteByIdSanPhamAndIdRAMAndIdROMAndIdMauSac(SanPham idSP, RAM idRAM, ROM idROM, MauSac idMauSac);

    @Query(value = "SELECT * FROM ChiTietSanPham ctsp WHERE (ctsp.IDMauSac IN (SELECT ID FROM MauSac ms WHERE ms.TenMauSac LIKE %:param%))\n" +
            "OR (ctsp.IDRAM IN (SELECT ID FROM RAM ram WHERE ram.Ten LIKE  %:param%))\n" +
            "OR (ctsp.IDROM IN (SELECT ID FROM ROM rom WHERE rom.TenRom LIKE  %:param%))" +
            "OR (ctsp.IDSanPham IN (SELECT ID FROM SanPham sp WHERE sp.TenSanPham LIKE  %:param%))" +
            "OR (ctsp.IDSanPham IN (SELECT ID FROM SanPham WHERE IDChip IN (SELECT ID FROM Chip WHERE Ten LIKE %:param%)))" +
            "OR (ctsp.IDSanPham IN (SELECT ID FROM SanPham WHERE IDPin IN (SELECT ID FROM Pin WHERE Ten LIKE %:param%)))" +
            "OR (ctsp.IDSanPham IN (SELECT ID FROM SanPham WHERE IDSim IN (SELECT ID FROM Sim WHERE Ten LIKE %:param%)))" +
            "OR (ctsp.IDSanPham IN (SELECT ID FROM SanPham WHERE IDCameraSau IN (SELECT ID FROM CameraSau WHERE Ten LIKE %:param%)))" +
            "OR (ctsp.IDSanPham IN (SELECT ID FROM SanPham WHERE IDCameraTruoc IN (SELECT ID FROM CameraTruoc WHERE Ten LIKE %:param%)))" +
            "OR (ctsp.IDSanPham IN (SELECT ID FROM SanPham WHERE IDHang IN (SELECT ID FROM Hang WHERE Ten LIKE %:param%)))" +
            "OR (ctsp.IDSanPham IN (SELECT ID FROM SanPham WHERE IDManHinh IN (SELECT ID FROM ManHinh WHERE Ten LIKE %:param%)))" +
            "OR (ctsp.IDSanPham IN (SELECT ID FROM SanPham WHERE IDHeDieuHanh IN (SELECT ID FROM HeDieuHanh WHERE Ten LIKE %:param%)))", nativeQuery = true)
    public Page<ChiTietSanPham> search(Pageable pageable, @Param("param") String param);

    public ChiTietSanPham findByIdRAMAndIdROMAndIdMauSacAndIdSanPham(RAM ram, ROM rom, MauSac mauSac, SanPham sanPham);

    @Query(value = "SELECT nv FROM ChiTietSanPham nv WHERE nv.id = (SELECT MAX(nv2.id) FROM ChiTietSanPham nv2)")
    Optional<ChiTietSanPham> findMaId();


    @Query("SELECT c FROM ChiTietSanPham c WHERE c.id IN (SELECT i.idChiTietSP.id FROM Imei i WHERE i.maImei IN :imeiList)")
    List<ChiTietSanPham> findByImeiList(@Param("imeiList") List<String> imeiList);

    @Query(value = "SELECT sp FROM ChiTietSanPham sp WHERE " +
            "sp.idSanPham.id=:idSP AND (sp.maChiTietSanPham LIKE %:search%) " +
            "AND (:status IS NULL OR sp.trangThai = :status) " +
            "AND (:idMauSac IS NULL OR sp.idMauSac.id = :idMauSac) " +
            "AND (:idRom IS NULL OR sp.idROM.id = :idRom) " +
            "AND (:idRam IS NULL OR sp.idRAM.id = :idRam) " +
            "ORDER BY sp.id DESC")
    Page<ChiTietSanPham> searchChiTietSanPham(
            @Param("idSP") BigInteger idSanPham,
            @Param("idMauSac") BigInteger idMauSac,
            @Param("idRom") BigInteger idRom,
            @Param("idRam") BigInteger idRam,
            @Param("search") String search,
            @Param("status") Integer status,
            Pageable pageable);

// Get sản phẩm add giỏ hàng

    @Query("SELECT c FROM ChiTietSanPham c WHERE c.idSanPham.id = :idSanPham and c.idROM.id = :idRom and c.idMauSac.id = :idMauSac")
    ChiTietSanPham getChiTietSanPhamByIdSanPhamAndIdROMAndIdMauSac
            (@Param("idSanPham") BigInteger idSanPham,
             @Param("idRom") BigInteger idRom,
             @Param("idMauSac") BigInteger idMauSac);
}

