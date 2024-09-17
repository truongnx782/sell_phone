package com.example.demo.Repository;

import com.example.demo.Entitys.HoaDon;
import com.example.demo.Entitys.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, BigInteger> {
    public static final int HD_CHO_XAC_NHAN = 1;
    public static final int HD_HOAN_THANH = 4;
    public static final int HD_DA_HUY = 5;
    public static final int HD_NEW = 0;
    public static final int HD_CHO_GIAO_HANG = 2;
    public static final int HD_DANG_GIAO = 3;
    Page<HoaDon> findAllByNgayTaoBetween(Date fromDate,Date toDate, Pageable pageable);
    Page<HoaDon> findAllByLoaiHoaDonAndTrangThai(int loaiHD,int trangThai, Pageable pageable);
    Page<HoaDon> findAllByTrangThai(int trangThai, Pageable pageable);
    @Query("SELECT hd FROM HoaDon hd JOIN hd.idKhachHang kh WHERE hd.maHD LIKE %:keyword% OR kh.sdt LIKE %:keyword%")
    Page<HoaDon> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT hd FROM HoaDon hd JOIN hd.idKhachHang kh WHERE hd.trangThai = :trangThai " +
            "AND (hd.maHD LIKE %:keyword% OR kh.sdt LIKE %:keyword%)")
    Page<HoaDon> searchByKeywordAndTrangThai(@Param("keyword") String keyword,
                                             @Param("trangThai") int trangThai,
                                             Pageable pageable);
    @Query("SELECT hd FROM HoaDon hd JOIN hd.idKhachHang kh WHERE hd.trangThai = :trangThai " +
            "AND hd.loaiHoaDon =:loaiHoaDon AND (hd.maHD LIKE %:keyword% OR kh.sdt LIKE %:keyword%)")
    Page<HoaDon> searchByKeywordAndLoaiHoaDonAndTrangThai(@Param("keyword") String keyword, @Param("keyword") int loaiHD,
                                             @Param("trangThai") int trangThai,
                                             Pageable pageable);

    List<HoaDon> findAllByIdKhachHang(KhachHang id);
    List<HoaDon> findAllByTrangThai(int trangThai);
    long countByTrangThai(int trangThai);
    Page<HoaDon> findAllByTrangThaiNot(int trangThai, Pageable pageable);

// Doanh thu theo tháng
    @Query(value = "SELECT YEAR(NgayTao) AS year, MONTH(NgayTao) AS month, SUM(TongTienSauGiam) AS doanhThu " +
            "FROM hoadon " +
            "WHERE YEAR(NgayTao) = YEAR(GETDATE())  and trangThai <> 0 and trangThai <> 5" +
            "GROUP BY YEAR(NgayTao), MONTH(NgayTao) " +
            "ORDER BY YEAR(NgayTao), MONTH(NgayTao)", nativeQuery = true)
    List<ThongKeInterface> findCurrentYearMonthlyDoanhThu();

    // Doanh thu theo hãng
    @Query("SELECT h.ten AS tenHang, SUM(hdct.tongTien) AS doanhThuHang " +
            "FROM HoaDon hd " +
            "JOIN hd.hoaDonChiTietList hdct " +
            "JOIN hdct.idChiTietSP ctsp " +
            "JOIN ctsp.idSanPham sp " +
            "JOIN sp.idHang h " +
            "WHERE FUNCTION('MONTH', hd.ngayTao) = :thang and  hd.trangThai <> 0 and hd.trangThai <> 5" +
            "GROUP BY h.ten " +
            "ORDER BY doanhThuHang DESC")
    List<ThongKeInterface> getDoanhThuByHang(@Param("thang") int thang);

    // Tỷ lệ bán online và offline
    @Query("SELECT " +
            "CASE h.loaiHoaDon " +
            "    WHEN 1 THEN 'Bán offline' " +
            "    WHEN 2 THEN 'Bán online' " +
            "    ELSE 'Khác' " +
            "END AS tenLoaiHoaDon, " +
            "COUNT(h.loaiHoaDon) AS soHoaDon " +
            "FROM HoaDon h where h.trangThai <> 0 and h.trangThai <> 5" +
            "GROUP BY h.loaiHoaDon " +
            "ORDER BY h.loaiHoaDon")
    List<ThongKeInterface> countLoaiHoaDon();

    // Thống kê số hóa đơn theo trạng thái
    @Query("SELECT h.trangThai AS trangThaiHoaDon, COUNT(h.trangThai) AS soHoaDon, SUM(h.tongTienSauGiam) AS doanhThu " +
            "FROM HoaDon h where h.trangThai <> 0 GROUP BY h.trangThai ORDER BY h.trangThai")
    List<ThongKeInterface> countHoaDonByTrangThai();

