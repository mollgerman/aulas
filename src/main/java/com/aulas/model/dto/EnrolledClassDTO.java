package com.aulas.model.dto;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class EnrolledClassDTO {
  private Long classId;
  private String title;

  public EnrolledClassDTO(Long classId, String title) {
      this.classId = classId;
      this.title = title;
  }
}