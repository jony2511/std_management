package com.example.demo.repository;

import com.example.demo.entity.Dept;
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
class DeptRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DeptRepository deptRepository;

    private Dept department;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        deptRepository.deleteAll();
        entityManager.flush();

        department = Dept.builder()
                .name("Computer Science Test")
                .description("CS Department")
                .build();
    }

    @Test
    void testSaveDepartment() {
        Dept savedDept = deptRepository.save(department);

        assertNotNull(savedDept);
        assertNotNull(savedDept.getId());
        assertEquals("Computer Science Test", savedDept.getName());
    }

    @Test
    void testFindById() {
        Dept savedDept = deptRepository.save(department);
        entityManager.flush();

        Optional<Dept> found = deptRepository.findById(savedDept.getId());

        assertTrue(found.isPresent());
        assertEquals("Computer Science Test", found.get().getName());
    }

    @Test
    void testFindByName_Found() {
        deptRepository.save(department);
        entityManager.flush();

        Optional<Dept> found = deptRepository.findByName("Computer Science Test");

        assertTrue(found.isPresent());
        assertEquals("Computer Science Test", found.get().getName());
        assertEquals("CS Department", found.get().getDescription());
    }

    @Test
    void testFindByName_NotFound() {
        Optional<Dept> found = deptRepository.findByName("Mathematics");

        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByName_True() {
        deptRepository.save(department);
        entityManager.flush();

        boolean exists = deptRepository.existsByName("Computer Science Test");

        assertTrue(exists);
    }

    @Test
    void testExistsByName_False() {
        boolean exists = deptRepository.existsByName("Mathematics");

        assertFalse(exists);
    }

    @Test
    void testDeleteDepartment() {
        Dept savedDept = deptRepository.save(department);
        entityManager.flush();
        Long deptId = savedDept.getId();

        deptRepository.deleteById(deptId);
        Optional<Dept> found = deptRepository.findById(deptId);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        deptRepository.save(department);

        Dept department2 = Dept.builder()
                .name("Mathematics Test")
                .description("Math Department")
                .build();
        deptRepository.save(department2);
        entityManager.flush();

        List<Dept> departments = deptRepository.findAll();

        assertNotNull(departments);
        assertTrue(departments.size() >= 2);
    }

    @Test
    void testUpdateDepartment() {
        Dept savedDept = deptRepository.save(department);
        entityManager.flush();

        savedDept.setDescription("Updated CS Department");
        Dept updatedDept = deptRepository.save(savedDept);

        assertEquals("Updated CS Department", updatedDept.getDescription());
    }

    @Test
    void testCascadeDelete() {
        Dept savedDept = deptRepository.save(department);
        entityManager.flush();
        Long deptId = savedDept.getId();

        deptRepository.deleteById(deptId);
        entityManager.flush();

        Optional<Dept> found = deptRepository.findById(deptId);
        assertFalse(found.isPresent());
    }
}
