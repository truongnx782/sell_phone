package com.example.demo.Controller;

import com.example.demo.Entitys.MauSac;
import com.example.demo.Repository.MauSacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.math.BigInteger;
import java.util.Optional;

@Controller

@RequestMapping("mau-sac")
public class MauSacController {

    @Autowired
    private MauSacRepository mauSacRepository;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> pageParam,
                        RedirectAttributes redirectAttributes)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<MauSac> data = mauSacRepository.findAll(pageable);
        model.addAttribute("data", data);
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách màu sắc");
        return "admin/ql_thuoc_tinh/mau_sac/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách màu sắc");
        model.addAttribute("message_title2", "Thêm mới màu sắc");
        return "admin/ql_thuoc_tinh/mau_sac/add";
    }

    @PostMapping("/store")
    public String store(
            @RequestParam("ten") String ten,
            @RequestParam("ma") String ma,
            Model model,
            RedirectAttributes redirectAttributes) {


        if (ten.trim().length() == 0 || (mauSacRepository.findByTenMauSacIsLike(ten.trim()) != null)|| ma.trim().length()==0) {
            if (ten.trim().length() == 0) {
                model.addAttribute("loi", "Loi_Trong!");
            }else if (mauSacRepository.findByTenMauSacIsLike(ten.trim()) != null) {
                model.addAttribute("loi", "Loi_Trung!");
            }
            if (ma.trim().length() == 0) {
                    model.addAttribute("loi", "Loi_Trong!");
            }else if (mauSacRepository.findByMaMauSacIsLike(ma.trim()) != null) {
                model.addAttribute("loi", "Loi_Trung!");
            }
            model.addAttribute("ten", ten);
            model.addAttribute("ma", ma);
            model.addAttribute("message_title1", "Danh sách màu sắc");
            model.addAttribute("message_title2", "Thêm mới màu sắc");
            return "admin/ql_thuoc_tinh/mau_sac/add";
        } else {
            MauSac mauSac = new MauSac();
            mauSac.setTenMauSac(ten);
            mauSac.setMaMauSac(ma);
            mauSac.setTrangThai(1);
            mauSacRepository.save(mauSac);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/mau-sac/index";

        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") BigInteger id)
    {
        int currentStatus = mauSacRepository.findById(id).get().getTrangThai();
        if (currentStatus == 1) {
            mauSacRepository.changeStatus(0, id);
        } else {
            mauSacRepository.changeStatus(1, id);
        }
        return "redirect:/mau-sac/index";
    }

    @GetMapping("search")
    public String search(@RequestParam("search") String ten, Model model, @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<MauSac> findSearch = mauSacRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("search", ten);
        model.addAttribute("checkSearch", true);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách màu sắc");
        return "admin/ql_thuoc_tinh/mau_sac/index";
    }

    @GetMapping("filter")
    public String filter(Model model, @RequestParam("status") int status, @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        if (status == 1) {
            model.addAttribute("data", mauSacRepository.findAllByTrangThai(pageable,mauSacRepository.ACTIVE));
        } else {
            model.addAttribute("data", mauSacRepository.findAllByTrangThai(pageable,mauSacRepository.INACTIVE));
        }

        return "admin/ql_thuoc_tinh/mau_sac/index";
    }

    @GetMapping("edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {
        Optional<MauSac> mauSac = mauSacRepository.findById(id);
        model.addAttribute("mauSac", mauSac.get());
        model.addAttribute("message_title1", "Danh sách màu sắc");
        model.addAttribute("message_title2", "Cập nhật màu sắc");
        return "admin/ql_thuoc_tinh/mau_sac/update";
    }

    @PostMapping("update/{id}")
    public String update(@PathVariable("id") BigInteger id, @RequestParam("ten") String ten, @RequestParam("ma") String ma,
             RedirectAttributes redirectAttributes, Model model)
    {

        MauSac mauSacOld = mauSacRepository.getReferenceById(id);
        if (ten.trim().length() == 0 || (mauSacRepository.findByTenMauSacIsLike(ten.trim()) != null && !ten.trim().equals(mauSacOld.getTenMauSac()))||
                ma.trim().length()==0 || (mauSacRepository.findByTenMauSacIsLike(ma.trim()) != null && !ma.trim().equals(mauSacOld.getMaMauSac()))) {
            if (ten.trim().length() == 0) {
                model.addAttribute("loi", "Loi_Trong!");
            }else if (mauSacRepository.findByTenMauSacIsLike(ten.trim()) != null && !ten.trim().equals(mauSacOld.getTenMauSac())) {
                model.addAttribute("loi", "Loi_Trung!");
            }
            if (ma.trim().length() == 0) {
                model.addAttribute("loi", "Loi_Trong!");
            }else if (mauSacRepository.findByTenMauSacIsLike(ma.trim()) != null && !ma.trim().equals(mauSacOld.getMaMauSac())) {
                model.addAttribute("loi", "Loi_Trung!");
            }
            mauSacOld.setMaMauSac(ma);
            mauSacOld.setTenMauSac(ten);
            model.addAttribute("mauSac", mauSacOld);
            model.addAttribute("message_title1", "Danh sách màu sắc");
            model.addAttribute("message_title2", "Cập nhật màu sắc");
            return "admin/ql_thuoc_tinh/mau_sac/update";
        } else {
            mauSacOld.setTenMauSac(ten);
            mauSacOld.setMaMauSac(ma);
            mauSacRepository.save(mauSacOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/mau-sac/index";

        }
    }

    @PostMapping("/quick-store")
    public Object quickStore(
            @RequestParam("ten") String ten,
            @RequestParam("ma") String ma,
            Model model,
            RedirectAttributes redirectAttributes) {

            if (ten.trim().length() == 0) {
                model.addAttribute("loi", "Loi_Trong!");
                return ResponseEntity.ok(1);
            }
            if (ma.trim().length() == 0) {
                model.addAttribute("loi", "Loi_Trong!");
                return ResponseEntity.ok(2);
            }
            if (mauSacRepository.findByTenMauSacIsLike(ten.trim()) != null) {
                model.addAttribute("loi", "Loi_Trung!");
                model.addAttribute("ten", ten);
                return ResponseEntity.ok(3);
            }
            if (mauSacRepository.findByMaMauSacIsLike(ma.trim()) != null) {
                model.addAttribute("loi", "Loi_Trung!");
                model.addAttribute("ma", ma);
                return ResponseEntity.ok(4);
            }
            MauSac mauSac = new MauSac();
            mauSac.setTenMauSac(ten);
            mauSac.setMaMauSac(ma);
            mauSac.setTrangThai(1);
            mauSacRepository.save(mauSac);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return ResponseEntity.ok(5);
    }

}
