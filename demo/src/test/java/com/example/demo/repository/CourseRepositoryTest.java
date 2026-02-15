package com.example.demo.repository;

import com.example.demo.entity.Course;
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
class CourseRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    private Course course;
    private Dept department;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        // Clear existing data
        courseRepository.deleteAll();
        entityManager.flush();

        department = Dept.builder()
                .name("CS Test Course")
                .description("CS Department")
                .build();
        entityManager.persist(department);
        entityManager.flush();

        teacher = new Teacher();
        teacher.setUsername("jane.smith.test");
        teacher.setPassword("password123");
        teacher.setEmail("jane.smith.test@example.com");
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");
        teacher.setPhone("9876543210");
        teacher.setEmployeeId("EMPT001");
        teacher.setSpecialization("Data Science");
        teacher.setRole(Role.TEACHER);
        teacher.setEnabled(true);
        teacher.setDepartment(department);
        entityManager.persist(teacher);
        entityManager.flush();

        course = new Course();
        course.setName("Data Structures");
        course.setCode("CS101");
        course.setDescription("Introduction to Data Structures");
        course.setCredits(3);
        course.setDepartment(department);
        course.setTeacher(teacher);
    }

    @Test
    void testSaveCourse() {
        Course savedCourse = courseRepository.save(course);

        assertNotNull(savedCourse);
        assertNotNull(savedCourse.getId());
        assertEquals("CS101", savedCourse.getCode());
    }

    @Test
    void testFindById() {
        entityManager.persist(course);
        entityManager.flush();
        Course savedCourse = course;

        Optional<Course> found = courseRepository.findById(savedCourse.getId());

        assertTrue(found.isPresent());
        assertEquals("CS101", found.get().getCode());
    }

    @Test
    void testFindByCode_Found() {
        entityManager.persist(course);
        entityManager.flush();

        Optional<Course> found = courseRepository.findByCode("CS101");

        assertTrue(found.isPresent());
        assertEquals("Data Structures", found.get().getName());
    }

    @Test
    void testFindByCode_NotFound() {
        Optional<Course> found = courseRepository.findByCode("CS999");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByDepartmentId() {
        entityManager.persist(course);
        entityManager.flush();

        Course course2 = new Course();
        course2.setName("Algorithms");
        course2.setCode("CS102");
        course2.setDescription("Introduction to Algorithms");
        course2.setCredits(4);
        course2.setDepartment(department);
        course2.setTeacher(teacher);
        entityManager.persist(course2);
        entityManager.flush();

        List<Course> courses = courseRepository.findByDepartmentId(department.getId());

        assertNotNull(courses);
        assertTrue(courses.size() >= 2);
    }

    @Test
    void testFindByTeacherId() {
        entityManager.persist(course);
        entityManager.flush();

        Course course2 = new Course();
        course2.setName("Algorithms");
        course2.setCode("CS102");
        course2.setDescription("Introduction to Algorithms");
        course2.setCredits(4);
        course2.setDepartment(department);
        course2.setTeacher(teacher);
        entityManager.persist(course2);
        entityManager.flush();

        List<Course> courses = courseRepository.findByTeacherId(teacher.getId());

        assertNotNull(courses);
        assertTrue(courses.size() >= 2);
    }

    @Test
    void testExistsByCode_True() {
        entityManager.persist(course);
        entityManager.flush();

        boolean exists = courseRepository.existsByCode("CS101");

        assertTrue(exists);
    }

    @Test
    void testExistsByCode_False() {
        boolean exists = courseRepository.existsByCode("CS999");

        assertFalse(exists);
    }

    @Test
    void testDeleteCourse() {
        entityManager.persist(course);
        entityManager.flush();
        Course savedCourse = course;
        Long courseId = savedCourse.getId();

        courseRepository.deleteById(courseId);
        Optional<Course> found = courseRepository.findById(courseId);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        entityManager.persist(course);
        entityManager.flush();

        Course course2 = new Course();
        course2.setName("Algorithms");
        course2.setCode("CS102");
        course2.setDescription("Introduction to Algorithms");
        course2.setCredits(4);
        course2.setDepartment(department);
        course2.setTeacher(teacher);
        entityManager.persist(course2);
        entityManager.flush();

        List<Course> courses = courseRepository.findAll();

        assertNotNull(courses);
        assertTrue(courses.size() >= 2);
    }

    @Test
    void testUpdateCourse() {
        entityManager.persist(course);
        entityManager.flush();
        Course savedCourse = course;

        savedCourse.setName("Advanced Data Structures");
        savedCourse.setCredits(4);
        Course updatedCourse = courseRepository.save(savedCourse);

        assertEquals("Advanced Data Structures", updatedCourse.getName());
        assertEquals(4, updatedCourse.getCredits());
    }
}
