package com.aulas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aulas.model.ClassEntity;
import com.aulas.model.Enrollment;
import com.aulas.model.User;
import com.aulas.model.dto.AssignmentWithClassDTO;
import com.aulas.model.dto.ClassWithTeacherDTO;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Find all enrollments for a given student
    List<Enrollment> findByStudentId(Long studentId);

    // Find all enrollments for a given class
    List<Enrollment> findByClassEntityId(Long classId);

    // Or a custom query to directly get ClassEntity objects
    @Query("SELECT e.classEntity FROM Enrollment e WHERE e.student.id = :studentId")
    List<ClassEntity> findClassesByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT e.student FROM Enrollment e WHERE e.classEntity.id = :classId")
    List<User> findStudentsByClassId(@Param("classId") Long classId);

    @Query("SELECT new com.aulas.model.dto.ClassWithTeacherDTO(" +
    "c.id, c.title, c.description, c.place, c.startDate, c.endDate, t.name) " +
    "FROM Enrollment e " +
    "JOIN e.classEntity c " +
    "JOIN c.teacher t " +
    "WHERE e.student.id = :studentId")
List<ClassWithTeacherDTO> findClassesByStudentIdWithTeacher(@Param("studentId") Long studentId);

@Query("""
    SELECT new com.aulas.model.dto.AssignmentWithClassDTO(
        a.id,
        a.title,
        a.description,
        a.dueDate,
        new com.aulas.model.dto.ClassInfoDTO(
            c.id,
            c.title,
            c.place
        )
    )
    FROM Enrollment e
         JOIN e.classEntity c
         JOIN Assignment a ON a.assignedClass = c
         LEFT JOIN Submission s
                ON s.assignment = a
                AND s.student.id = :studentId
    WHERE e.student.id = :studentId
      AND s.id IS NULL
""")
List<AssignmentWithClassDTO> findAssignmentsWithoutSubmissionForStudent(@Param("studentId") Long studentId);

}
