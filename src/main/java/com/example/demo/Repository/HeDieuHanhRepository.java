package com.example.demo.Repository;

import com.example.demo.Entitys.HeDieuHanh;
import com.example.demo.Entitys.ManHinh;
import com.example.demo.Entitys.Pin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Repository
@Transactional
public interface HeDieuHanhRepository extends JpaRepository<HeDieuHanh, BigInteger> {

    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;


    public Page<HeDieuHanh> findAllByTrangThai(Pageable pageable, int trangThai);

    @Query(value = "select h from  HeDieuHanh h where h.trangThai = :trangThai order by h.id desc ")
    public List<HeDieuHanh> findByTrangThai(@Param("trangThai") int trangThai);

    public HeDieuHanh findByTenIsLike(String ten);

    @Modifying
    @Query(value = "UPDATE HeDieuHanh SET trangThai = :trangThai WHERE id = :id")
    void changeStatus(@Param("trangThai") int trangThai, @Param("id") int id);

    @Query(value = "SELECT hdh FROM HeDieuHanh hdh WHERE hdh.ten LIKE %:ten% ORDER BY hdh.id DESC")
    Page<HeDieuHanh> search(@Param("ten") String ten, Pageable pageable);


    @Query(value = "SELECT p FROM HeDieuHanh p WHERE p.trangThai=1 ORDER BY p.id DESC")
    Page<HeDieuHanh> findAll(Pageable pageable);

}
