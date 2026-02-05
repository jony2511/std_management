package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer credits;
    private Long departmentId;
    private String departmentName;
    private Long teacherId;
    private String teacherName;
}
