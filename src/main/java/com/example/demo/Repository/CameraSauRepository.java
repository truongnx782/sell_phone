package com.example.demo.Repository;

import com.example.demo.Entitys.CameraSau;
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
public interface CameraSauRepository extends JpaRepository<CameraSau, BigInteger> {

    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    @Query(value = "select h from  CameraSau h order by h.id desc ")
    public Page<CameraSau> findAll(Pageable pageable);

    public Page<CameraSau> findAllByTrangThai(Pageable pageable, int trangThai);

    @Query(value = "select h from  CameraSau h where h.trangThai=1 order by h.id desc ")
    public List<CameraSau> findByTrangThai(int trangThai);

    public CameraSau findByTenIsLike(String ten);

    @Modifying
    @Query(value = "UPDATE CameraSau SET trangThai = :trangThai WHERE id = :id")
    void changeStatus(@Param("trangThai") int trangThai, @Param("id") BigInteger id);

    @Query(value = "SELECT camSau FROM CameraSau camSau WHERE camSau.ten LIKE %:ten%")
    Page<CameraSau> search(@Param("ten") String ten, Pageable pageable);

}
