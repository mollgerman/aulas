package com.aulas.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionDTOWithStudentName {
    private Long id;
    private String submissionFile; // This can be a URL or path to the file
    private Integer grade;
    private LocalDateTime submissionDate;
    private Long assignmentId;
    private Long studentId;
    private String studentName;
}
