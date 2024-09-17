package com.example.demo.Repository;

import com.example.demo.Entitys.Hang;

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
public interface HangRepository extends JpaRepository<Hang, BigInteger> {
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    @Query(value = "select h from  Hang h order by h.id desc ")
    public Page<Hang> findAll(Pageable pageable);

    public Page<Hang> findAllByTrangThai(Pageable pageable, int trangThai);

    @Query(value = "select h from  Hang h where h.trangThai=1 order by h.id desc ")
    public List<Hang> findByTrangThai(int trangThai);

    public Hang findByTenIsLike(String ten);

    @Modifying
    @Query(value = "UPDATE Hang SET trangThai = :trangThai WHERE id = :id")
    void changeStatus(@Param("trangThai") int trangThai, @Param("id") BigInteger id);

    @Query(value = "SELECT hang FROM Hang hang WHERE hang.ten LIKE %:ten%")
    Page<Hang> search(@Param("ten") String ten, Pageable pageable);

    @Query(value = "SELECT p FROM Hang p WHERE p.trangThai=:Status ORDER BY p.id DESC")
    List<Hang> findAllBy(@Param("Status") int i);

}
