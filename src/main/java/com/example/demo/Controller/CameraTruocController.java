package com.example.demo.Controller;

import com.example.demo.Entitys.CameraTruoc;
import com.example.demo.Entitys.Chip;
import com.example.demo.Repository.CameraTruocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.math.BigInteger;
import java.util.Optional;

@Controller
@RequestMapping("cameraTruoc")
public class CameraTruocController {

    @Autowired
    private CameraTruocRepository cameraTruocRepository;

    @GetMapping("/index")
    public String index(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            RedirectAttributes redirectAttributes)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<CameraTruoc> data = cameraTruocRepository.findAll(pageable);
        model.addAttribute("data", data);
        // Kiểm tra xem có thông báo "success" trong redirectAttributes hay không
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách camera trước");
        return "admin/ql_thuoc_tinh/camera_truoc/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách camera trước");
        model.addAttribute("message_title2", "Thêm mới camera trước");
        return "admin/ql_thuoc_tinh/camera_truoc/add";
    }


    @PostMapping("/store")
    public String store(@RequestParam("ten") String ten,
                        Model model,
                        @RequestParam("page") Optional<Integer> pageParam,
                        RedirectAttributes redirectAttributes)
    {

        if (ten.trim().length() == 0 || (cameraTruocRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(cameraTruocRepository.findByTenIsLike(ten.trim()) != null){
                model.addAttribute("loi",  "Loi_Trung!");
                model.addAttribute("ten", ten);
            }
            model.addAttribute("message_title1", "Danh sách camera trước");
            model.addAttribute("message_title2", "Thêm mới camera trước");
            return "admin/ql_thuoc_tinh/camera_truoc/add";
        }else {
            CameraTruoc cameraTruoc = new CameraTruoc();
            cameraTruoc.setTen(ten);
            cameraTruoc.setTrangThai(1);
            cameraTruocRepository.save(cameraTruoc);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/cameraTruoc/index";
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") BigInteger id)
    {
        int currentStatus = cameraTruocRepository.findById(id).get().getTrangThai();
        if (currentStatus == 1) {
            cameraTruocRepository.changeStatus(0, id);
        } else {
            cameraTruocRepository.changeStatus(1, id);
        }
        return "redirect:/cameraTruoc/index";
    }

    @GetMapping("search")
    public String search(
            @RequestParam("search") String ten,
            Model model,
            @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<CameraTruoc> findSearch = cameraTruocRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("search", ten);
        model.addAttribute("checkSearch", true);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách camera trước");
        return "admin/ql_thuoc_tinh/camera_truoc/index";
    }


    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {
        Optional<CameraTruoc> cameraSau = cameraTruocRepository.findById(id);
        CameraTruoc c = cameraTruocRepository.getReferenceById(id);
        model.addAttribute("cameraTruoc", c);
        model.addAttribute("message_title1", "Danh sách camera trước");
        model.addAttribute("message_title2", "Cập nhật camera trước");
        return "admin/ql_thuoc_tinh/camera_truoc/update";
    }

    @PostMapping("/update/{id}")
    public String update(
            Model model,
            @PathVariable("id") BigInteger id,
            @RequestParam("ten") String ten,
            RedirectAttributes redirectAttributes)
    {
        CameraTruoc cameraTruocOld = cameraTruocRepository.getReferenceById(id);
        if (ten.trim().length() == 0 || (cameraTruocRepository.findByTenIsLike(ten.trim()) != null  && !ten.trim().equals(cameraTruocOld.getTen()))) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(cameraTruocRepository.findByTenIsLike(ten.trim()) != null  && !ten.trim().equals(cameraTruocOld.getTen())){
                model.addAttribute("loi",  "Loi_Trung!");

            }
            cameraTruocOld.setTen(ten);
            model.addAttribute("cameraTruoc", cameraTruocOld);
            model.addAttribute("message_title1", "Danh sách camera trước");
            model.addAttribute("message_title2", "Cập nhật camera trước");
            return "admin/ql_thuoc_tinh/camera_truoc/update";
        }else {

            cameraTruocOld.setTen(ten);
            cameraTruocRepository.save(cameraTruocOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/cameraTruoc/index";
        }
    }

    @PostMapping("/quick-store")
    public Object quickStore(@RequestParam("ten") String ten,
                        Model model,
                        @RequestParam("page") Optional<Integer> pageParam,
                        RedirectAttributes redirectAttributes)
    {

        if (ten.trim().length() == 0 || (cameraTruocRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
                return ResponseEntity.ok(1);
            }
            else {
                model.addAttribute("loi",  "Loi_Trung!");
                return ResponseEntity.ok(2);
            }
        }else {
            System.out.println("Tên cam "+ten);
            CameraTruoc cameraTruoc = new CameraTruoc();
            cameraTruoc.setTen(ten);
            cameraTruoc.setTrangThai(1);
            cameraTruocRepository.save(cameraTruoc);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return ResponseEntity.ok(3);
        }
    }


}
