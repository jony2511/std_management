package com.example.demo.service;

import com.example.demo.dto.TeacherDTO;
import com.example.demo.entity.Dept;
import com.example.demo.entity.Role;
import com.example.demo.entity.Teacher;
import com.example.demo.repository.DeptRepository;
import com.example.demo.repository.TeacherRepository;
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
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private DeptRepository deptRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;
    private TeacherDTO teacherDTO;
    private Dept department;

    @BeforeEach
    void setUp() {
        department = Dept.builder()
                .id(1L)
                .name("Computer Science")
                .description("CS Department")
                .build();

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUsername("jane.smith");
        teacher.setPassword("encodedPassword");
        teacher.setEmail("jane.smith@example.com");
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");
        teacher.setPhone("9876543210");
        teacher.setEmployeeId("EMP001");
        teacher.setSpecialization("Data Science");
        teacher.setRole(Role.TEACHER);
        teacher.setEnabled(true);
        teacher.setDepartment(department);

        teacherDTO = TeacherDTO.builder()
                .id(1L)
                .username("jane.smith")
                .password("password123")
                .email("jane.smith@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .phone("9876543210")
                .employeeId("EMP001")
                .specialization("Data Science")
                .departmentId(1L)
                .departmentName("Computer Science")
                .build();
    }

    @Test
    void testGetAllTeachers() {
        when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher));

        List<TeacherDTO> result = teacherService.getAllTeachers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("jane.smith", result.get(0).getUsername());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void testGetTeacherById_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        TeacherDTO result = teacherService.getTeacherById(1L);

        assertNotNull(result);
        assertEquals("jane.smith", result.getUsername());
        assertEquals("EMP001", result.getEmployeeId());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTeacherById_NotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> teacherService.getTeacherById(1L));

        assertTrue(exception.getMessage().contains("Teacher not found"));
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTeacherByUsername_Success() {
        when(teacherRepository.findByUsername("jane.smith")).thenReturn(Optional.of(teacher));

        TeacherDTO result = teacherService.getTeacherByUsername("jane.smith");

        assertNotNull(result);
        assertEquals("jane.smith", result.getUsername());
        verify(teacherRepository, times(1)).findByUsername("jane.smith");
    }

    @Test
    void testGetTeacherByUsername_NotFound() {
        when(teacherRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> teacherService.getTeacherByUsername("unknown"));

        assertTrue(exception.getMessage().contains("Teacher not found"));
    }

    @Test
    void testCreateTeacher_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(teacherRepository.existsByEmployeeId(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(deptRepository.findById(1L)).thenReturn(Optional.of(department));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        TeacherDTO result = teacherService.createTeacher(teacherDTO);

        assertNotNull(result);
        assertEquals("jane.smith", result.getUsername());
        verify(teacherRepository, times(1)).save(any(Teacher.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void testCreateTeacher_UsernameExists() {
        when(userRepository.existsByUsername("jane.smith")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> teacherService.createTeacher(teacherDTO));

        assertTrue(exception.getMessage().contains("Username"));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    void testCreateTeacher_EmailExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("jane.smith@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> teacherService.createTeacher(teacherDTO));

        assertTrue(exception.getMessage().contains("Email"));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    void testCreateTeacher_EmployeeIdExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(teacherRepository.existsByEmployeeId("EMP001")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> teacherService.createTeacher(teacherDTO));

        assertTrue(exception.getMessage().contains("Employee ID"));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    void testUpdateTeacher_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(deptRepository.findById(1L)).thenReturn(Optional.of(department));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        teacherDTO.setPhone("1234567890");
        teacherDTO.setSpecialization("Machine Learning");

        TeacherDTO result = teacherService.updateTeacher(1L, teacherDTO);

        assertNotNull(result);
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    void testUpdateTeacher_WithoutPassword() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(deptRepository.findById(1L)).thenReturn(Optional.of(department));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        teacherDTO.setPassword(null);

        TeacherDTO result = teacherService.updateTeacher(1L, teacherDTO);

        assertNotNull(result);
        verify(teacherRepository, times(1)).save(any(Teacher.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testUpdateTeacher_NotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> teacherService.updateTeacher(1L, teacherDTO));

        assertTrue(exception.getMessage().contains("Teacher not found"));
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    void testDeleteTeacher_Success() {
        when(teacherRepository.existsById(1L)).thenReturn(true);
        doNothing().when(teacherRepository).deleteById(1L);

        teacherService.deleteTeacher(1L);

        verify(teacherRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTeacher_NotFound() {
        when(teacherRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> teacherService.deleteTeacher(1L));

        assertTrue(exception.getMessage().contains("Teacher not found"));
        verify(teacherRepository, never()).deleteById(anyLong());
    }
}

