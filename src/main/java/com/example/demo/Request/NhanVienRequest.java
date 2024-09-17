package com.example.demo.Request;

//import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Past;
import java.math.BigInteger;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienRequest {

    private Integer id;
    @NotBlank(message = "Tên nhân viên không được để trống")
    private  String tenNhanVien;
    @NotBlank(message = "SDT không được trống")
    private String sdt;
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;
    @NotBlank(message = "CCCD nhân viên không được để trống")
    private  String CCCD;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Ngày sinh không được bỏ trống")
    @Past(message = "Ngày sinh phải là một ngày đã qua")
    private Date ngaySinh;
    @NotBlank(message = "Địa chỉ cụ thể không được để trống")
    private String diaChiCuThe;
    @NotBlank(message = "Phường không được để trống")
    private String phuong;
    @NotBlank(message = "Quận không được để trống")
    private String quan;
    @NotBlank(message = "Tỉnh không được để trống")
    private String tinh;
    private  String ghiChu;
    private String anhNhanVien;
    @NotNull(message = "Giới tính không được bỏ trống")
    private Boolean gioiTinh;
    private BigInteger idChucVu;
    private Boolean isSources=false;




}
