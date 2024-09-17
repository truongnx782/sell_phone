package com.example.demo.Controller;


import com.example.demo.Entitys.Chip;
import com.example.demo.Repository.ChipRepository;
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
@RequestMapping("chip")
public class ChipController {

    @Autowired
    private ChipRepository chipRepository;

    @GetMapping("/index")
    public String index(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            RedirectAttributes redirectAttributes)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Chip> data = chipRepository.findAll(pageable);
        model.addAttribute("data", data);
        // Kiểm tra xem có thông báo "success" trong redirectAttributes hay không
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách chip");
        return "admin/ql_thuoc_tinh/chip/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách chip");
        model.addAttribute("message_title2", "Thêm mới chip");
        return "admin/ql_thuoc_tinh/chip/add";
    }

    @PostMapping("/store")
    public String store(
            @RequestParam("ten") String ten,
            Model model,
            RedirectAttributes redirectAttributes)
    {

        if (ten.trim().length() == 0 || (chipRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(chipRepository.findByTenIsLike(ten.trim()) != null){
                model.addAttribute("loi",  "Loi_Trung!");
                model.addAttribute("ten", ten);
            }
            model.addAttribute("message_title1", "Danh sách chip");
            model.addAttribute("message_title2", "Thêm mới chip");
            return "admin/ql_thuoc_tinh/chip/add";
        }else {
            Chip chip = new Chip();
            chip.setTen(ten);
            chip.setTrangThai(1);
            chipRepository.save(chip);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/chip/index";
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") BigInteger id)
    {
        int currentStatus = chipRepository.findById(id).get().getTrangThai();
        if (currentStatus == 1) {
            chipRepository.changeStatus(0, id);
        } else {
            chipRepository.changeStatus(1, id);
        }
        return "redirect:/chip/index";
    }

    @GetMapping("search")
    public String search(
            @RequestParam("search") String ten,
            Model model,
            @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Chip> findSearch = chipRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("search", ten);
        model.addAttribute("checkSearch", true);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách chip");
        return "admin/ql_thuoc_tinh/chip/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {
        Optional<Chip> chip = chipRepository.findById(id);
        Chip c = chipRepository.getReferenceById(id);
        model.addAttribute("chip", c);
        model.addAttribute("message_title1", "Danh sách chip");
        model.addAttribute("message_title2", "Cập nhật chip");
        return "admin/ql_thuoc_tinh/chip/update";
    }

    @PostMapping("/update/{id}")
    public String update(
            Model model,
            @PathVariable("id") BigInteger id,
            @RequestParam("ten") String ten,
            RedirectAttributes redirectAttributes)
    {
        Chip chipOld = chipRepository.getReferenceById(id);

        if (ten.trim().length() == 0 || (chipRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(chipOld.getTen()))) {
            if (ten.trim().length() == 0) {
                model.addAttribute("loi", "Loi_Trong!");
            } else if (chipRepository.findByTenIsLike(ten.trim()) != null) {
                model.addAttribute("loi", "Loi_Trung!");
            }
            chipOld.setTen(ten);
            model.addAttribute("chip", chipOld);
            model.addAttribute("message_title1", "Danh sách chip");
            model.addAttribute("message_title2", "Cập nhật chip");
            return "admin/ql_thuoc_tinh/chip/update";
        } else {
            chipOld.setTen(ten);
            chipRepository.save(chipOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/chip/index";
        }
    }


    @PostMapping("/quick-store")
    public Object quickSave(
            @RequestParam("ten") String ten,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (ten.trim().isEmpty() || chipRepository.findByTenIsLike(ten.trim()) != null) {
            if (ten.trim().isEmpty()) {
                return ResponseEntity.ok(1);
            } else {
                model.addAttribute("loi", "Loi_Trung!");
                model.addAttribute("ten", ten);
                return ResponseEntity.ok(2);
            }
        } else {
            Chip chip = new Chip();
            chip.setTen(ten);
            chip.setTrangThai(1);
            chipRepository.save(chip);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return ResponseEntity.ok(3);
        }
    }
}
