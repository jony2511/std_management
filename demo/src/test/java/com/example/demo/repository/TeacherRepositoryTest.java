package com.example.demo.repository;

import com.example.demo.entity.Dept;
import com.example.demo.entity.Role;
import com.example.demo.entity.Teacher;
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
class TeacherRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher teacher;
    private Dept department;

    @BeforeEach
    void setUp() {
        // Clear existing data
        teacherRepository.deleteAll();
        entityManager.flush();

        department = Dept.builder()
                .name("CS Test Teacher")
                .description("CS Department")
                .build();
        entityManager.persist(department);
        entityManager.flush();

        teacher = new Teacher();
        teacher.setUsername("jane.smith");
        teacher.setPassword("password123");
        teacher.setEmail("jane.smith@example.com");
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");
        teacher.setPhone("9876543210");
        teacher.setEmployeeId("EMP001");
        teacher.setSpecialization("Data Science");
        teacher.setRole(Role.TEACHER);
        teacher.setEnabled(true);
        teacher.setDepartment(department);
    }

    @Test
    void testSaveTeacher() {
        Teacher savedTeacher = teacherRepository.save(teacher);

        assertNotNull(savedTeacher);
        assertNotNull(savedTeacher.getId());
        assertEquals("jane.smith", savedTeacher.getUsername());
        assertEquals("EMP001", savedTeacher.getEmployeeId());
    }

    @Test
    void testFindById() {
        entityManager.persist(teacher);
        entityManager.flush();
        Teacher savedTeacher = teacher;

        Optional<Teacher> found = teacherRepository.findById(savedTeacher.getId());

        assertTrue(found.isPresent());
        assertEquals("jane.smith", found.get().getUsername());
    }

    @Test
    void testFindByUsername_Found() {
        entityManager.persist(teacher);
        entityManager.flush();

        Optional<Teacher> found = teacherRepository.findByUsername("jane.smith");

        assertTrue(found.isPresent());
        assertEquals("jane.smith", found.get().getUsername());
        assertEquals("EMP001", found.get().getEmployeeId());
    }

    @Test
    void testFindByUsername_NotFound() {
        Optional<Teacher> found = teacherRepository.findByUsername("unknown");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmployeeId_Found() {
        entityManager.persist(teacher);
        entityManager.flush();

        Optional<Teacher> found = teacherRepository.findByEmployeeId("EMP001");

        assertTrue(found.isPresent());
        assertEquals("jane.smith", found.get().getUsername());
    }

    @Test
    void testFindByEmployeeId_NotFound() {
        Optional<Teacher> found = teacherRepository.findByEmployeeId("EMP999");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByDepartmentId() {
        entityManager.persist(teacher);
        entityManager.flush();

        Teacher teacher2 = new Teacher();
        teacher2.setUsername("john.doe");
        teacher2.setPassword("password123");
        teacher2.setEmail("john.doe@example.com");
        teacher2.setFirstName("John");
        teacher2.setLastName("Doe");
        teacher2.setPhone("1234567890");
        teacher2.setEmployeeId("EMP002");
        teacher2.setSpecialization("Machine Learning");
        teacher2.setRole(Role.TEACHER);
        teacher2.setEnabled(true);
        teacher2.setDepartment(department);
        entityManager.persist(teacher2);
        entityManager.flush();

        List<Teacher> teachers = teacherRepository.findByDepartmentId(department.getId());

        assertNotNull(teachers);
        assertTrue(teachers.size() >= 2);
    }

    @Test
    void testExistsByEmployeeId_True() {
        entityManager.persist(teacher);
        entityManager.flush();

        boolean exists = teacherRepository.existsByEmployeeId("EMP001");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmployeeId_False() {
        boolean exists = teacherRepository.existsByEmployeeId("EMP999");

        assertFalse(exists);
    }

    @Test
    void testDeleteTeacher() {
        entityManager.persist(teacher);
        entityManager.flush();
        Teacher savedTeacher = teacher;
        Long teacherId = savedTeacher.getId();

        teacherRepository.deleteById(teacherId);
        Optional<Teacher> found = teacherRepository.findById(teacherId);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        entityManager.persist(teacher);
        entityManager.flush();

        Teacher teacher2 = new Teacher();
        teacher2.setUsername("john.doe");
        teacher2.setPassword("password123");
        teacher2.setEmail("john.doe@example.com");
        teacher2.setFirstName("John");
        teacher2.setLastName("Doe");
        teacher2.setPhone("1234567890");
        teacher2.setEmployeeId("EMP002");
        teacher2.setSpecialization("Machine Learning");
        teacher2.setRole(Role.TEACHER);
        teacher2.setEnabled(true);
        teacher2.setDepartment(department);
        entityManager.persist(teacher2);
        entityManager.flush();

        List<Teacher> teachers = teacherRepository.findAll();

        assertNotNull(teachers);
        assertTrue(teachers.size() >= 2);
    }

    @Test
    void testUpdateTeacher() {
        entityManager.persist(teacher);
        entityManager.flush();
        Teacher savedTeacher = teacher;

        savedTeacher.setEmail("newemail@example.com");
        savedTeacher.setSpecialization("Artificial Intelligence");
        Teacher updatedTeacher = teacherRepository.save(savedTeacher);

        assertEquals("newemail@example.com", updatedTeacher.getEmail());
        assertEquals("Artificial Intelligence", updatedTeacher.getSpecialization());
    }
}
