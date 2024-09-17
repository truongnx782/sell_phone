package com.example.demo.Controller;

import com.example.demo.Beans.PhieuGiamGiaBean;
import com.example.demo.Entitys.KhachHang;
import com.example.demo.Entitys.PhieuGG_KH;
import com.example.demo.Entitys.PhieuGiamGia;
import com.example.demo.Repository.*;
import com.example.demo.Service.EmailService;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@Controller
@RequestMapping("/phieu-giam-gia")
public class PhieuGiamGiaController {

    @Autowired
    PhieuGiamGiaRepository pggRE;

    @Autowired
    KhachHangRepository khachHangRE;

    @Autowired
    PhieuGG_KHRepo pgg_khRE;

    @Autowired
    EmailService emailService;

    Page<PhieuGiamGia> dsPGGSort;
    Page<PhieuGiamGia> dsPGGSortIndex;
    String searchSort;
    String trangThaiSort;
    String ngayBatDauSort;
    String ngayKetThucSort;


    @GetMapping("/index")
    public String getUI(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            RedirectAttributes redirectAttributes
    ) throws ParseException {
        Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<PhieuGiamGia> dsPGG = this.pggRE.findAll(pageable);
        dsPGGSortIndex = dsPGG;
        model.addAttribute("pggList", dsPGG);
        List<PhieuGiamGia> lst = pggRE.findPhieuGiamGiasByTrangThai0And1();
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String ngayFormat = currentDateTime.format(formatter);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date ngayHienTai  = dateFormat.parse(ngayFormat);
        for (PhieuGiamGia p:lst) {
            if(p.getNgayKetThuc().before(ngayHienTai)){
                p.setTrangThai(2);
                this.pggRE.save(p);
            }
        }
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
        return "admin/ql_phieu_giam_gia/index_pgg";
    }

    @GetMapping("/get-list-pgg")
    @ResponseBody
    public ResponseEntity<Page<PhieuGiamGia>> getListPGG(
            @RequestParam("page") Optional<Integer> pageParam
    ) {
        Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
        Page<PhieuGiamGia> dsPGG = this.pggRE.findAll(pageable);
        return ResponseEntity.ok(dsPGG);
    }

