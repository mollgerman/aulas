
package com.aulas.model.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeSubmissionRequest {
    
    @NotNull(message = "Grade is required")
    @Min(value = 0, message = "Grade must be at least 0")
    @Max(value = 100, message = "Grade cannot exceed 100")
    private Integer grade;
}
