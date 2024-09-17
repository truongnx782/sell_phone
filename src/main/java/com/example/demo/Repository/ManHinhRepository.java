package com.example.demo.Repository;

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
public interface ManHinhRepository extends JpaRepository<ManHinh, BigInteger> {

    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    @Query(value = "select h from  ManHinh h order by h.id desc ")
    public Page<ManHinh> findAll(Pageable pageable);

    @Query(value = "select h from  ManHinh h order by h.id desc ")
    List<ManHinh> findAllByOrderByIdDesc();

    public Page<ManHinh> findAllByTrangThai(Pageable pageable, int trangThai);

    @Query(value = "select h from  ManHinh h where h.trangThai=1 order by h.id desc ")
    public List<ManHinh> findByTrangThai(int trangThai);

    public ManHinh findByTenIsLike(String ten);

    @Modifying
    @Query(value = "UPDATE ManHinh SET trangThai = :trangThai WHERE id = :id")
    void changeStatus(@Param("trangThai") int trangThai, @Param("id") BigInteger id);

    @Query(value = "SELECT manHinh FROM ManHinh manHinh WHERE manHinh.ten LIKE %:ten%")
    Page<ManHinh> search(@Param("ten") String ten, Pageable pageable);

    @Query(value = "SELECT p FROM ManHinh p WHERE p.trangThai=:Status ORDER BY p.id DESC")
    List<ManHinh> findAllBy(@Param("Status") int i);

}
