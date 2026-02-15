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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private DeptRepository deptRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private StudentDTO studentDTO;
    private Dept department;
    private Course course;

    @BeforeEach
    void setUp() {
        department = Dept.builder()
                .id(1L)
                .name("Computer Science")
                .description("CS Department")
                .build();

        student = Student.builder()
                .id(1L)
                .username("john.doe")
                .password("encodedPassword")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .studentId("STU001")
                .year(2)
                .address("123 Main St")
                .department(department)
                .role(Role.STUDENT)
                .enabled(true)
                .build();

        studentDTO = StudentDTO.builder()
                .id(1L)
                .username("john.doe")
                .password("password123")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .studentId("STU001")
                .year(2)
                .address("123 Main St")
                .departmentId(1L)
                .departmentName("Computer Science")
                .build();

        course = new Course();
        course.setId(1L);
        course.setName("Data Structures");
        course.setCode("CS101");
    }

    @Test
    void testGetAllStudents() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student));

        List<StudentDTO> result = studentService.getAllStudents();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john.doe", result.get(0).getUsername());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testGetStudentById_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentDTO result = studentService.getStudentById(1L);

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        assertEquals("STU001", result.getStudentId());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetStudentById_NotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.getStudentById(1L));

        assertTrue(exception.getMessage().contains("Student not found"));
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetStudentByUsername_Success() {
        when(studentRepository.findByUsername("john.doe")).thenReturn(Optional.of(student));

        StudentDTO result = studentService.getStudentByUsername("john.doe");

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        verify(studentRepository, times(1)).findByUsername("john.doe");
    }

    @Test
    void testGetStudentByUsername_NotFound() {
        when(studentRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.getStudentByUsername("unknown"));

        assertTrue(exception.getMessage().contains("Student not found"));
    }

    @Test
    void testGetStudentsByDepartment() {
        when(studentRepository.findByDepartmentId(1L)).thenReturn(Arrays.asList(student));

        List<StudentDTO> result = studentService.getStudentsByDepartment(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepository, times(1)).findByDepartmentId(1L);
    }

    @Test
    void testCreateStudent_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByStudentId(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(deptRepository.findById(1L)).thenReturn(Optional.of(department));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentDTO result = studentService.createStudent(studentDTO);

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void testCreateStudent_UsernameExists() {
        when(userRepository.existsByUsername("john.doe")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.createStudent(studentDTO));

        assertTrue(exception.getMessage().contains("Username"));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testCreateStudent_EmailExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.createStudent(studentDTO));

        assertTrue(exception.getMessage().contains("Email"));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testCreateStudent_StudentIdExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByStudentId("STU001")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.createStudent(studentDTO));

        assertTrue(exception.getMessage().contains("Student ID"));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testUpdateStudentProfile_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        studentDTO.setPhone("9876543210");
        studentDTO.setAddress("456 Oak Ave");

        StudentDTO result = studentService.updateStudentProfile(1L, studentDTO, "john.doe");

        assertNotNull(result);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testUpdateStudentProfile_Unauthorized() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.updateStudentProfile(1L, studentDTO, "other.user"));

        assertTrue(exception.getMessage().contains("only update your own profile"));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testUpdateStudent_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(deptRepository.findById(1L)).thenReturn(Optional.of(department));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        StudentDTO result = studentService.updateStudent(1L, studentDTO);

        assertNotNull(result);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testDeleteStudent_Success() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        studentService.deleteStudent(1L);

        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteStudent_NotFound() {
        when(studentRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.deleteStudent(1L));

        assertTrue(exception.getMessage().contains("Student not found"));
        verify(studentRepository, never()).deleteById(anyLong());
    }

    @Test
    void testEnrollInCourse_Success() {
        student.setCourses(new HashSet<>());
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentDTO result = studentService.enrollInCourse(1L, 1L);

        assertNotNull(result);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testEnrollInCourse_StudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.enrollInCourse(1L, 1L));

        assertTrue(exception.getMessage().contains("Student not found"));
    }

    @Test
    void testEnrollInCourse_CourseNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.enrollInCourse(1L, 1L));

        assertTrue(exception.getMessage().contains("Course not found"));
    }

    @Test
    void testUnenrollFromCourse_Success() {
        student.setCourses(new HashSet<>(Arrays.asList(course)));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentDTO result = studentService.unenrollFromCourse(1L, 1L);

        assertNotNull(result);
        verify(studentRepository, times(1)).save(any(Student.class));
    }
}

