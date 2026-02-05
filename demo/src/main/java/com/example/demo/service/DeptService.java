package com.example.demo.service;

import com.example.demo.dto.DeptDTO;
import com.example.demo.entity.Dept;
import com.example.demo.repository.DeptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DeptService {

    private final DeptRepository deptRepository;

    public List<DeptDTO> getAllDepartments() {
        return deptRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DeptDTO getDepartmentById(Long id) {
        Dept dept = deptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return convertToDTO(dept);
    }

    public DeptDTO createDepartment(DeptDTO deptDTO) {
        if (deptRepository.existsByName(deptDTO.getName())) {
            throw new RuntimeException("Department with name " + deptDTO.getName() + " already exists");
        }
        Dept dept = Dept.builder()
                .name(deptDTO.getName())
                .description(deptDTO.getDescription())
                .build();
        dept = deptRepository.save(dept);
        return convertToDTO(dept);
    }

    public DeptDTO updateDepartment(Long id, DeptDTO deptDTO) {
        Dept dept = deptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        dept.setName(deptDTO.getName());
        dept.setDescription(deptDTO.getDescription());
        dept = deptRepository.save(dept);
        return convertToDTO(dept);
    }

    public void deleteDepartment(Long id) {
        if (!deptRepository.existsById(id)) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        deptRepository.deleteById(id);
    }

    private DeptDTO convertToDTO(Dept dept) {
        return DeptDTO.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .build();
    }
}
