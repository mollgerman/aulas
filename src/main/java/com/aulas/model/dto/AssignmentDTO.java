package com.aulas.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDTO {
    private Long id;
    private String title;
    private String description;
    private Long classId;
    
}
