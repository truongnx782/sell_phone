package com.example.demo.Repository;

import java.math.BigDecimal;

public interface ThongKeInterface {
    Integer getYear();
    Integer getMonth();
    BigDecimal getDoanhThu();
    BigDecimal getDoanhThuHang();
    String getTenHang();
    String getTenLoaiHoaDon();
    Integer getTrangThaiHoaDon();
    Integer getSoHoaDon();


}
