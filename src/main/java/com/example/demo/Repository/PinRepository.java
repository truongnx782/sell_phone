package com.example.demo.Repository;

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
public interface PinRepository extends JpaRepository<Pin, BigInteger> {

    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    @Query(value = "select h from  Pin h  order by h.id desc ")
    public Page<Pin> findAll(Pageable pageable);

    @Query(value = "select h from  Pin h where h.trangThai=1 order by h.id desc ")
    public Page<Pin> findAllByTrangThai(Pageable pageable, int trangThai);

    public List<Pin> findByTrangThai(int trangThai);

    public Pin findByTenIsLike(String ten);


    @Modifying
    @Query(value = "UPDATE Pin SET trangThai = :trangThai WHERE id = :id")
    void changeStatus(@Param("trangThai") int trangThai, @Param("id") BigInteger id);

    @Query(value = "SELECT pin FROM Pin pin WHERE pin.ten LIKE %:ten%")
    Page<Pin> search(@Param("ten") String ten, Pageable pageable);

    @Query(value = "SELECT p FROM Pin p WHERE p.trangThai=:Status ORDER BY p.id DESC")
    List<Pin> findAllBy(@Param("Status") int i);

}
