package com.aulas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aulas.model.Submission;
import com.aulas.model.dto.SubmissionDTOWithStudentName;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    // Find all submissions for a specific assignment
    List<Submission> findByAssignmentId(Long assignmentId);

    // Find submissions by student and assignment
    Optional<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

        @Query("SELECT new com.aulas.model.dto.SubmissionDTOWithStudentName(" +
           "s.id, s.submissionFile, s.grade, s.submissionDate, " +
           "s.assignment.id, s.student.id, s.student.name) " +
           "FROM Submission s " +
           "WHERE s.assignment.id = :assignmentId")
    List<SubmissionDTOWithStudentName> findByAssignmentIdWithStudentName(@Param("assignmentId") Long assignmentId);


}
