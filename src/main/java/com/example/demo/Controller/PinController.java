package com.example.demo.Controller;

import com.example.demo.Entitys.Pin;
import com.example.demo.Repository.PinRepository;
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
@RequestMapping("pin")
public class PinController {

    @Autowired
    private PinRepository pinRepository;

    @GetMapping("/index")
    public String index(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            RedirectAttributes redirectAttributes)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Pin> data = pinRepository.findAll(pageable);
        model.addAttribute("data", data);
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý pin");
        model.addAttribute("message_title2", "Danh sách pin");

        return "admin/ql_thuoc_tinh/pin/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách pin");
        model.addAttribute("message_title2", "Thêm mới pin");
        return "admin/ql_thuoc_tinh/pin/add";
    }

    @PostMapping("/store")
    public String store(@RequestParam("ten") String ten,
                        RedirectAttributes redirectAttributes,
                        Model model)
    {
        if (ten.trim().length() == 0 || (pinRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(pinRepository.findByTenIsLike(ten.trim()) != null){
                model.addAttribute("loi",  "Loi_Trung!");
                model.addAttribute("ten", ten);
            }
            model.addAttribute("message_title1", "Danh sách pin");
            model.addAttribute("message_title2", "Thêm mới pin");
            return "admin/ql_thuoc_tinh/pin/add";
        }else {
            Pin pin = new Pin();
            pin.setTen(ten);
            pin.setTrangThai(1);
            pinRepository.save(pin);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/pin/index";
        }
    }


    @GetMapping("search")
    public String search(@RequestParam("search") String ten, Model model, @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Pin> findSearch = pinRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("search", ten);
        model.addAttribute("checkSearch", true);
        model.addAttribute("message_title1", "Quản lý pin");
        model.addAttribute("message_title2", "Danh sách pin");
        return "admin/ql_thuoc_tinh/pin/index";
    }


    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {
        Pin pin = pinRepository.getReferenceById(id);
        model.addAttribute("pin", pin);
        model.addAttribute("message_title1", "Danh sách pin");
        model.addAttribute("message_title2", "Cập nhật pin");
        return "admin/ql_thuoc_tinh/pin/update";
    }

    @PostMapping("/update/{id}")
    public String update(
            Model model,
            @PathVariable("id") BigInteger id,
            @RequestParam("ten") String ten,
            RedirectAttributes redirectAttributes)
    {

        Pin pinOld = pinRepository.getReferenceById(id);
        if (ten.trim().length() == 0 || (pinRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(pinOld.getTen()))) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(pinRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(pinOld.getTen())){
                model.addAttribute("loi",  "Loi_Trung!");
            }
            pinOld.setTen(ten);
            model.addAttribute("pin", pinOld);
            model.addAttribute("message_title1", "Danh sách pin");
            model.addAttribute("message_title2", "Cập nhật pin");
            return "admin/ql_thuoc_tinh/pin/update";
        }else {
            pinOld.setTen(ten);
            pinRepository.save(pinOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/pin/index";
        }
    }

    @PostMapping("/quick-store")
    public Object quickStore(@RequestParam("ten") String ten,
                        RedirectAttributes redirectAttributes,
                        Model model)
    {
        if (ten.trim().length() == 0 || (pinRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
                return ResponseEntity.ok(1);
            }
            else {
                model.addAttribute("loi",  "Loi_Trung!");
                model.addAttribute("ten", ten);
                return ResponseEntity.ok(2);
            }
        }else {
            Pin pin = new Pin();
            pin.setTen(ten);
            pin.setTrangThai(1);
            pinRepository.save(pin);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return  ResponseEntity.ok(3);
        }
    }

}
