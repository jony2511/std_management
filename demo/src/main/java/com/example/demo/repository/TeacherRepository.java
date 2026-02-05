package com.example.demo.repository;

import com.example.demo.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUsername(String username);

    Optional<Teacher> findByEmployeeId(String employeeId);

    List<Teacher> findByDepartmentId(Long departmentId);

    boolean existsByEmployeeId(String employeeId);
}
