package com.example.demo.Repository;

import com.example.demo.Entitys.MauSac;
import com.example.demo.Entitys.ROM;
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
public interface MauSacRepository extends JpaRepository<MauSac, BigInteger> {

    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    @Query(value = "select h from  MauSac h  order by h.id desc ")
    public Page<MauSac> findAll(Pageable pageable);

    @Query(value = "select h from  MauSac h where h.trangThai=1 order by h.id desc ")
    public Page<MauSac> findAllByTrangThai(Pageable pageable, int trangThai);

    public List<MauSac> findByTrangThai(int trangThai);

    public MauSac findByTenMauSacIsLike(String ten);

    public MauSac findByMaMauSacIsLike(String ma);

    @Modifying
    @Query(value = "UPDATE MauSac SET trangThai = :trangThai WHERE id = :id")
    void changeStatus(@Param("trangThai") int trangThai, @Param("id") BigInteger id);

    @Query(value = "SELECT mauSac FROM MauSac mauSac WHERE mauSac.tenMauSac LIKE %:ten%")
    Page<MauSac> search(@Param("ten") String ten, Pageable pageable);



}
