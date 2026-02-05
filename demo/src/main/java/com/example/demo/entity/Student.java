package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student extends User {

    private String studentId;

    private Integer year;

    private String address;

    // Many Students belong to One Department (M:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Dept department;

    // Many Students enroll in Many Courses (M:M)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "student_courses", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();

    @Builder
    public Student(Long id, String username, String password, String email,
            String firstName, String lastName, String phone, Role role,
            boolean enabled, String studentId, Integer year, String address,
            Dept department) {
        super(id, username, password, email, firstName, lastName, phone, role, enabled);
        this.studentId = studentId;
        this.year = year;
        this.address = address;
        this.department = department;
    }
}