    @GetMapping("/get-list-pgg/search")
    public String getSearch(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            @RequestParam("search") String search,
            @RequestParam("trangThai") String trangThai,
            @RequestParam("ngayBatDau") String ngayBatDau,
            @RequestParam("ngayKetThuc") String ngayKetThuc
    ) throws ParseException {
        model.addAttribute("checkSearch", true);
        String trimmedInput = trangThai.replace(",", "");
        trangThai = trimmedInput;
        System.out.println("Trạng thái: "+trimmedInput);
        searchSort  = search;
        trangThaiSort = trangThai;
        ngayBatDauSort = ngayBatDau;
        ngayKetThucSort = ngayKetThuc;
        Integer trangThaiSearch = 3;

        Boolean flagInput = false;
        Boolean flagGiaTriGiam = false;
        Boolean flagSoLuong = false;
        Boolean flagLoaiGiamGia = false;
        Boolean flagNgayBatDau = false;
        Boolean flagNgayKetThuc = false;
        Boolean flagTrangThai = false;
        LocalDateTime ngaybd = null;
        Timestamp ngayBatDauTimestamp = null;
        LocalDateTime ngaykt = null;
        Timestamp ngayKetThucTimestamp = null;

        BigDecimal giaTriGiam = new BigDecimal(0);
        Integer soLuong = -1;
        Integer loaiGiamGia = null;
        if(trangThai != null && trangThai != ""){
            flagTrangThai = true;
            trangThaiSearch = Integer.parseInt(trangThai);
        }

        if(!checkNgayIsNull(ngayBatDau)){
            flagNgayBatDau = true;
            ngaybd = LocalDateTime.parse(ngayBatDau);
            ngayBatDauTimestamp = Timestamp.valueOf(ngaybd);
        }

        if(!checkNgayIsNull(ngayKetThuc)){
            flagNgayKetThuc = true;
            ngaykt = LocalDateTime.parse(ngayKetThuc);
            ngayKetThucTimestamp = Timestamp.valueOf(ngaykt);
        }

        if(!checkInputSearchIsNull(search)){
            flagInput = true;
            if(checkInputSearchIsNumber(search.trim())){
                giaTriGiam = new BigDecimal(search);
                soLuong = Integer.parseInt(search);
                if(pggRE.existsByGiaTriToiThieuApDung(giaTriGiam)){
                    flagGiaTriGiam = true;
                }else if(pggRE.existsBySoLuong(soLuong)){
                    flagSoLuong = true;
                }
            }else {
                if (search.trim().equalsIgnoreCase("Giảm theo %")) {
                    flagLoaiGiamGia = true;
                    loaiGiamGia = 0;
                } else if (search.trim().equalsIgnoreCase("Giảm theo tiền")) {
                    flagLoaiGiamGia = true;
                    loaiGiamGia = 1;
                }
            }
        }

        if(flagInput){
            if(flagGiaTriGiam){
                if(flagTrangThai && flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByGiaTriToiThieuApDungAndTrangThaiAndNgayBatDauAndNgayKetThucBetween(giaTriGiam,trangThaiSearch,ngayBatDauTimestamp,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(flagTrangThai && flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByGiaTriToiThieuApDungAndTrangThaiAndNgayBatDau(giaTriGiam,trangThaiSearch,ngayBatDauTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(flagTrangThai && !flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByGiaTriToiThieuApDungAndTrangThaiAndNgayKetThuc(giaTriGiam,trangThaiSearch,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                } else if(flagTrangThai && !flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByGiaTriToiThieuApDungAndTrangThai(giaTriGiam,trangThaiSearch,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByGiaTriToiThieuApDungAndNgayBatDauAndNgayKetThucBetween(giaTriGiam,ngayBatDauTimestamp, ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && !flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByGiaTriToiThieuApDungAndNgayKetThuc(giaTriGiam, ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByGiaTriToiThieuApDungAndNgayBatDau(giaTriGiam,ngayBatDauTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && !flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByGiaTriToiThieuApDung(giaTriGiam,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }
            }
            else if(flagSoLuong){
                if(flagTrangThai && flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasBySoLuongAndTrangThaiAndNgayBatDauAndNgayKetThucBetween(soLuong,trangThaiSearch,ngayBatDauTimestamp,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(flagTrangThai && flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasBySoLuongAndTrangThaiAndNgayBatDau(soLuong,trangThaiSearch,ngayBatDauTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(flagTrangThai && !flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasBySoLuongAndTrangThaiAndNgayKetThuc(soLuong,trangThaiSearch,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                } else if(flagTrangThai && !flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasBySoLuongAndTrangThai(soLuong,trangThaiSearch,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasBySoLuongAndNgayBatDauAndNgayKetThucBetween(soLuong,ngayBatDauTimestamp,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && !flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasBySoLuongAndNgayKetThuc(soLuong,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasBySoLuongAndNgayBatDau(soLuong,ngayBatDauTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
//                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
//                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && !flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasBySoLuong(soLuong,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }
            }
            else if(flagLoaiGiamGia){
                if(flagTrangThai && flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByLoaiGiamGiaAndTrangThaiAndNgayBatDauAndNgayKetThucBetween(loaiGiamGia,trangThaiSearch,ngayBatDauTimestamp,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(flagTrangThai && flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByLoaiGiamGiaAndTrangThaiAndNgayBatDau(loaiGiamGia,trangThaiSearch,ngayBatDauTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(flagTrangThai && !flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByLoaiGiamGiaAndTrangThaiAndNgayKetThuc(loaiGiamGia,trangThaiSearch,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
//                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                } else if(flagTrangThai && !flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByLoaiGiamGiaAndTrangThai(loaiGiamGia,trangThaiSearch,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
//                    model.addAttribute("ngayBatDau", ngayBatDau);
//                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByLoaiGiamGiaAndNgayBatDauAndNgayKetThucBetween(loaiGiamGia,ngayBatDauTimestamp,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
//                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && !flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByLoaiGiamGiaAndNgayKetThuc(loaiGiamGia,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
//                    model.addAttribute("trangThai", trangThai);
//                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByLoaiGiamGiaAndNgayBatDau(loaiGiamGia,ngayBatDauTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
//                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
//                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && !flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByLoaiGiamGia(loaiGiamGia,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }
            }
            else if(!flagGiaTriGiam && !flagSoLuong && !flagLoaiGiamGia){
                if(flagTrangThai && flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndTrangThaiAndNgayBatDauAndNgayKetThucBetween(search,search,trangThaiSearch,ngayBatDauTimestamp,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(flagTrangThai && flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndTrangThaiAndNgayBatDau(search,search,trangThaiSearch,ngayBatDauTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(flagTrangThai && !flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndTrangThaiAndNgayKetThuc(search,search,trangThaiSearch,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
//                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                } else if(flagTrangThai && !flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndTrangThai(search,search,trangThaiSearch,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
                    model.addAttribute("trangThai", trangThai);
//                    model.addAttribute("ngayBatDau", ngayBatDau);
//                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndNgayBatDauAndNgayKetThucBetween(search,search,ngayBatDauTimestamp,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
//                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && !flagNgayBatDau && flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndNgayKetThuc(search,search,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
//                    model.addAttribute("trangThai", trangThai);
//                    model.addAttribute("ngayBatDau", ngayBatDau);
                    dsPGGSort = dsPGG;
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGiaAndNgayBatDau(search,search,ngayBatDauTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
//                    model.addAttribute("trangThai", trangThai);
                    model.addAttribute("ngayBatDau", ngayBatDau);
//                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagTrangThai && !flagNgayBatDau && !flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByMaPhieuGiamGiaOrTenPhieuGiamGia(search,search,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("search", search);
//                    model.addAttribute("trangThai", trangThai);
//                    model.addAttribute("ngayBatDau", ngayBatDau);
//                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }
            }
        }
        else if(!flagInput) {
            if(flagTrangThai){
               if(flagNgayBatDau && flagNgayKetThuc){
                   Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                   Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByTrangThaiAndNgayBatDauAndNgayKetThucBetween(trangThaiSearch,ngayBatDauTimestamp,ngayKetThucTimestamp,pageable);
                   model.addAttribute("pggList", dsPGG);
                   model.addAttribute("trangThai", trangThai);
                   model.addAttribute("ngayBatDau", ngayBatDau);
                   model.addAttribute("ngayKetThuc", ngayKetThuc);
                   dsPGGSort = dsPGG;
                   model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                   model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                   return "admin/ql_phieu_giam_gia/index_pgg";
               }else if(!flagNgayBatDau && flagNgayKetThuc){
                   Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                   Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByTrangThaiAndNgayKetThuc(trangThaiSearch,ngayKetThucTimestamp,pageable);
                   model.addAttribute("pggList", dsPGG);
                   model.addAttribute("trangThai", trangThai);
                   model.addAttribute("ngayKetThuc", ngayKetThuc);
                   dsPGGSort = dsPGG;
                   model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                   model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                   return "admin/ql_phieu_giam_gia/index_pgg";
               }else if(!flagNgayBatDau && flagNgayKetThuc){
                   Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                   Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByTrangThaiAndNgayBatDau(trangThaiSearch,ngayBatDauTimestamp,pageable);
                   model.addAttribute("pggList", dsPGG);
                   model.addAttribute("trangThai", trangThai);
                   model.addAttribute("ngayBatDau", ngayBatDau);
                   dsPGGSort = dsPGG;
                   model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                   model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                   return "admin/ql_phieu_giam_gia/index_pgg";
               }else if(!flagNgayBatDau && !flagNgayKetThuc){
                   Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                   Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByTrangThai(trangThaiSearch,pageable);
                   model.addAttribute("pggList", dsPGG);
                   model.addAttribute("trangThai", trangThai);
                   dsPGGSort = dsPGG;
                   model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                   model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                   return "admin/ql_phieu_giam_gia/index_pgg";
               }
            }
            else if(flagNgayBatDau){
                if(flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByNgayBatDauAndNgayKetThucBetween(ngayBatDauTimestamp,ngayKetThucTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    model.addAttribute("ngayKetThuc", ngayKetThuc);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }else if(!flagNgayKetThuc){
                    Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                    Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByNgayBatDau(ngayBatDauTimestamp,pageable);
                    model.addAttribute("pggList", dsPGG);
                    model.addAttribute("ngayBatDau", ngayBatDau);
                    dsPGGSort = dsPGG;
                    model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                    model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                    return "admin/ql_phieu_giam_gia/index_pgg";
                }
            }else {
                Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "maPhieuGiamGia"));
                Page<PhieuGiamGia> dsPGG = this.pggRE.findPhieuGiamGiasByNgayKetThuc(ngayKetThucTimestamp,pageable);
                model.addAttribute("pggList", dsPGG);
                model.addAttribute("ngayKetThuc", ngayKetThuc);
                dsPGGSort = dsPGG;
                model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
                model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
                return "admin/ql_phieu_giam_gia/index_pgg";
            }
        }

        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
            return "admin/ql_phieu_giam_gia/index_pgg";
    }


    @GetMapping("/detail/{id}")

    public PhieuGiamGia getOne(
            Model model,
            @PathVariable("id") BigInteger id
    ) {
        return pggRE.findById(id).get();
    }

    @GetMapping("/add-pgg")
    public String getUIAdd(Model model) {
        PhieuGiamGia pgg = new PhieuGiamGia();
        model.addAttribute("pgg", pgg);
        List<KhachHang> lstKH = khachHangRE.findAll();
        model.addAttribute("lstKH", lstKH);
        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Thêm mới phiếu giảm giá");
        return "admin/ql_phieu_giam_gia/create";
    }

    @PostMapping("/pgg-create")
    public String save(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            @Validated @ModelAttribute("pgg") PhieuGiamGiaBean pggForm,
            BindingResult error,
            @RequestParam(name = "checkBoxKH", required = false) BigInteger[] selectedKHValues
    ) throws ParseException {
        if (error.hasErrors() || !pggForm.isNgayKetThucSauNgayBatDau() || !pggForm.checkSoLuong() || !pggForm.isSoTienDuocGiamVaGiaTriToiThieu()) {
            if (!pggForm.isNgayKetThucSauNgayBatDau()) {
                error.rejectValue("ngayKetThuc", "error.ngayKetThuc", "Ngày kết thúc phải sau ngày bắt đầu!");
            }
            if (pggForm.getLoaiGiamGia() != null) {
                if (pggForm.getLoaiGiamGia() == 0) {
                    if (!pggForm.isSoTienDuocGiamVaGiaTriToiThieu()) {
                        error.rejectValue("soTienDuocGiamToiDa", "error.soTienDuocGiamToiDa", "Số tiền được giảm <= giá trị hóa đơn áp dụng!");
                    }
                    if (!pggForm.checkPhanTramGiam()) {
                        error.rejectValue("phanTramGiam", "error.phanTramGiam", "Nhập phần trăm!");
                    }
                    if (!pggForm.checkSoTienDuocGiamToiDa()) {
                        error.rejectValue("soTienDuocGiamToiDa", "error.soTienDuocGiamToiDa", "Nhập số tiền!");
                    }
                } else if (pggForm.getLoaiGiamGia() == 1) {
                    if (!pggForm.checkSoTienDuocGiam()) {
                        error.rejectValue("soTienDuocGiam", "error.soTienDuocGiam", "Nhập số tiền!");
                    }
                }
            }

            if (pggForm.getTrangThaiSoLuong() == 0) {
                if (!pggForm.checkSoLuong()) {
                    error.rejectValue("soLuong", "error.soLuong", "Nhập số lượng!");
                }
            }
            System.out.println("Vẫn còn lỗi");
            model.addAttribute("pgg", pggForm);
            List<KhachHang> lstKH = khachHangRE.findAll();
            model.addAttribute("lstKH", lstKH);
            model.addAttribute("lstKHSelect", selectedKHValues);
            model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
            model.addAttribute("message_title2", "Thêm mới phiếu giảm giá");
            return "admin/ql_phieu_giam_gia/create";
        } else {
//            Thêm phiếu giảm giá
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date ngayBDForm = pggForm.getNgayBatDau();
            String ngayBD = dateFormat.format(ngayBDForm);
            Date ngayBDNew = new Date();
            Date ngayKTForm = pggForm.getNgayKetThuc();
            String ngayKT = dateFormat.format(ngayKTForm);
            Date ngayKTNew = new Date();
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String ngayHienTai = currentDateTime.format(formatter);
            Date ngayTao = new Date();
            try {
                ngayBDNew = dateFormat.parse(ngayBD);
                ngayKTNew = dateFormat.parse(ngayKT);
                ngayTao = dateFormat.parse(ngayHienTai);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PhieuGiamGia pggNew = new PhieuGiamGia();
            String maPhieuGiamGia = "";
            Long soPGG = pggRE.countAllPhieuGiamGia();
            if (pggForm.getMaPhieuGiamGia() == null || pggForm.getMaPhieuGiamGia() == "") {
                do {
                    maPhieuGiamGia = "PGG00" + soPGG++;
                } while (pggRE.existsByMaPhieuGiamGia(maPhieuGiamGia));
                pggNew.setMaPhieuGiamGia(maPhieuGiamGia);
            } else {
                pggNew.setMaPhieuGiamGia("PGG00" + pggForm.getMaPhieuGiamGia());
            }
            pggNew.setTenPhieuGiamGia(pggForm.getTenPhieuGiamGia());
            pggNew.setNgayBatDau(ngayBDNew);
            pggNew.setNgayKetThuc(ngayKTNew);
            pggNew.setLoaiGiamGia(pggForm.getLoaiGiamGia());
            if (pggForm.getLoaiGiamGia() == 0) {
                pggNew.setPhanTramGiam(pggForm.getPhanTramGiam());
                pggNew.setSoTienDuocGiamToiDa(pggForm.getSoTienDuocGiamToiDa());
                pggNew.setSoTienDuocGiam(new BigDecimal(0));
            } else {
                pggNew.setPhanTramGiam(0);
                pggNew.setSoTienDuocGiamToiDa(new BigDecimal(0));
                pggNew.setSoTienDuocGiam(pggForm.getSoTienDuocGiam());
            }

            pggNew.setGiaTriToiThieuApDung(pggForm.getGiaTriToiThieuApDung());
            pggNew.setMoTa(pggForm.getMoTa());
            pggNew.setNgayTao(ngayTao);
            pggNew.setNgaySua(ngayTao);
            pggNew.setNguoiTao("oanhntk");
            pggNew.setNguoiSua("oanhntk");
            if (ngayBDNew.after(ngayTao)) {
                pggNew.setTrangThai(0);
            } else {
                pggNew.setTrangThai(1);
            }
            pggNew.setTrangThaiCongKhai(pggForm.getTrangThaiCongKhai());
            this.pggRE.save(pggNew);
//            Thêm phiếu giảm giá_khách hàng

            if(pggForm.getTrangThaiCongKhai() == 1){
                for (BigInteger idKH:selectedKHValues) {
                    PhieuGG_KH phieuGG_kh = new PhieuGG_KH();
                    phieuGG_kh.setVoucherID(pggNew.getId());
                    phieuGG_kh.setKhachHangID(idKH);
                    this.pgg_khRE.save(phieuGG_kh);
                }
                pggNew.setTrangThaiSoLuong(0);
                pggNew.setSoLuong(selectedKHValues.length);
                // Gửi email
                List<String> lstEmails = new ArrayList<>();
                for (BigInteger idKH:selectedKHValues) {
                    String e = khachHangRE.getReferenceById(idKH).getEmail();
                    lstEmails.add(e);
                }
                String subject = "Phiếu giảm giá MPShop";
                String text = "MPShop xin gửi tặng khách hàng phiếu giảm giá "+pggNew.getTenPhieuGiamGia()+
                        ". Phiếu giảm giá được áp dụng từ "+pggNew.getNgayBatDau()+" đến "+pggNew.getNgayKetThuc()+".\n"+"Cảm ơn quý khách hàng đã luôn tin tưởng và lựa chọn mua sắm các sản phẩm của MPShop!";
                String[] emailArray = lstEmails.toArray(new String[lstEmails.size()]);
                emailService.sendEmail(emailArray, subject, text);
            }else {
                pggNew.setTrangThaiSoLuong(pggForm.getTrangThaiSoLuong());
                if (pggForm.getTrangThaiSoLuong() == 1) {
                    pggNew.setSoLuong(0);
                } else {
                    pggNew.setSoLuong(pggForm.getSoLuong());
                }
            }


            model.addAttribute("success", "Tạo phiếu thành công!");
            Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "id"));
            Page<PhieuGiamGia> dsPGG = this.pggRE.findAll(pageable);
            dsPGGSortIndex = dsPGG;
            model.addAttribute("pggList", dsPGG);
            model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
            model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
            return "admin/ql_phieu_giam_gia/index_pgg";
        }
    }

    @GetMapping("/update-pgg/{id}")
    public String getUIEdit(
            Model model,
            @PathVariable("id") BigInteger id
    ) {
        PhieuGiamGia pgg = pggRE.getReferenceById(id);
        List<PhieuGG_KH> listPGG_KH = pgg_khRE.findPhieuGG_KHSByVoucherID(id);
        List<KhachHang> lstKH = khachHangRE.findAll();
        model.addAttribute("lstKH", lstKH);
        if(listPGG_KH.size() > 0){
            model.addAttribute("listPGG_KH", listPGG_KH);
            List<KhachHang> lstPGG_KH = new ArrayList<>();
            for (PhieuGG_KH p:listPGG_KH) {
                lstPGG_KH.add(khachHangRE.getReferenceById(p.getKhachHangID()));
            }
            model.addAttribute("lstP_KH", lstPGG_KH);
        }
        model.addAttribute("pgg", pgg);
        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Cập nhật phiếu giảm giá");
        return "admin/ql_phieu_giam_gia/update";
    }

    @PostMapping("/update-status/{id}")
    public ResponseEntity<Map<String, Object>> updateTrangThai(@PathVariable("id") BigInteger id) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String ngayLocal = currentDateTime.format(formatter);
        Date ngayHienTai = new Date();
        ngayHienTai = dateFormat.parse(ngayLocal);
        PhieuGiamGia pgg = pggRE.getReferenceById(id);
        try {
            if(pgg.getTrangThai() != 2){
                pgg.setTrangThai(2);
                pggRE.save(pgg);
                Map<String, Object> response = new HashMap<>();
                response.put("trangThaiUpdate", 2);
                response.put("success", "Cập nhật thành công!");
                return ResponseEntity.ok(response);
            }else if(pgg.getNgayBatDau().after(ngayHienTai)){
                pgg.setTrangThai(0);
                pggRE.save(pgg);
                Map<String, Object> response = new HashMap<>();
                response.put("trangThaiUpdate", 0);
                response.put("success", "Cập nhật thành công!");
                return ResponseEntity.ok(response);
            }else if(!ngayHienTai.before(pgg.getNgayBatDau()) && !ngayHienTai.after(pgg.getNgayKetThuc())){
                pgg.setTrangThai(1);
                pggRE.save(pgg);
                Map<String, Object> response = new HashMap<>();
                response.put("trangThaiUpdate", 1);
                response.put("success", "Cập nhật thành công!");
                return ResponseEntity.ok(response);
            } else if(pgg.getSoLuong() == 0 && pgg.getTrangThaiSoLuong() == 0 && !pgg.getNgayKetThuc().after(ngayHienTai)){
                if(pgg.getSoLuong() == 0){
                    Map<String, Object> response = new HashMap<>();
                    response.put("trangThaiUpdate", pgg.getTrangThai());
                    response.put("fail", "Bạn cần cập nhật lại số lượng!");
                    return ResponseEntity.ok(response);
                }else{
                    Map<String, Object> response = new HashMap<>();
                    response.put("trangThaiUpdate", pgg.getTrangThai());
                    response.put("fail", "Cập nhật thất bại!");
                    return ResponseEntity.ok(response);
                }
            }else if(!pgg.getNgayKetThuc().after(ngayHienTai)){
                Map<String, Object> response = new HashMap<>();
                response.put("trangThaiUpdate", pgg.getTrangThai());
                response.put("fail", "Bạn cần cập nhật lại ngày áp dụng!");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("trangThaiUpdate", pgg.getTrangThai());
                response.put("fail", "Cập nhật thất bại!");
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/edit/{id}")
    public String put(
            @PathVariable("id") BigInteger id,
            Model model,
            @Validated @ModelAttribute("pgg") PhieuGiamGiaBean pggForm,
            BindingResult error
    ) throws ParseException {
        if (error.hasErrors() ||  !pggForm.isNgayKetThucSauNgayBatDau() || !pggForm.checkSoLuong() || !pggForm.isSoTienDuocGiamVaGiaTriToiThieu() || pggForm.getNgayBatDau() == null) {
//            if(error.get)
            if (!pggForm.isNgayKetThucSauNgayBatDau()) {
                error.rejectValue("ngayKetThuc", "error.ngayKetThuc", "Ngày kết thúc phải sau ngày bắt đầu!");
            }
            if (pggForm.getLoaiGiamGia() == 0) {
                if (!pggForm.isSoTienDuocGiamVaGiaTriToiThieu()) {
                    error.rejectValue("soTienDuocGiamToiDa", "error.soTienDuocGiamToiDa", "Số tiền được giảm <= giá trị hóa đơn áp dụng!");
                }
                if (!pggForm.checkPhanTramGiam()) {
                    error.rejectValue("phanTramGiam", "error.phanTramGiam", "Nhập phần trăm!");
                }
                if (!pggForm.checkSoTienDuocGiamToiDa()) {
                    error.rejectValue("soTienDuocGiamToiDa", "error.soTienDuocGiamToiDa", "Nhập số tiền!");
                }
            } else if (pggForm.getLoaiGiamGia() == 1) {
                if (!pggForm.checkSoTienDuocGiam()) {
                    error.rejectValue("soTienDuocGiam", "error.soTienDuocGiam", "Nhập số tiền!");
                }
            }
            if (pggForm.getTrangThaiSoLuong() == 0) {
                if (!pggForm.checkSoLuong()) {
                    error.rejectValue("soLuong", "error.soLuong", "Nhập số lượng!");
                }
            }


            // Lấy danh sách các lỗi trường
            List<FieldError> fieldErrors = error.getFieldErrors();

            // In tên của các trường lỗi
            for (FieldError fieldError : fieldErrors) {
                String fieldName = fieldError.getField();
                if(fieldError.getDefaultMessage().equals("Ngày bắt đầu không hợp lệ!")){
                    System.out.println("Mã id: " + pggRE.getReferenceById(id).getId());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date ngayBDForm = pggForm.getNgayBatDau();
                    String ngayBD = dateFormat.format(ngayBDForm);
                    Date ngayBDNew = new Date();
                    Date ngayKTForm = pggForm.getNgayKetThuc();
                    String ngayKT = dateFormat.format(ngayKTForm);
                    Date ngayKTNew = new Date();

                    LocalDateTime currentDateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String ngayHienTai = currentDateTime.format(formatter);
                    Date ngayTao = new Date();
                    try {
                        ngayBDNew = dateFormat.parse(ngayBD);
                        ngayKTNew = dateFormat.parse(ngayKT);
                        ngayTao = dateFormat.parse(ngayHienTai);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Mã phiếu: "+pggForm.getMaPhieuGiamGia());
                    PhieuGiamGia pggNew = pggRE.getReferenceById(id);
                    pggNew.setId(id);
                    pggNew.setMaPhieuGiamGia(pggForm.getMaPhieuGiamGia());
                    pggNew.setTenPhieuGiamGia(pggForm.getTenPhieuGiamGia());
                    pggNew.setNgayBatDau(ngayBDNew);
                    pggNew.setNgayKetThuc(ngayKTNew);
                    pggNew.setLoaiGiamGia(pggForm.getLoaiGiamGia());
                    if (pggForm.getLoaiGiamGia() == 0) {
                        pggNew.setPhanTramGiam(pggForm.getPhanTramGiam());
                        pggNew.setSoTienDuocGiamToiDa(pggForm.getSoTienDuocGiamToiDa());
                        pggNew.setSoTienDuocGiam(new BigDecimal(0));
                    } else {
                        pggNew.setPhanTramGiam(0);
                        pggNew.setSoTienDuocGiamToiDa(new BigDecimal(0));
                        pggNew.setSoTienDuocGiam(pggForm.getSoTienDuocGiam());
                    }
                    pggNew.setTrangThaiSoLuong(pggForm.getTrangThaiSoLuong());
                    if (pggForm.getTrangThaiSoLuong() == 1) {
                        pggNew.setSoLuong(0);
                    } else {
                        pggNew.setSoLuong(pggForm.getSoLuong());
                    }
                    pggNew.setGiaTriToiThieuApDung(pggForm.getGiaTriToiThieuApDung());
                    pggNew.setMoTa(pggForm.getMoTa());
                    pggNew.setNgaySua(ngayTao);
                    pggNew.setNguoiSua("oanhntk");
                    if (ngayBDNew.after(ngayTao)) {
                        pggNew.setTrangThai(0);
                    } else {
                        pggNew.setTrangThai(1);
                    }
                    model.addAttribute("success", true);
                    this.pggRE.save(pggNew);
                    return "redirect:/phieu-giam-gia/index";
                }
            }

            System.out.println("Vẫn còn lỗi: "+error.getFieldError());

            model.addAttribute("pgg", pggForm);
            model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
            model.addAttribute("message_title2", "Cập nhật phiếu giảm giá");
            return "admin/ql_phieu_giam_gia/update";
        } else {
            System.out.println("Mã id: " + pggRE.getReferenceById(id).getId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date ngayBDForm = pggForm.getNgayBatDau();
            String ngayBD = dateFormat.format(ngayBDForm);
            Date ngayBDNew = new Date();
            Date ngayKTForm = pggForm.getNgayKetThuc();
            String ngayKT = dateFormat.format(ngayKTForm);
            Date ngayKTNew = new Date();

            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String ngayHienTai = currentDateTime.format(formatter);
            Date ngayTao = new Date();
            try {
                ngayBDNew = dateFormat.parse(ngayBD);
                ngayKTNew = dateFormat.parse(ngayKT);
                ngayTao = dateFormat.parse(ngayHienTai);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Mã phiếu: "+pggForm.getMaPhieuGiamGia());
            PhieuGiamGia pggNew = pggRE.getReferenceById(id);
            pggNew.setId(id);
            pggNew.setMaPhieuGiamGia(pggForm.getMaPhieuGiamGia());
            pggNew.setTenPhieuGiamGia(pggForm.getTenPhieuGiamGia());
            pggNew.setNgayBatDau(ngayBDNew);
            pggNew.setNgayKetThuc(ngayKTNew);
            pggNew.setLoaiGiamGia(pggForm.getLoaiGiamGia());
            if (pggForm.getLoaiGiamGia() == 0) {
                pggNew.setPhanTramGiam(pggForm.getPhanTramGiam());
                pggNew.setSoTienDuocGiamToiDa(pggForm.getSoTienDuocGiamToiDa());
                pggNew.setSoTienDuocGiam(new BigDecimal(0));
            } else {
                pggNew.setPhanTramGiam(0);
                pggNew.setSoTienDuocGiamToiDa(new BigDecimal(0));
                pggNew.setSoTienDuocGiam(pggForm.getSoTienDuocGiam());
            }
            pggNew.setTrangThaiSoLuong(pggForm.getTrangThaiSoLuong());
            if (pggForm.getTrangThaiSoLuong() == 1) {
                pggNew.setSoLuong(0);
            } else {
                pggNew.setSoLuong(pggForm.getSoLuong());
            }
            pggNew.setGiaTriToiThieuApDung(pggForm.getGiaTriToiThieuApDung());
            pggNew.setMoTa(pggForm.getMoTa());
            pggNew.setNgaySua(ngayTao);
            pggNew.setNguoiSua("oanhntk");
            if (ngayBDNew.after(ngayTao)) {
                pggNew.setTrangThai(0);
            } else {
                pggNew.setTrangThai(1);
            }
            model.addAttribute("success", true);
            this.pggRE.save(pggNew);
            return "redirect:/phieu-giam-gia/index";
        }
    }


    public Boolean checkInputSearchIsNumber(String input) {
        if (input.trim().matches("[\\d+]")) {
            return true;
        }
        return false;
    }

    public Boolean checkInputSearchIsNull(String input) {
        if (input.trim().equals("") || input.trim() == null) {
            return true;
        }
        return false;
    }

    public Boolean checkNgayIsNull(String ngaySearch) {
        if (ngaySearch.isBlank()) {
            return true;
        }
        return false;
    }

    public Boolean checkTrangThaiIsNotNull(Integer trangThai) {
        if (trangThai == null) {
            return false;
        }
        return true;
    }

    @GetMapping("/sort-name-desc")
    public String sortTenDesc(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam
    ) {
        Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "tenPhieuGiamGia"));
        dsPGGSort = this.pggRE.findAll(pageable);
        model.addAttribute("search", searchSort);
        model.addAttribute("trangThai", trangThaiSort);
        model.addAttribute("ngayBatDau", ngayBatDauSort);
        model.addAttribute("ngayKetThuc", ngayKetThucSort);
        model.addAttribute("pggList", dsPGGSort);
        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
        return "admin/ql_phieu_giam_gia/index_pgg";
    }


    @GetMapping("/sort-name-search-asc")
    public String sortTenAsc(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            @RequestParam("search") String search,
            @RequestParam("trangThai") String trangThai,
            @RequestParam("ngayBatDau") String ngayBatDau,
            @RequestParam("ngayKetThuc") String ngayKetThuc
    ) {
        Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.ASC, "tenPhieuGiamGia"));
        Page<PhieuGiamGia> dsPGG = this.pggRE.findAll(pageable);
        model.addAttribute("search", searchSort);
        model.addAttribute("trangThai", trangThaiSort);
        model.addAttribute("ngayBatDau", ngayBatDauSort);
        model.addAttribute("ngayKetThuc", ngayKetThucSort);
        model.addAttribute("pggList", dsPGG);
        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
        return "admin/ql_phieu_giam_gia/index";
    }

    @GetMapping("/sort-name-search-desc")
    public String sortTenDesc(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            @RequestParam("search") String search,
            @RequestParam("trangThai") String trangThai,
            @RequestParam("ngayBatDau") String ngayBatDau,
            @RequestParam("ngayKetThuc") String ngayKetThuc
    ) {
        Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "tenPhieuGiamGia"));
        Page<PhieuGiamGia> dsPGG = this.pggRE.findAll(pageable);
        model.addAttribute("search", searchSort);
        model.addAttribute("trangThai", trangThaiSort);
        model.addAttribute("ngayBatDau", ngayBatDauSort);
        model.addAttribute("ngayKetThuc", ngayKetThucSort);
        model.addAttribute("pggList", dsPGG);
        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
        return "admin/ql_phieu_giam_gia/index";
    }

    @GetMapping("/sort-time-desc")
    public String sortThoiGianDesc(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam
    ) {
        Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "ngayBatDau"));
        dsPGGSort = this.pggRE.findAll(pageable);
        model.addAttribute("search", searchSort);
        model.addAttribute("trangThai", trangThaiSort);
        model.addAttribute("ngayBatDau", ngayBatDauSort);
        model.addAttribute("ngayKetThuc", ngayKetThucSort);
        model.addAttribute("pggList", dsPGGSort);
        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
        return "admin/ql_phieu_giam_gia/index_pgg";
    }

    @GetMapping("/sort-time-asc")
    public String sortThoiGiaAsc(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam
    ) {
        Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.ASC, "ngayBatDau"));
        dsPGGSort = this.pggRE.findAll(pageable);
        model.addAttribute("search", searchSort);
        model.addAttribute("trangThai", trangThaiSort);
        model.addAttribute("ngayBatDau", ngayBatDauSort);
        model.addAttribute("ngayKetThuc", ngayKetThucSort);
        model.addAttribute("pggList", dsPGGSort);
        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
        return "admin/ql_phieu_giam_gia/index_pgg";
    }

    @GetMapping("/sort-type-desc")
    public String sortLoaiDesc(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam
    ) {
        Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.DESC, "loaiGiamGia"));
        dsPGGSort = this.pggRE.findAll(pageable);
        model.addAttribute("search", searchSort);
        model.addAttribute("trangThai", trangThaiSort);
        model.addAttribute("ngayBatDau", ngayBatDauSort);
        model.addAttribute("ngayKetThuc", ngayKetThucSort);
        model.addAttribute("pggList", dsPGGSort);
        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
        return "admin/ql_phieu_giam_gia/index_pgg";
    }

    @GetMapping("/sort-type-asc")
    public String sortLoaiAsc(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam
    ) {
        Pageable pageable = PageRequest.of(pageParam.orElse(0), 5, Sort.by(Sort.Direction.ASC, "loaiGiamGia"));
        dsPGGSort = this.pggRE.findAll(pageable);
        model.addAttribute("search", searchSort);
        model.addAttribute("trangThai", trangThaiSort);
        model.addAttribute("ngayBatDau", ngayBatDauSort);
        model.addAttribute("ngayKetThuc", ngayKetThucSort);
        model.addAttribute("pggList", dsPGGSort);
        model.addAttribute("message_title1", "Quản lý phiếu giảm giá");
        model.addAttribute("message_title2", "Danh sách phiếu giảm giá");
        return "admin/ql_phieu_giam_gia/index_pgg";
    }

    private boolean shouldSkipNgayBatDauCheck(Date ngayBatDau) {
        // Nếu ngày bắt đầu là null, bỏ qua kiểm tra
        if (ngayBatDau == null) {
            return true;
        }

        // Lấy thời gian hiện tại
        LocalDateTime currentDateTime = LocalDateTime.now();
        Timestamp ngayHienTaiTimestamp = Timestamp.valueOf(currentDateTime);

        // Chuyển đổi ngày bắt đầu sang Timestamp
        Timestamp ngayBatDauTimestamp = new Timestamp(ngayBatDau.getTime());

        // Nếu ngày bắt đầu không cần phải kiểm tra, bỏ qua lỗi
        return ngayBatDauTimestamp.before(ngayHienTaiTimestamp) ||
                ngayBatDauTimestamp.equals(ngayHienTaiTimestamp) ||
                ngayBatDauTimestamp.after(ngayHienTaiTimestamp);
    }

}
