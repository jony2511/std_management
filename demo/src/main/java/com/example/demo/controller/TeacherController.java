package com.example.demo.controller;

import com.example.demo.dto.TeacherDTO;
import com.example.demo.service.DeptService;
import com.example.demo.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teachers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final TeacherService teacherService;
    private final DeptService deptService;

    @GetMapping
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teachers/list";
    }

    @GetMapping("/view/{id}")
    public String viewTeacher(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        return "teachers/view";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("teacher", new TeacherDTO());
        model.addAttribute("departments", deptService.getAllDepartments());
        return "teachers/form";
    }

    @PostMapping("/create")
    public String createTeacher(@ModelAttribute TeacherDTO teacherDTO, RedirectAttributes redirectAttributes) {
        try {
            teacherService.createTeacher(teacherDTO);
            redirectAttributes.addFlashAttribute("success", "Teacher created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/teachers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        model.addAttribute("departments", deptService.getAllDepartments());
        return "teachers/form";
    }

    @PostMapping("/edit/{id}")
    public String updateTeacher(@PathVariable Long id, @ModelAttribute TeacherDTO teacherDTO,
            RedirectAttributes redirectAttributes) {
        try {
            teacherService.updateTeacher(id, teacherDTO);
            redirectAttributes.addFlashAttribute("success", "Teacher updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/teachers";
    }

    @PostMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            teacherService.deleteTeacher(id);
            redirectAttributes.addFlashAttribute("success", "Teacher deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/teachers";
    }
}
