package com.example.demo.Repository;

import com.example.demo.Entitys.ChucVu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, BigInteger> {
     ChucVu findByTenChucVuIsLike(String ten);

    @Query(value = "SELECT cv FROM ChucVu cv WHERE cv.id = (SELECT MAX(cv2.id) FROM ChucVu cv2)")
    Optional<ChucVu> findMaId();

    @Modifying
    @Query(value = "UPDATE ChucVu SET trangThai = :trangThai WHERE id = :id")
    void changeStatus(@Param("trangThai") int trangThai, @Param("id") BigInteger id);

    @Query(value = "SELECT chucvu FROM ChucVu chucvu WHERE chucvu.tenChucVu LIKE %:ten%")
    Page<ChucVu> search(@Param("ten") String ten, Pageable pageable);

    List<ChucVu> findAllByTrangThai(int i);

    Optional<ChucVu> findFirstByTenChucVuIsLike(String tenChucVu);
}
