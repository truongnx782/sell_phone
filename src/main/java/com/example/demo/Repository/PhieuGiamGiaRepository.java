package com.example.demo.Repository;

import com.example.demo.Entitys.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, BigInteger> {

    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = 0 or p.trangThai = 1")
    List<PhieuGiamGia> findPhieuGiamGiasByTrangThai0And1();

    @Query("SELECT COUNT(p) FROM PhieuGiamGia p")
    Long countAllPhieuGiamGia();

    @Query("SELECT p FROM PhieuGiamGia p WHERE" +
            " p.giaTriToiThieuApDung <= :totalAmount AND p.trangThai = 1 AND p.trangThaiCongKhai = 0")
    List<PhieuGiamGia> findValidVouchers(@Param("totalAmount") BigDecimal totalAmount);

    @Query("SELECT p FROM PhieuGiamGia p WHERE p.giaTriToiThieuApDung <= :totalAmount" +
            " AND p.trangThai = 1 AND (" +
            "(p.trangThaiCongKhai = 0) " + // Áp dụng cho tất cả khách hàng
            "OR (p.trangThaiCongKhai = 1 AND EXISTS (SELECT pk FROM PhieuGG_KH pk " +
            "WHERE pk.khachHangID = :khachHangId AND pk.voucherID = p.id)))")
    List<PhieuGiamGia> findValidVouchersNew(@Param("totalAmount") BigDecimal totalAmount,
                                            @Param("khachHangId") BigInteger khachHangId);



    @Query("SELECT p FROM PhieuGiamGia p WHERE (p.trangThai = 1 AND p.soLuong = 0) or p.ngayKetThuc < :ngayKetThuc")
    List<PhieuGiamGia> findVoucherEnd(@Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc);


    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = 0 AND  p.ngayBatDau <= :ngayBatDau")
    List<PhieuGiamGia> findVoucherStart(@Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau);

    boolean existsByMaPhieuGiamGia(String maPhieuGiamGia);

    boolean existsByGiaTriToiThieuApDung(BigDecimal giaTriGiam);

    boolean existsBySoLuong(Integer soLuong);

    PhieuGiamGia findTopByOrderByMaPhieuGiamGiaDesc();

    // Hàm đầy đủ 4 điều kiện (Giá trị tối thiểu áp dụng)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.giaTriToiThieuApDung = :giaTriToiThieuApDung and p.trangThai = :trangThai and p.ngayBatDau >= :ngayBatDau and p.ngayKetThuc <= :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByGiaTriToiThieuApDungAndTrangThaiAndNgayBatDauAndNgayKetThucBetween(
            @Param("giaTriToiThieuApDung") BigDecimal giaTriToiThieuApDung,
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm Giá trị tối thiểu áp dụng, ko có ngày kết thúc)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.giaTriToiThieuApDung = :giaTriToiThieuApDung and p.trangThai = :trangThai and p.ngayBatDau = :ngayBatDau")
    public Page<PhieuGiamGia> findPhieuGiamGiasByGiaTriToiThieuApDungAndTrangThaiAndNgayBatDau(
            @Param("giaTriToiThieuApDung") BigDecimal giaTriToiThieuApDung,
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            Pageable pageable);
    // Hàm Giá trị tối thiểu áp dụng, ko có ngày bắt đầu)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.giaTriToiThieuApDung = :giaTriToiThieuApDung and p.trangThai = :trangThai and p.ngayKetThuc = :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByGiaTriToiThieuApDungAndTrangThaiAndNgayKetThuc(
            @Param("giaTriToiThieuApDung") BigDecimal giaTriToiThieuApDung,
            @Param("trangThai") Integer trangThai,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm Giá trị tối thiểu áp dụng, ko có ngày bắt đầu và kết thúc)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.giaTriToiThieuApDung = :giaTriToiThieuApDung and p.trangThai = :trangThai")
    public Page<PhieuGiamGia> findPhieuGiamGiasByGiaTriToiThieuApDungAndTrangThai(
            @Param("giaTriToiThieuApDung") BigDecimal giaTriToiThieuApDung,
            @Param("trangThai") Integer trangThai,
            Pageable pageable);
    // Hàm (Giá trị tối thiểu áp dụng, ko có trạng thái)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.giaTriToiThieuApDung = :giaTriToiThieuApDung and p.ngayBatDau >= :ngayBatDau and p.ngayKetThuc <= :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByGiaTriToiThieuApDungAndNgayBatDauAndNgayKetThucBetween(
            @Param("giaTriToiThieuApDung") BigDecimal giaTriToiThieuApDung,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm (Giá trị tối thiểu áp dụng, ko có trạng thái và ngày bắt đầu)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.giaTriToiThieuApDung = :giaTriToiThieuApDung and p.ngayKetThuc = :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByGiaTriToiThieuApDungAndNgayKetThuc(
            @Param("giaTriToiThieuApDung") BigDecimal giaTriToiThieuApDung,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm  (Giá trị tối thiểu áp dụng, ko có trạng thái và ngày kết thúc)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.giaTriToiThieuApDung = :giaTriToiThieuApDung and p.ngayBatDau = :ngayBatDau ")
    public Page<PhieuGiamGia> findPhieuGiamGiasByGiaTriToiThieuApDungAndNgayBatDau(
            @Param("giaTriToiThieuApDung") BigDecimal giaTriToiThieuApDung,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            Pageable pageable);
    //Hàm lấy theo giá trị tối thiểu áp dụng
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.giaTriToiThieuApDung = :giaTriToiThieuApDung ")
    public Page<PhieuGiamGia> findPhieuGiamGiasByGiaTriToiThieuApDung(
            @Param("giaTriToiThieuApDung") BigDecimal giaTriToiThieuApDung, Pageable pageable);

    // Hàm đầy đủ 4 điều kiện (Số lượng)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.soLuong = :soLuong and p.trangThai = :trangThai and p.ngayBatDau >= :ngayBatDau and p.ngayKetThuc <= :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasBySoLuongAndTrangThaiAndNgayBatDauAndNgayKetThucBetween(
            @Param("soLuong") Integer soLuong,
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm Số lượng (ko có ngày kết thúc)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.soLuong = :soLuong and p.trangThai = :trangThai and p.ngayBatDau = :ngayBatDau")
    public Page<PhieuGiamGia> findPhieuGiamGiasBySoLuongAndTrangThaiAndNgayBatDau(
            @Param("soLuong") Integer soLuong,
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            Pageable pageable);
    // Hàm Số lượng (ko có ngày bắt đầu)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.soLuong = :soLuong and p.trangThai = :trangThai and p.ngayKetThuc = :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasBySoLuongAndTrangThaiAndNgayKetThuc(
            @Param("soLuong") Integer soLuong,
            @Param("trangThai") Integer trangThai,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm Số lượng (ko có ngày bắt đầu và kết thúc)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.soLuong = :soLuong and p.trangThai = :trangThai")
    public Page<PhieuGiamGia> findPhieuGiamGiasBySoLuongAndTrangThai(
            @Param("soLuong") Integer soLuong,
            @Param("trangThai") Integer trangThai,
            Pageable pageable);
    // Hàm Số lượng (ko có trạng thái)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.soLuong = :soLuong and p.ngayBatDau >= :ngayBatDau and p.ngayKetThuc <= :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasBySoLuongAndNgayBatDauAndNgayKetThucBetween(
            @Param("soLuong") Integer soLuong,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm Số lượng (ko có trạng thái và ngày bắt đầu)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.soLuong = :soLuong and p.ngayKetThuc = :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasBySoLuongAndNgayKetThuc(
            @Param("soLuong") Integer soLuong,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm Số lượng (ko có trạng thái và ngày kết thúc)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.soLuong = :soLuong and p.ngayBatDau = :ngayBatDau ")
    public Page<PhieuGiamGia> findPhieuGiamGiasBySoLuongAndNgayBatDau(
            @Param("soLuong") Integer soLuong,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            Pageable pageable);
    //Hàm lấy theo số lượng
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.soLuong = :soLuong" )
    public Page<PhieuGiamGia> findPhieuGiamGiasBySoLuong(
            @Param("soLuong") Integer soLuong, Pageable pageable);

    // Hàm lấy đủ 4 điều kiện (Loại giảm giá)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.loaiGiamGia = :loaiGiamGia and p.trangThai = :trangThai and p.ngayBatDau >= :ngayBatDau and p.ngayKetThuc <= :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByLoaiGiamGiaAndTrangThaiAndNgayBatDauAndNgayKetThucBetween(
            @Param("loaiGiamGia") Integer loaiGiamGia,
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm Loại giảm giá, ko có ngày kết thúc
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.loaiGiamGia = :loaiGiamGia and p.trangThai = :trangThai and p.ngayBatDau = :ngayBatDau")
    public Page<PhieuGiamGia> findPhieuGiamGiasByLoaiGiamGiaAndTrangThaiAndNgayBatDau(
            @Param("loaiGiamGia") Integer loaiGiamGia,
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            Pageable pageable);
    // Hàm Loại giảm giá, ko có ngày bắt đầu
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.loaiGiamGia = :loaiGiamGia and p.trangThai = :trangThai and p.ngayKetThuc = :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByLoaiGiamGiaAndTrangThaiAndNgayKetThuc(
            @Param("loaiGiamGia") Integer loaiGiamGia,
            @Param("trangThai") Integer trangThai,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm Loại giảm giá, ko có ngày bắt đầu và kết thúc
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.loaiGiamGia = :loaiGiamGia and p.trangThai = :trangThai")
    public Page<PhieuGiamGia> findPhieuGiamGiasByLoaiGiamGiaAndTrangThai(
            @Param("loaiGiamGia") Integer loaiGiamGia,
            @Param("trangThai") Integer trangThai,
            Pageable pageable);
    // Hàm Loại giảm giá, ko có trạng thái
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.loaiGiamGia = :loaiGiamGia and p.ngayBatDau >= :ngayBatDau and p.ngayKetThuc <= :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByLoaiGiamGiaAndNgayBatDauAndNgayKetThucBetween(
            @Param("loaiGiamGia") Integer loaiGiamGia,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm Loại giảm giá, ko có trạng thái và ngày bắt đầu
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.loaiGiamGia = :loaiGiamGia and p.ngayKetThuc = :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByLoaiGiamGiaAndNgayKetThuc(
            @Param("loaiGiamGia") Integer loaiGiamGia,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm Loại giảm giá, ko có trạng thái và ngày kết thúc
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.loaiGiamGia = :loaiGiamGia and p.ngayBatDau = :ngayBatDau")
    public Page<PhieuGiamGia> findPhieuGiamGiasByLoaiGiamGiaAndNgayBatDau(
            @Param("loaiGiamGia") Integer loaiGiamGia,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            Pageable pageable);
    // Hàm Loại giảm giá
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.loaiGiamGia = :loaiGiamGia" )
    public Page<PhieuGiamGia> findPhieuGiamGiasByLoaiGiamGia(
            @Param("loaiGiamGia") Integer loaiGiamGia, Pageable pageable);

    // Hàm đầy đủ 4 điều kiện (Mã phiếu)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.maPhieuGiamGia LIKE %:maPhieuGiamGia% or p.tenPhieuGiamGia LIKE %:tenPhieuGiamGia%  and p.trangThai = :trangThai and p.ngayBatDau >= :ngayBatDau and p.ngayKetThuc <= :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndTrangThaiAndNgayBatDauAndNgayKetThucBetween(
            @Param("maPhieuGiamGia") String maPhieuGiamGia,
            @Param("tenPhieuGiamGia") String tenPhieuGiamGia,
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm mã, tên phiếu, ko có ngày kết thúc
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.maPhieuGiamGia LIKE %:maPhieuGiamGia% or p.tenPhieuGiamGia LIKE %:tenPhieuGiamGia%  and p.trangThai = :trangThai and p.ngayBatDau = :ngayBatDau")
    public Page<PhieuGiamGia> findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndTrangThaiAndNgayBatDau(
            @Param("maPhieuGiamGia") String maPhieuGiamGia,
            @Param("tenPhieuGiamGia") String tenPhieuGiamGia,
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            Pageable pageable);
    // Hàm mã, tên phiếu, ko có ngày bắt đầu
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.maPhieuGiamGia LIKE %:maPhieuGiamGia% or p.tenPhieuGiamGia LIKE %:tenPhieuGiamGia%  and p.trangThai = :trangThai and p.ngayKetThuc = :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndTrangThaiAndNgayKetThuc(
            @Param("maPhieuGiamGia") String maPhieuGiamGia,
            @Param("tenPhieuGiamGia") String tenPhieuGiamGia,
            @Param("trangThai") Integer trangThai,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm mã, tên phiếu, ko có ngày bắt đầu và ngày kết thúc
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.maPhieuGiamGia LIKE %:maPhieuGiamGia% or p.tenPhieuGiamGia LIKE %:tenPhieuGiamGia%  and p.trangThai = :trangThai")
    public Page<PhieuGiamGia> findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndTrangThai(
            @Param("maPhieuGiamGia") String maPhieuGiamGia,
            @Param("tenPhieuGiamGia") String tenPhieuGiamGia,
            @Param("trangThai") Integer trangThai,
            Pageable pageable);
    // Hàm mã, tên phiếu, ko có trạng thái
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.maPhieuGiamGia LIKE %:maPhieuGiamGia% or p.tenPhieuGiamGia LIKE %:tenPhieuGiamGia%  and p.ngayBatDau >= :ngayBatDau and p.ngayKetThuc <= :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndNgayBatDauAndNgayKetThucBetween(
            @Param("maPhieuGiamGia") String maPhieuGiamGia,
            @Param("tenPhieuGiamGia") String tenPhieuGiamGia,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm mã, tên phiếu, ko có trạng thái và ngày bắt đầu
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.maPhieuGiamGia LIKE %:maPhieuGiamGia% or p.tenPhieuGiamGia LIKE %:tenPhieuGiamGia%  and p.ngayKetThuc = :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndNgayKetThuc(
            @Param("maPhieuGiamGia") String maPhieuGiamGia,
            @Param("tenPhieuGiamGia") String tenPhieuGiamGia,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    // Hàm mã, tên phiếu, ko có trạng thái và ngày kết thúc
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.maPhieuGiamGia LIKE %:maPhieuGiamGia% or p.tenPhieuGiamGia LIKE %:tenPhieuGiamGia%  and p.ngayBatDau = :ngayBatDau ")
    public Page<PhieuGiamGia> findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndNgayBatDau(
            @Param("maPhieuGiamGia") String maPhieuGiamGia,
            @Param("tenPhieuGiamGia") String tenPhieuGiamGia,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            Pageable pageable);
    // Hàm mã, tên phiếu
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.maPhieuGiamGia LIKE %:maPhieuGiamGia% or p.tenPhieuGiamGia LIKE %:tenPhieuGiamGia% ")
    public Page<PhieuGiamGia> findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGia(
            @Param("maPhieuGiamGia") String maPhieuGiamGia,
            @Param("tenPhieuGiamGia") String tenPhieuGiamGia,
            Pageable pageable);

    //Hàm trạng thái, ko có input
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = :trangThai and p.ngayBatDau >= :ngayBatDau and p.ngayKetThuc <= :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByTrangThaiAndNgayBatDauAndNgayKetThucBetween(
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    //Hàm trạng thái, ko có input và ngày bắt dầu
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = :trangThai and p.ngayKetThuc = :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByTrangThaiAndNgayKetThuc(
            @Param("trangThai") Integer trangThai,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);
    //Hàm trạng thái, ko có input và ngày kết thúc
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = :trangThai and p.ngayBatDau >= :ngayBatDau")
    public Page<PhieuGiamGia> findPhieuGiamGiasByTrangThaiAndNgayBatDau(
            @Param("trangThai") Integer trangThai,
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            Pageable pageable);
    //Hàm trạng thái
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = :trangThai")
    public Page<PhieuGiamGia> findPhieuGiamGiasByTrangThai(
            @Param("trangThai") Integer trangThai,Pageable pageable);

    //Hàm lấy theo khoảng ngày bắt đầu và ngày kết thúc
    @Query("SELECT p FROM PhieuGiamGia p WHERE  p.ngayBatDau >= :ngayBatDau and p.ngayKetThuc <= :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByNgayBatDauAndNgayKetThucBetween(
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);

    //Hàm lấy theo ngày bắt đầu
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.ngayBatDau = :ngayBatDau")
    public Page<PhieuGiamGia> findPhieuGiamGiasByNgayBatDau(
            @Param("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayBatDau,
            Pageable pageable);

    //Hàm lấy theo ngày kết thúc
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.ngayKetThuc = :ngayKetThuc")
    public Page<PhieuGiamGia> findPhieuGiamGiasByNgayKetThuc(
            @Param("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Timestamp ngayKetThuc,
            Pageable pageable);

}
