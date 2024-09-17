package com.example.demo.Repository;

import com.example.demo.Entitys.Image;
import com.example.demo.Entitys.SanPham;
import com.example.demo.Entitys.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository

public interface ImageRepository extends JpaRepository<Image, BigInteger> {

    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    public List<Image> findByTrangThai(int trangThai);

    @Query(value = "select i from Image i where i.sanPhamId = :idSP order by i.id desc ")
    Image findTop1BySanPhamId(@Param("idSP") BigInteger idSP);

    @Query(value = "select i from Image  i where i.sanPhamId.id=:id")
    List<Image> findAllBySanPhamId(@Param("id") BigInteger id);

    @Query("select i from Image i where i.mauSacId is null and i.sanPhamId.id = :id")
    List<Image> findAllAnhChinhBySanPhamId(@Param("id") BigInteger id);

    @Query(value = "select DISTINCT  i.tenAnh from Image i where i.sanPhamId.id = :idSP and i.mauSacId = :idMS")
    List<String> findBySanPhamIdAndMauSacId(@Param("idSP") BigInteger idSP, @Param("idMS") BigInteger idMS);

}