//    Thống kê trong ngày
    @Query(value = "SELECT nv.MaNhanVien AS maNhanVien, nv.TenNhanVien AS tenNhanVien, SUM(hd.TongTienSauGiam) AS doanhThuNhanVien, COUNT(hd.ID) AS soHoaDonTheoNhanVien " +
            "FROM hoadon hd " +
            "JOIN NhanVien nv ON hd.IDNhanVien = nv.ID " +
            "WHERE hd.TrangThai != 0 AND hd.TrangThai != 5 AND CAST(hd.NgayTao AS DATE) = CAST(GETDATE() AS DATE) " +
            "GROUP BY nv.MaNhanVien, nv.TenNhanVien "+
            "ORDER BY doanhThuNhanVien DESC",
            nativeQuery = true)
    List<DoanhThuTrongNgay> findDoanhThuOnToday();

    @Query(value = "SELECT COUNT(hd.ID) AS hoaDonTaiQuay " +
            "FROM hoadon hd " +
            "WHERE hd.loaiHoaDon = 1 AND CAST(hd.NgayTao AS DATE) = CAST(GETDATE() AS DATE) ",
            nativeQuery = true)
    Integer getHoaDonTaiQuay();

    @Query(value = "SELECT COUNT(hd.ID) AS hoaDonOnline " +
            "FROM hoadon hd " +
            "WHERE hd.loaiHoaDon = 2 AND CAST(hd.NgayTao AS DATE) = CAST(GETDATE() AS DATE) ",
            nativeQuery = true)
    Integer getHoaDonOnline();

//    Top 5 sản phẩm bán chạy
    @Query("SELECT sp.tenSanPham AS tenSanPham, ctsp.idROM.tenRom AS tenRom, COUNT(hdct.idChiTietSP) AS soLuong " +
            "FROM HoaDon hd " +
            "JOIN hd.hoaDonChiTietList hdct " +
            "JOIN hdct.idChiTietSP ctsp " +
            "JOIN ctsp.idSanPham sp " +
            "GROUP BY sp.tenSanPham, ctsp.idROM " +
            "ORDER BY soLuong DESC")
    List<SanPhamThongke> findTop5Products();


    //    Thống kê trong ngày
    @Query(value = "SELECT ct.IDHinhThuc as IDHinhThuc " +
            "FROM HoaDon hd " +
            "JOIN ChiTietPhuongThucTT ct ON hd.ID = ct.IDHoaDon " +
            "WHERE hd.ID = :id AND ct.IDHinhThuc = 3 ",
            nativeQuery = true)
    BigInteger getIDHinhThucThanhToan(@Param("id") BigInteger id);


//    @Query("SELECT hd FROM HoaDon hd  WHERE hd.idKhachHang.id = :idKhachHang")
//    List<HoaDon> getDonHang(@Param("idKhachHang") BigInteger idKhachHang);

    @Query("SELECT sp.tenSanPham AS tenSanPham, hd.maHD AS maHD,  hd.tongTienSauGiam AS tongTienSauGiam, hd.ngayTao AS ngayTao,  hd.trangThai AS trangThai " +
            "FROM HoaDon hd " +
            "JOIN hd.hoaDonChiTietList hdct " +
            "JOIN hdct.idChiTietSP ctsp " +
            "JOIN ctsp.idSanPham sp where hd.idKhachHang.id = :idKhachHang ORDER BY hd.ngayTao DESC")
    List<DanhSachDonHang> getDonHang(@Param("idKhachHang") BigInteger idKhachHang);


}

