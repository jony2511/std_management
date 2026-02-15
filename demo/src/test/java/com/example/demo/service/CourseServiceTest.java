package com.example.demo.service;

import com.example.demo.dto.CourseDTO;
import com.example.demo.entity.Course;
import com.example.demo.entity.Dept;
import com.example.demo.entity.Teacher;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DeptRepository;
import com.example.demo.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private DeptRepository deptRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private CourseDTO courseDTO;
    private Dept department;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        department = Dept.builder()
                .id(1L)
                .name("Computer Science")
                .description("CS Department")
                .build();

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");

        course = new Course();
        course.setId(1L);
        course.setName("Data Structures");
        course.setCode("CS101");
        course.setDescription("Introduction to Data Structures");
        course.setCredits(3);
        course.setDepartment(department);
        course.setTeacher(teacher);

        courseDTO = CourseDTO.builder()
                .id(1L)
                .name("Data Structures")
                .code("CS101")
                .description("Introduction to Data Structures")
                .credits(3)
                .departmentId(1L)
                .departmentName("Computer Science")
                .teacherId(1L)
                .teacherName("Jane Smith")
                .build();
    }

    @Test
    void testGetAllCourses() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course));

        List<CourseDTO> result = courseService.getAllCourses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getCode());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testGetCourseById_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseDTO result = courseService.getCourseById(1L);

        assertNotNull(result);
        assertEquals("CS101", result.getCode());
        assertEquals("Data Structures", result.getName());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCourseById_NotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseService.getCourseById(1L));

        assertTrue(exception.getMessage().contains("Course not found"));
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCoursesByDepartment() {
        when(courseRepository.findByDepartmentId(1L)).thenReturn(Arrays.asList(course));

        List<CourseDTO> result = courseService.getCoursesByDepartment(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getCode());
        verify(courseRepository, times(1)).findByDepartmentId(1L);
    }

    @Test
    void testCreateCourse_Success() {
        when(courseRepository.existsByCode("CS101")).thenReturn(false);
        when(deptRepository.findById(1L)).thenReturn(Optional.of(department));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDTO result = courseService.createCourse(courseDTO);

        assertNotNull(result);
        assertEquals("CS101", result.getCode());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testCreateCourse_CodeExists() {
        when(courseRepository.existsByCode("CS101")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseService.createCourse(courseDTO));

        assertTrue(exception.getMessage().contains("Course with code"));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testCreateCourse_DepartmentNotFound() {
        when(courseRepository.existsByCode("CS101")).thenReturn(false);
        when(deptRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseService.createCourse(courseDTO));

        assertTrue(exception.getMessage().contains("Department not found"));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testCreateCourse_TeacherNotFound() {
        when(courseRepository.existsByCode("CS101")).thenReturn(false);
        when(deptRepository.findById(1L)).thenReturn(Optional.of(department));
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseService.createCourse(courseDTO));

        assertTrue(exception.getMessage().contains("Teacher not found"));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testCreateCourse_WithoutDepartment() {
        courseDTO.setDepartmentId(null);
        courseDTO.setTeacherId(null);
        when(courseRepository.existsByCode("CS101")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDTO result = courseService.createCourse(courseDTO);

        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
        verify(deptRepository, never()).findById(anyLong());
        verify(teacherRepository, never()).findById(anyLong());
    }

    @Test
    void testUpdateCourse_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(deptRepository.findById(1L)).thenReturn(Optional.of(department));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        courseDTO.setName("Advanced Data Structures");

        CourseDTO result = courseService.updateCourse(1L, courseDTO);

        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testUpdateCourse_NotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseService.updateCourse(1L, courseDTO));

        assertTrue(exception.getMessage().contains("Course not found"));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testDeleteCourse_Success() {
        when(courseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(1L);

        courseService.deleteCourse(1L);

        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCourse_NotFound() {
        when(courseRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseService.deleteCourse(1L));

        assertTrue(exception.getMessage().contains("Course not found"));
        verify(courseRepository, never()).deleteById(anyLong());
    }
}

