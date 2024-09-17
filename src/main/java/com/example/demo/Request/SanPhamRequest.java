package com.example.demo.Request;

import com.example.demo.Entitys.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamRequest {
    private BigInteger id;
    private String maSanPham;
    @NotEmpty(message = "Tên sản phẩm không được trống")
    private String tenSanPham;
    @NotNull(message = "Hệ điều hành không được bỏ trống")
    private BigInteger idHeDieuHanh;
    @NotNull(message = "Màn hình không được bỏ trống")
    private BigInteger idManHinh;
    @NotNull(message = "Hãng không được bỏ trống")
    private BigInteger idHang;
    @NotNull(message = "Camera trước không được bỏ trống")
    private BigInteger idCameraTruoc;
    @NotEmpty(message = "Camera sau không được bỏ trống")
    private List< BigInteger> idCameraSau;
    @NotNull(message = "Camera chính sau không được bỏ trống")
    private BigInteger idCameraChinhSau;
    @NotNull(message = "Sim không được bỏ trống")
    private BigInteger idSim;
    @NotNull(message = "Pinkhông được bỏ trống")
    private BigInteger idPin;
    @NotNull(message = "Chip không được bỏ trống")
    private BigInteger idChip;
    private Date ngayTao;
    private Date ngaySua;
    private String nguoiTao;
    private String nguoiSua;
    private Integer trangThai;
}
