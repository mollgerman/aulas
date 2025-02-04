package com.aulas.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "class_students") // same name as before, but now mapped as an entity
@Getter
@Setter
public class Enrollment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "class_id", nullable = false)
  private ClassEntity classEntity;

  // Optional: additional fields, like enrollmentDate, etc.
  private LocalDate enrollmentDate;

  public Enrollment() {
  }

  public Enrollment(User student, ClassEntity classEntity) {
    this.student = student;
    this.classEntity = classEntity;
    this.enrollmentDate = LocalDate.now();
  }
}
