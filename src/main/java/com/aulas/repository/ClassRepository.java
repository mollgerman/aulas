package com.aulas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aulas.model.ClassEntity;
import com.aulas.model.dto.ClassWithTeacherDTO;
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
  
  Optional<ClassEntity> findById(Long id); 

  List<ClassEntity> findByTeacherId(Long teacherId);

  @Query("SELECT new com.aulas.model.dto.ClassWithTeacherDTO(" +
  "c.id, c.title, c.description, c.place, c.startDate, c.endDate, t.name) " +
  "FROM ClassEntity c " +
  "JOIN c.teacher t " +
  "WHERE c.id = :classId")
ClassWithTeacherDTO findClassByClassIdWithTeacher(@Param("classId") Long classId);
}