package com.example.demo.Controller;

import com.example.demo.Entitys.DiaChi;
import com.example.demo.Entitys.KhachHang;
import com.example.demo.Repository.DiaChiRepository;
import com.example.demo.Repository.KhachHangRepository;
import com.example.demo.Request.DiaChiRequest;
import com.example.demo.Utils.RepoUtuils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("dia-chi")
public class DiaChiController extends RepoUtuils {
    @Autowired
    DiaChiRepository diaChiRepository;
    @Autowired
    KhachHangRepository khachHangRepository;

    @GetMapping("/route")
    public String yourHandlerMethod() {
        return "redirect:/khach-hang/hien-thi";
    }

    @PostMapping("/storeNoIDKhachHang")
    public String create(@Valid @ModelAttribute("DiaChi") DiaChiRequest diaChiRequest, BindingResult result, Model model, HttpSession session) {
        try {
            if (result.hasErrors()) {
                KhachHang kh = new KhachHang();
                List<DiaChi> list = diaChiRepository.findAllBy(WAIT);
                model.addAttribute("KhachHang", kh);
                model.addAttribute("listDC", list);
                model.addAttribute("check", true);
                model.addAttribute("message_title1", "quản lý khách hàng");
                model.addAttribute("message_title2", "Thêm khách hàng");
                return "KhachHang/add";
            }
            DiaChi diaChi = new DiaChi();
            diaChi.setDiaChiCuThe(diaChiRequest.getDiaChiCuThe());
            diaChi.setTinh(diaChiRequest.getTinh());
            diaChi.setQuan(diaChiRequest.getQuan());
            diaChi.setPhuong(diaChiRequest.getPhuong());
            diaChi.setTrangThai(WAIT);
            diaChiRepository.save(diaChi);
            return "redirect:/khach-hang/view-add";
        } catch (
                Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/storeYesIdKhachHang/{KhachHangId}")
    public String create2(@Valid @ModelAttribute("DiaChi") DiaChiRequest diaChiRequest,
                          BindingResult result, @PathVariable("KhachHangId") BigInteger khachHangID,
                          Model model) throws Exception {
        try {
            if (result.hasErrors()) {
                Optional<KhachHang> khachHangOptional = khachHangRepository.findById(khachHangID);
                List<DiaChi> list = diaChiRepository.findAllBy(khachHangID, ACTIVE, DEFAULT);
                model.addAttribute("KhachHang", khachHangOptional.get());
                model.addAttribute("listDC", list);
                model.addAttribute("check", true);
                model.addAttribute("message_title1", "quản lý khách hàng");
                model.addAttribute("message_title2", "Thêm khách hàng");
                System.out.println("co loi");
                return "KhachHang/form";
            }
            DiaChi diaChi = new DiaChi();
            diaChi.setIdKhachHang(khachHangID);
            diaChi.setDiaChiCuThe(diaChiRequest.getDiaChiCuThe());
            diaChi.setTinh(diaChiRequest.getTinh());
            diaChi.setQuan(diaChiRequest.getQuan());
            diaChi.setPhuong(diaChiRequest.getPhuong());
            diaChi.setTrangThai(ACTIVE);
            diaChiRepository.save(diaChi);
            return "redirect:/khach-hang/edit/" + khachHangID;
        } catch (
                Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("edit/{idKhachHang}/{idDiaChi}")
    public String edit(
            @PathVariable("idKhachHang") BigInteger idKhachHang,
            @PathVariable("idDiaChi") BigInteger idDiaChi,
            Model model) {
        try {
            Optional<DiaChi> diaChi = diaChiRepository.findById(idDiaChi);
            System.out.println(diaChi.get());
            Optional<KhachHang> khachHangOptional = khachHangRepository.findById(idKhachHang);
            List<DiaChi> ldc = diaChiRepository.findAllBy(idKhachHang, ACTIVE, DEFAULT);
            model.addAttribute("KhachHang", khachHangOptional.get());
            model.addAttribute("check2", true);
            model.addAttribute("listDC", ldc);
            model.addAttribute("DiaChi", diaChi.get());
            model.addAttribute("message_title1", "quản lý khách hàng");
            model.addAttribute("message_title2", "Thêm khách hàng");
            return "admin/KhachHang/form";
        } catch (
                Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @GetMapping("delete/{idKhachHang}/{idDiaChi}")
    public String delete(
            @PathVariable("idKhachHang") BigInteger idKhachHang,
            @PathVariable("idDiaChi") BigInteger idDiaChi) {
        try {
            Optional<DiaChi> diaChi = diaChiRepository.findById(idDiaChi);
            DiaChi dc = diaChi.get();
            dc.setTrangThai(IN_ACTIVE);
            diaChiRepository.save(dc);
            return "redirect:/khach-hang/edit/" + idKhachHang;
        } catch (
                Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("delete/{idDiaChi}")
    public String deleteNoIdKhachHang(
            @PathVariable("idDiaChi") BigInteger idDiaChi) {
        try {
            Optional<DiaChi> diaChi = diaChiRepository.findById(idDiaChi);
            DiaChi dc = diaChi.get();
            dc.setTrangThai(IN_ACTIVE);
            diaChiRepository.save(dc);
            return "redirect:/khach-hang/view-add";
        } catch (
                Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("updateYesIdKhachHang/{idKhachHang}")
    public String update(@Valid @ModelAttribute("DiaChi") DiaChiRequest diaChiRequest, BindingResult result,
                         @PathVariable("idKhachHang") BigInteger idKhachHang,
                         Model model) {
        try {
            if (result.hasErrors()) {
                System.out.println("co loi");
                Optional<KhachHang> khachHangOptional = khachHangRepository.findById(idKhachHang);
                List<DiaChi> list = diaChiRepository.findAllBy(idKhachHang, ACTIVE, DEFAULT);
                model.addAttribute("KhachHang", khachHangOptional.get());
                model.addAttribute("listDC", list);
                model.addAttribute("check2", true);
                return "admin/KhachHang/form";
            }

            Optional<DiaChi> diaChi = diaChiRepository.findById(diaChiRequest.getId());
            diaChi.map(diaChi2 -> {
                diaChi2.setDiaChiCuThe(diaChiRequest.getDiaChiCuThe());
                diaChi2.setIdKhachHang(idKhachHang);
                diaChi2.setPhuong(diaChiRequest.getPhuong());
                diaChi2.setQuan(diaChiRequest.getQuan());
                diaChi2.setTinh(diaChiRequest.getTinh());
                diaChiRepository.save(diaChi2);
                return diaChi2;
            });

            DiaChi diaChi1 = new DiaChi();
            List<DiaChi> ldc = diaChiRepository.findAllBy(idKhachHang, ACTIVE, DEFAULT);
            Optional<KhachHang> khachHangOptional = khachHangRepository.findById(idKhachHang);
            model.addAttribute("DiaChi", diaChi1);
            model.addAttribute("listDC", ldc);
            model.addAttribute("KhachHang", khachHangOptional.get());
            return "admin/KhachHang/form";
        } catch (
                Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
