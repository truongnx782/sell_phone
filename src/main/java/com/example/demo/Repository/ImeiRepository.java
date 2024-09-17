package com.example.demo.Repository;

import com.example.demo.Entitys.ChiTietSanPham;
import com.example.demo.Entitys.Imei;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Repository
public interface ImeiRepository extends JpaRepository<Imei, BigInteger> {


    @Query("SELECT COUNT(im.idChiTietSP) FROM Imei im WHERE im.idChiTietSP.id = :id")

    Integer countIM(@Param("id") BigInteger id);

    @Query("select i from Imei i where i.idChiTietSP.id=:id")
    List<Imei> findAllByIdChiTietSanPham(@Param("id") BigInteger id);

    @Query("select i from Imei i where i.idChiTietSP.id=:id")
    Page<Imei> findAllByIdChiTietSanPham(@Param("id") BigInteger id, Pageable pageable);


//    @Query(value = "SELECT COALESCE(COUNT(im), 0) FROM Imei im WHERE im.idChiTietSP IN :chiTietSPIds GROUP BY im.idChiTietSP ORDER BY im.idChiTietSP.id DESC"
//         )
//    List<Integer> countByIdChiTietSanPham(@Param("chiTietSPIds") List<Integer> chiTietSPIds);

    @Query("SELECT i.idChiTietSP FROM Imei i WHERE i.maImei IN :imeiList")
    List<ChiTietSanPham> findProductDetailsByImeiList(@Param("imeiList") List<String> imeiList);
    Page<Imei> findByIdChiTietSPAndTrangThai(ChiTietSanPham idChiTietSP, int trangThai, Pageable pageable);
    @Query("SELECT i FROM Imei i WHERE i.maImei = :maImei")
    Imei findByMaImei(@Param("maImei") String maImei);
    @Query("SELECT COUNT(im) FROM Imei im WHERE im.idChiTietSP.id = :id and im.trangThai = 1")
    Integer countIMByIdCTSPAndTrangThai(@Param("id") BigInteger id);

    @Query(value = "SELECT ctsp.MaChiTietSanPham  as MaChiTietSanPham," +
            "ms.TenMauSac as TenMauSac," +
            "r.Ten as TenRam," +
            "rom.TenRom as TenRom," +
            "i.Ma_Imei as maIM " +
            "FROM Imei i " +
            "left join ChiTietSanPham ctsp  ON  i.IDChiTietSP =ctsp.ID " +
            "left join SanPham s on ctsp.IDSanPham= s.ID " +
            "left join MauSac ms on ctsp.IDMauSac = ms.ID " +
            "LEFT join RAM r on ctsp.IDROM =r.ID " +
            "LEFT join ROM rom on ctsp.IDROM =rom.ID " +
            " where ctsp.IDSanPham=:idSP and i.TrangThai=1 ORDER  by ctsp.ID  ",nativeQuery = true)
    List<Map<String,Object>> findAllByIdSanPhamAndStatus(@Param("idSP") BigInteger idSP);


//    select top 1 * from Imei i
//    where i.IDChiTietSP = 78 and i.TrangThai = 1
//    @Query("SELECT im FROM Imei im WHERE im.idChiTietSP.id = :id and im.trangThai = 1 order by im.id asc LIMIT 1")
//    Imei findTop1ImeiByIdChiTietSPAndTrangThai(@Param("id") BigInteger id);
    @Query("SELECT im FROM Imei im WHERE im.idChiTietSP.id = :id AND im.trangThai = 1 ORDER BY im.id ASC")
    List<Imei> findImeiByIdChiTietSPAndTrangThai(@Param("id") BigInteger id, Pageable pageable);

}
