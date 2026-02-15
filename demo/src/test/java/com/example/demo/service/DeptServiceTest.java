package com.example.demo.service;

import com.example.demo.dto.DeptDTO;
import com.example.demo.entity.Dept;
import com.example.demo.repository.DeptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeptServiceTest {

    @Mock
    private DeptRepository deptRepository;

    @InjectMocks
    private DeptService deptService;

    private Dept department;
    private DeptDTO deptDTO;

    @BeforeEach
    void setUp() {
        department = Dept.builder()
                .id(1L)
                .name("Computer Science")
                .description("CS Department")
                .build();

        deptDTO = DeptDTO.builder()
                .id(1L)
                .name("Computer Science")
                .description("CS Department")
                .build();
    }

    @Test
    void testGetAllDepartments() {
        when(deptRepository.findAll()).thenReturn(Arrays.asList(department));

        List<DeptDTO> result = deptService.getAllDepartments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Computer Science", result.get(0).getName());
        verify(deptRepository, times(1)).findAll();
    }

    @Test
    void testGetDepartmentById_Success() {
        when(deptRepository.findById(1L)).thenReturn(Optional.of(department));

        DeptDTO result = deptService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals("Computer Science", result.getName());
        assertEquals("CS Department", result.getDescription());
        verify(deptRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDepartmentById_NotFound() {
        when(deptRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deptService.getDepartmentById(1L));

        assertTrue(exception.getMessage().contains("Department not found"));
        verify(deptRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateDepartment_Success() {
        when(deptRepository.existsByName("Computer Science")).thenReturn(false);
        when(deptRepository.save(any(Dept.class))).thenReturn(department);

        DeptDTO result = deptService.createDepartment(deptDTO);

        assertNotNull(result);
        assertEquals("Computer Science", result.getName());
        verify(deptRepository, times(1)).save(any(Dept.class));
    }

    @Test
    void testCreateDepartment_NameExists() {
        when(deptRepository.existsByName("Computer Science")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deptService.createDepartment(deptDTO));

        assertTrue(exception.getMessage().contains("Department with name"));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(deptRepository, never()).save(any(Dept.class));
    }

    @Test
    void testUpdateDepartment_Success() {
        when(deptRepository.findById(1L)).thenReturn(Optional.of(department));
        when(deptRepository.save(any(Dept.class))).thenReturn(department);

        deptDTO.setDescription("Updated CS Department");

        DeptDTO result = deptService.updateDepartment(1L, deptDTO);

        assertNotNull(result);
        verify(deptRepository, times(1)).save(any(Dept.class));
    }

    @Test
    void testUpdateDepartment_NotFound() {
        when(deptRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deptService.updateDepartment(1L, deptDTO));

        assertTrue(exception.getMessage().contains("Department not found"));
        verify(deptRepository, never()).save(any(Dept.class));
    }

    @Test
    void testDeleteDepartment_Success() {
        when(deptRepository.existsById(1L)).thenReturn(true);
        doNothing().when(deptRepository).deleteById(1L);

        deptService.deleteDepartment(1L);

        verify(deptRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDepartment_NotFound() {
        when(deptRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deptService.deleteDepartment(1L));

        assertTrue(exception.getMessage().contains("Department not found"));
        verify(deptRepository, never()).deleteById(anyLong());
    }
}

