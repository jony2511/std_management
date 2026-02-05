package com.example.demo.service;

import com.example.demo.dto.TeacherDTO;
import com.example.demo.entity.Dept;
import com.example.demo.entity.Role;
import com.example.demo.entity.Teacher;
import com.example.demo.repository.DeptRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final DeptRepository deptRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TeacherDTO getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
        return convertToDTO(teacher);
    }

    public TeacherDTO getTeacherByUsername(String username) {
        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found with username: " + username));
        return convertToDTO(teacher);
    }

    public TeacherDTO createTeacher(TeacherDTO teacherDTO) {
        if (userRepository.existsByUsername(teacherDTO.getUsername())) {
            throw new RuntimeException("Username " + teacherDTO.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail(teacherDTO.getEmail())) {
            throw new RuntimeException("Email " + teacherDTO.getEmail() + " already exists");
        }
        if (teacherRepository.existsByEmployeeId(teacherDTO.getEmployeeId())) {
            throw new RuntimeException("Employee ID " + teacherDTO.getEmployeeId() + " already exists");
        }

        Teacher teacher = new Teacher();
        teacher.setUsername(teacherDTO.getUsername());
        teacher.setPassword(passwordEncoder.encode(teacherDTO.getPassword()));
        teacher.setEmail(teacherDTO.getEmail());
        teacher.setFirstName(teacherDTO.getFirstName());
        teacher.setLastName(teacherDTO.getLastName());
        teacher.setPhone(teacherDTO.getPhone());
        teacher.setEmployeeId(teacherDTO.getEmployeeId());
        teacher.setSpecialization(teacherDTO.getSpecialization());
        teacher.setRole(Role.TEACHER);
        teacher.setEnabled(true);

        if (teacherDTO.getDepartmentId() != null) {
            Dept dept = deptRepository.findById(teacherDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            teacher.setDepartment(dept);
        }

        teacher = teacherRepository.save(teacher);
        return convertToDTO(teacher);
    }

    public TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));

        teacher.setEmail(teacherDTO.getEmail());
        teacher.setFirstName(teacherDTO.getFirstName());
        teacher.setLastName(teacherDTO.getLastName());
        teacher.setPhone(teacherDTO.getPhone());
        teacher.setSpecialization(teacherDTO.getSpecialization());

        if (teacherDTO.getPassword() != null && !teacherDTO.getPassword().isEmpty()) {
            teacher.setPassword(passwordEncoder.encode(teacherDTO.getPassword()));
        }

        if (teacherDTO.getDepartmentId() != null) {
            Dept dept = deptRepository.findById(teacherDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            teacher.setDepartment(dept);
        }

        teacher = teacherRepository.save(teacher);
        return convertToDTO(teacher);
    }

    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new RuntimeException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    private TeacherDTO convertToDTO(Teacher teacher) {
        return TeacherDTO.builder()
                .id(teacher.getId())
                .username(teacher.getUsername())
                .email(teacher.getEmail())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .phone(teacher.getPhone())
                .employeeId(teacher.getEmployeeId())
                .specialization(teacher.getSpecialization())
                .departmentId(teacher.getDepartment() != null ? teacher.getDepartment().getId() : null)
                .departmentName(teacher.getDepartment() != null ? teacher.getDepartment().getName() : null)
                .build();
    }
}
