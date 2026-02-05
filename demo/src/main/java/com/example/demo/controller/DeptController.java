package com.example.demo.controller;

import com.example.demo.dto.DeptDTO;
import com.example.demo.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class DeptController {

    private final DeptService deptService;

    @GetMapping
    public String listDepartments(Model model) {
        model.addAttribute("departments", deptService.getAllDepartments());
        return "departments/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("department", new DeptDTO());
        return "departments/form";
    }

    @PostMapping("/create")
    public String createDepartment(@ModelAttribute DeptDTO deptDTO, RedirectAttributes redirectAttributes) {
        try {
            deptService.createDepartment(deptDTO);
            redirectAttributes.addFlashAttribute("success", "Department created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("department", deptService.getDepartmentById(id));
        return "departments/form";
    }

    @PostMapping("/edit/{id}")
    public String updateDepartment(@PathVariable Long id, @ModelAttribute DeptDTO deptDTO,
            RedirectAttributes redirectAttributes) {
        try {
            deptService.updateDepartment(id, deptDTO);
            redirectAttributes.addFlashAttribute("success", "Department updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departments";
    }

    @PostMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            deptService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("success", "Department deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departments";
    }
}
