package com.example.demo.controller;

import com.example.demo.dto.StudentDTO;
import com.example.demo.service.CourseService;
import com.example.demo.service.DeptService;
import com.example.demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final DeptService deptService;
    private final CourseService courseService;

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students/list";
    }

    @GetMapping("/view/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "students/view";
    }

    // Only TEACHER can create students
    @GetMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new StudentDTO());
        model.addAttribute("departments", deptService.getAllDepartments());
        return "students/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String createStudent(@ModelAttribute StudentDTO studentDTO, RedirectAttributes redirectAttributes) {
        try {
            studentService.createStudent(studentDTO);
            redirectAttributes.addFlashAttribute("success", "Student created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/students";
    }

    // Teacher can edit any student
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        model.addAttribute("departments", deptService.getAllDepartments());
        return "students/form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateStudent(@PathVariable Long id, @ModelAttribute StudentDTO studentDTO,
            RedirectAttributes redirectAttributes) {
        try {
            studentService.updateStudent(id, studentDTO);
            redirectAttributes.addFlashAttribute("success", "Student updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/students";
    }

    // Student can view and update their own profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public String viewOwnProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentDTO student = studentService.getStudentByUsername(userDetails.getUsername());
        model.addAttribute("student", student);
        return "students/profile";
    }

    @GetMapping("/profile/edit")
    @PreAuthorize("hasRole('STUDENT')")
    public String showEditProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        StudentDTO student = studentService.getStudentByUsername(userDetails.getUsername());
        model.addAttribute("student", student);
        return "students/profile-edit";
    }

    @PostMapping("/profile/edit")
    @PreAuthorize("hasRole('STUDENT')")
    public String updateOwnProfile(@AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute StudentDTO studentDTO,
            RedirectAttributes redirectAttributes) {
        try {
            StudentDTO currentStudent = studentService.getStudentByUsername(userDetails.getUsername());
            studentService.updateStudentProfile(currentStudent.getId(), studentDTO, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/students/profile";
    }

    // Enroll in a course
    @PostMapping("/enroll/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public String enrollInCourse(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId,
            RedirectAttributes redirectAttributes) {
        try {
            StudentDTO student = studentService.getStudentByUsername(userDetails.getUsername());
            studentService.enrollInCourse(student.getId(), courseId);
            redirectAttributes.addFlashAttribute("success", "Enrolled in course successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses";
    }
}
