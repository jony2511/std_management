package com.example.demo.config;

import com.example.demo.entity.Dept;
import com.example.demo.entity.Role;
import com.example.demo.entity.Teacher;
import com.example.demo.repository.DeptRepository;
import com.example.demo.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TeacherRepository teacherRepository;
    private final DeptRepository deptRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default department if not exists
        if (deptRepository.count() == 0) {
            Dept csDept = Dept.builder()
                    .name("Computer Science")
                    .description("Department of Computer Science and Engineering")
                    .build();
            deptRepository.save(csDept);

            Dept eeDept = Dept.builder()
                    .name("Electrical Engineering")
                    .description("Department of Electrical Engineering")
                    .build();
            deptRepository.save(eeDept);

            log.info("Default departments created");
        }

        // Create default teacher/admin if not exists
        if (teacherRepository.count() == 0) {
            Dept dept = deptRepository.findByName("Computer Science").orElse(null);

            Teacher admin = new Teacher();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@school.com");
            admin.setFirstName("Admin");
            admin.setLastName("Teacher");
            admin.setPhone("1234567890");
            admin.setEmployeeId("EMP001");
            admin.setSpecialization("System Administration");
            admin.setRole(Role.TEACHER);
            admin.setEnabled(true);
            admin.setDepartment(dept);
            teacherRepository.save(admin);

            log.info("Default admin teacher created - Username: admin, Password: admin123");
        }
    }
}
