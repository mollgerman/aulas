package com.aulas.model.dto;



import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssignmentWithClassDTO {
  private Long id;
  private String title;
  private String description;
  private LocalDateTime dueDate;

  private ClassInfoDTO assignedClass; // a nested DTO for minimal class info

  // Getters/setters
}