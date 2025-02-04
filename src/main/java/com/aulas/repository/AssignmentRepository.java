package com.aulas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aulas.model.Assignment;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByAssignedClass_Id(Long classId); // Correct relationship-based query

    @Query("""
    SELECT a
    FROM Assignment a
    JOIN a.assignedClass c
    JOIN c.enrollments e
    LEFT JOIN Submission s
           ON s.assignment = a
           AND s.student.id = :studentId
    WHERE e.student.id = :studentId
      AND s.id IS NULL
""")
List<Assignment> findAssignmentsWithoutSubmissionForStudent(@Param("studentId") Long studentId);
}