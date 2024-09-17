package com.example.demo.Repository;

import com.example.demo.Entitys.HoaDon;
import com.example.demo.Entitys.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, BigInteger> {
    public static final int CONFIRM = 2;
    public static final int SHIP = 3;
    public static final int COMPLETE = 4;
    public static final int UNDO = 5;
    public static final int CANCEL = 6;
    public static final int EDIT = 7;
    List<LichSuHoaDon> findAllByIdHoaDonOrderByThoiGianDesc(HoaDon hd);
    List<LichSuHoaDon> findAllByIdHoaDon(HoaDon id);
    LichSuHoaDon findFirstByIdHoaDonOrderByThoiGianDesc(HoaDon hd);

}
