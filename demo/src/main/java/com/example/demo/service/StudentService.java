package com.example.demo.service;

import com.example.demo.dto.StudentDTO;
import com.example.demo.entity.Course;
import com.example.demo.entity.Dept;
import com.example.demo.entity.Role;
import com.example.demo.entity.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DeptRepository;
import com.example.demo.repository.StudentRepository;
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
public class StudentService {

    private final StudentRepository studentRepository;
    private final DeptRepository deptRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return convertToDTO(student);
    }

    public StudentDTO getStudentByUsername(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found with username: " + username));
        return convertToDTO(student);
    }

    public List<StudentDTO> getStudentsByDepartment(Long departmentId) {
        return studentRepository.findByDepartmentId(departmentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Only TEACHER can create student profiles
    public StudentDTO createStudent(StudentDTO studentDTO) {
        if (userRepository.existsByUsername(studentDTO.getUsername())) {
            throw new RuntimeException("Username " + studentDTO.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail(studentDTO.getEmail())) {
            throw new RuntimeException("Email " + studentDTO.getEmail() + " already exists");
        }
        if (studentRepository.existsByStudentId(studentDTO.getStudentId())) {
            throw new RuntimeException("Student ID " + studentDTO.getStudentId() + " already exists");
        }

        Student student = new Student();
        student.setUsername(studentDTO.getUsername());
        student.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        student.setEmail(studentDTO.getEmail());
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setPhone(studentDTO.getPhone());
        student.setStudentId(studentDTO.getStudentId());
        student.setYear(studentDTO.getYear());
        student.setAddress(studentDTO.getAddress());
        student.setRole(Role.STUDENT);
        student.setEnabled(true);

        if (studentDTO.getDepartmentId() != null) {
            Dept dept = deptRepository.findById(studentDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            student.setDepartment(dept);
        }

        student = studentRepository.save(student);
        return convertToDTO(student);
    }

    // Student can update their own profile (limited fields)
    public StudentDTO updateStudentProfile(Long id, StudentDTO studentDTO, String currentUsername) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        // Check if the student is updating their own profile
        if (!student.getUsername().equals(currentUsername)) {
            throw new RuntimeException("You can only update your own profile");
        }

        // Students can only update limited fields
        student.setEmail(studentDTO.getEmail());
        student.setPhone(studentDTO.getPhone());
        student.setAddress(studentDTO.getAddress());

        if (studentDTO.getPassword() != null && !studentDTO.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        }

        student = studentRepository.save(student);
        return convertToDTO(student);
    }

    // Teacher can update all student fields
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        student.setEmail(studentDTO.getEmail());
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setPhone(studentDTO.getPhone());
        student.setYear(studentDTO.getYear());
        student.setAddress(studentDTO.getAddress());

        if (studentDTO.getPassword() != null && !studentDTO.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        }

        if (studentDTO.getDepartmentId() != null) {
            Dept dept = deptRepository.findById(studentDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            student.setDepartment(dept);
        }

        student = studentRepository.save(student);
        return convertToDTO(student);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    // Enroll student in a course (M:M relationship)
    public StudentDTO enrollInCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.getCourses().add(course);
        student = studentRepository.save(student);
        return convertToDTO(student);
    }

    // Unenroll student from a course
    public StudentDTO unenrollFromCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.getCourses().remove(course);
        student = studentRepository.save(student);
        return convertToDTO(student);
    }

    private StudentDTO convertToDTO(Student student) {
        return StudentDTO.builder()
                .id(student.getId())
                .username(student.getUsername())
                .email(student.getEmail())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .phone(student.getPhone())
                .studentId(student.getStudentId())
                .year(student.getYear())
                .address(student.getAddress())
                .departmentId(student.getDepartment() != null ? student.getDepartment().getId() : null)
                .departmentName(student.getDepartment() != null ? student.getDepartment().getName() : null)
                .build();
    }
}
