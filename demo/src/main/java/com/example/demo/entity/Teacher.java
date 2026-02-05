package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends User {

    private String employeeId;

    private String specialization;

    // Many Teachers belong to One Department (M:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Dept department;

    // One Teacher teaches Many Courses (1:M)
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();

    @Builder
    public Teacher(Long id, String username, String password, String email,
            String firstName, String lastName, String phone, Role role,
            boolean enabled, String employeeId, String specialization,
            Dept department) {
        super(id, username, password, email, firstName, lastName, phone, role, enabled);
        this.employeeId = employeeId;
        this.specialization = specialization;
        this.department = department;
    }
}
