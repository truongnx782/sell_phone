package com.example.demo.Beans;

import com.example.demo.Repository.SkipFutureOrPresent;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Component;
//
//import javax.validation.constraints.FutureOrPresent;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.PositiveOrZero;
//import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class PhieuGiamGiaBean {

    private BigInteger id;

    private String maPhieuGiamGia;


    @Size(min = 3, max = 255, message = "Độ dài tên từ 3-255 ký tự!")
    private String tenPhieuGiamGia;

    @NotNull(message = "Không để trống!")
    @PositiveOrZero(message = "Giá trị phải > 0")
//    @Digits(integer = 10, fraction = 2, message = "Giá trị phải là số!")
    private BigDecimal giaTriToiThieuApDung;

    @Size(min = 3, max = 255, message = "Độ dài mô tả từ 3-255 ký tự!")
    private String moTa;

    @PositiveOrZero(message = "Số lượng phải >= 0")
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Integer soLuong;

    @PositiveOrZero(message = "Phần trăm giảm phải >= 0")
    private Integer phanTramGiam;

    @PositiveOrZero(message = "Số tiền được giảm tối đa >= 0")
    private BigDecimal soTienDuocGiamToiDa;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "Chọn ngày bắt đầu!")
    @FutureOrPresent(message = "Ngày bắt đầu không hợp lệ!")
    private Date ngayBatDau;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "Chọn ngày kết thúc!")
    private Date ngayKetThuc;

    @PositiveOrZero(message = "Số tiền được giảm >= 0")
    private BigDecimal soTienDuocGiam;

    @NotNull(message = "Chọn loại giảm giá!")
    private Integer loaiGiamGia;

    private Date ngayTao;


    private Date ngaySua;


    private String nguoiTao;


    private String nguoiSua;


    private Integer trangThai;

    private Integer trangThaiSoLuong;

    private Integer trangThaiCongKhai;

    public boolean checkSoTienDuocGiam() {
        if(loaiGiamGia == 1 && (soTienDuocGiam == null || soTienDuocGiam.equals(""))){
            return false;
        }
            phanTramGiam = 0;
            soTienDuocGiamToiDa = new BigDecimal(0);
        return true;
    }

    public boolean checkPhanTramGiam() {
        if(loaiGiamGia == 0 && (phanTramGiam == null || phanTramGiam.equals(""))){
            return false;
        }
        soTienDuocGiam = new BigDecimal(0);
        return true;

    }

    public boolean checkSoTienDuocGiamToiDa() {
        if( loaiGiamGia == 0 && (soTienDuocGiamToiDa == null || soTienDuocGiamToiDa.equals(""))){
            return false;
        }
        return true;
    }
    public boolean isNgayKetThucSauNgayBatDau() {
        if (ngayKetThuc == null || ngayBatDau == null) {
            return true;
        }
        return ngayKetThuc.after(ngayBatDau);
    }

    public boolean isSoTienDuocGiamVaGiaTriToiThieu() {
        if (soTienDuocGiamToiDa == null || giaTriToiThieuApDung == null) {
            return true;
        }else {
            if(loaiGiamGia == 0 && (soTienDuocGiamToiDa.compareTo(giaTriToiThieuApDung) > 0 )){
                return false;
            }
        }
            return true;

    }

    public boolean checkSoLuong() {
        if(trangThaiSoLuong == 0 && (soLuong == null || soLuong.equals("") )){
            return false;
        }
            return true;

    }

}
