package com.example.demo.Controller;

import com.example.demo.Entitys.CameraSau;

import com.example.demo.Entitys.Sim;
import com.example.demo.Repository.SimRepository;
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
@RequestMapping("sim")
public class SimController {

    @Autowired
    private SimRepository simRepository;

    @GetMapping("/index")
    public String index( Model model,
                         @RequestParam("page") Optional<Integer> pageParam,
                         RedirectAttributes redirectAttributes)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Sim> data = simRepository.findAll(pageable);
        model.addAttribute("data", data);
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách sim");
        return "admin/ql_thuoc_tinh/sim/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách sim");
        model.addAttribute("message_title2", "Thêm mới sim");
        return "admin/ql_thuoc_tinh/sim/add";
    }

    @PostMapping("/store")
    public String store(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {
        if (ten.trim().length() == 0 || (simRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(simRepository.findByTenIsLike(ten.trim()) != null){
                model.addAttribute("loi",  "Loi_Trung!");
            }
            model.addAttribute("ten", ten);
            model.addAttribute("message_title1", "Danh sách sim");
            model.addAttribute("message_title2", "Thêm mới sim");
            return "admin/ql_thuoc_tinh/sim/add";
        }else {
            Sim sim = new Sim();
            sim.setTen(ten);
            sim.setTrangThai(1);
            simRepository.save(sim);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/sim/index";
        }
    }


    @GetMapping("search")
    public String search(@RequestParam("search") String ten, Model model, @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Sim> findSearch = simRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("search", ten);
        model.addAttribute("checkSearch", true);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách sim");
        return "admin/ql_thuoc_tinh/sim/index";
    }


    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {
        Sim sim = simRepository.getReferenceById(id);
        model.addAttribute("sim", sim);
        model.addAttribute("message_title1", "Danh sách sim");
        model.addAttribute("message_title2", "Cập nhật sim");
        return "admin/ql_thuoc_tinh/sim/update";
    }

    @PostMapping("/update/{id}")
    public String update( Model model,
                          @PathVariable("id") BigInteger id,
                          @RequestParam("ten") String ten,
                          RedirectAttributes redirectAttributes)
    {
        Sim simOld = simRepository.getReferenceById(id);
        if (ten.trim().length() == 0 || (simRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(simOld.getTen()))) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(simRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(simOld.getTen())){
                model.addAttribute("loi",  "Loi_Trung!");
            }
            simOld.setTen(ten);
            model.addAttribute("sim", simOld);
            model.addAttribute("message_title1", "Danh sách sim");
            model.addAttribute("message_title2", "Cập nhật sim");
            return "admin/ql_thuoc_tinh/sim/update";
        }else {
            simOld.setTen(ten);
            simRepository.save(simOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/sim/index";
        }
    }

    @PostMapping("/quick-store")
    public Object quickStore(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {
        if (ten.trim().length() == 0 || (simRepository.findByTenIsLike(ten.trim()) != null)) {
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
            Sim sim = new Sim();
            sim.setTen(ten);
            sim.setTrangThai(1);
            simRepository.save(sim);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return ResponseEntity.ok(3);
        }
    }
}
