package com.example.demo.repository;

import com.example.demo.entity.Dept;
import com.example.demo.entity.Role;
import com.example.demo.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudentRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    private Student student;
    private Dept department;

    @BeforeEach
    void setUp() {
        // Clear existing data
        studentRepository.deleteAll();
        entityManager.flush();

        department = Dept.builder()
                .name("CS Test Dept")
                .description("CS Department")
                .build();
        entityManager.persist(department);
        entityManager.flush();

        student = Student.builder()
                .username("john.doe")
                .password("password123")
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
    }

    @Test
    void testSaveStudent() {
        Student savedStudent = studentRepository.save(student);

        assertNotNull(savedStudent);
        assertNotNull(savedStudent.getId());
        assertEquals("john.doe", savedStudent.getUsername());
        assertEquals("STU001", savedStudent.getStudentId());
    }

    @Test
    void testFindById() {
        entityManager.persist(student);
        entityManager.flush();
        Student savedStudent = student;

        Optional<Student> found = studentRepository.findById(savedStudent.getId());

        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUsername());
    }

    @Test
    void testFindByUsername_Found() {
        entityManager.persist(student);
        entityManager.flush();

        Optional<Student> found = studentRepository.findByUsername("john.doe");

        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUsername());
        assertEquals("STU001", found.get().getStudentId());
    }

    @Test
    void testFindByUsername_NotFound() {
        Optional<Student> found = studentRepository.findByUsername("unknown");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByStudentId_Found() {
        entityManager.persist(student);
        entityManager.flush();

        Optional<Student> found = studentRepository.findByStudentId("STU001");

        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUsername());
    }

    @Test
    void testFindByStudentId_NotFound() {
        Optional<Student> found = studentRepository.findByStudentId("STU999");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByDepartmentId() {
        entityManager.persist(student);
        entityManager.flush();

        Student student2 = Student.builder()
                .username("jane.doe")
                .password("password123")
                .email("jane.doe@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .phone("9876543210")
                .studentId("STU002")
                .year(3)
                .address("456 Oak Ave")
                .department(department)
                .role(Role.STUDENT)
                .enabled(true)
                .build();
        entityManager.persist(student2);
        entityManager.flush();

        List<Student> students = studentRepository.findByDepartmentId(department.getId());

        assertNotNull(students);
        assertEquals(2, students.size());
    }

    @Test
    void testExistsByStudentId_True() {
        entityManager.persist(student);
        entityManager.flush();

        boolean exists = studentRepository.existsByStudentId("STU001");

        assertTrue(exists);
    }

    @Test
    void testExistsByStudentId_False() {
        boolean exists = studentRepository.existsByStudentId("STU999");

        assertFalse(exists);
    }

    @Test
    void testDeleteStudent() {
        entityManager.persist(student);
        entityManager.flush();
        Student savedStudent = student;
        Long studentId = savedStudent.getId();

        studentRepository.deleteById(studentId);
        Optional<Student> found = studentRepository.findById(studentId);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        entityManager.persist(student);
        entityManager.flush();

        Student student2 = Student.builder()
                .username("jane.doe")
                .password("password123")
                .email("jane.doe@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .phone("9876543210")
                .studentId("STU002")
                .year(3)
                .address("456 Oak Ave")
                .department(department)
                .role(Role.STUDENT)
                .enabled(true)
                .build();
        entityManager.persist(student2);
        entityManager.flush();

        List<Student> students = studentRepository.findAll();

        assertNotNull(students);
        assertTrue(students.size() >= 2);
    }

    @Test
    void testUpdateStudent() {
        entityManager.persist(student);
        entityManager.flush();
        Student savedStudent = student;

        savedStudent.setEmail("newemail@example.com");
        savedStudent.setPhone("5555555555");
        Student updatedStudent = studentRepository.save(savedStudent);

        assertEquals("newemail@example.com", updatedStudent.getEmail());
        assertEquals("5555555555", updatedStudent.getPhone());
    }
}
