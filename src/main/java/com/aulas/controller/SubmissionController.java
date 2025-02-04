// src/main/java/com/aulas/controller/SubmissionController.java

package com.aulas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.aulas.model.dto.SubmissionDTO;
import com.aulas.model.dto.SubmissionDTOWithStudentName;
import com.aulas.model.dto.GradeSubmissionRequest;
import com.aulas.service.SubmissionService;
import com.aulas.service.UserService;
import com.aulas.model.User;


@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    /**
     * Endpoint for students to submit an assignment.
     *
     * @param assignmentId The ID of the assignment.
     * @param file         The uploaded file.
     * @param userDetails  The authenticated user details.
     * @return The created SubmissionDTO.
     */
    @PostMapping("/submit/{assignmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionDTO> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String email) {

        User student = userService.getUserByEmail(email);
        SubmissionDTO submissionDTO = submissionService.submitAssignment(assignmentId, student.getId(), file);
        return ResponseEntity.ok(submissionDTO);
    }

    /**
     * Endpoint for teachers to view all submissions for an assignment.
     *
     * @param assignmentId The ID of the assignment.
     * @return List of SubmissionDTOs.
     */
    @GetMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<SubmissionDTOWithStudentName>> getSubmissionsForAssignment(@PathVariable Long assignmentId) {
        List<SubmissionDTOWithStudentName> submissions = submissionService.getSubmissionsForAssignment(assignmentId);
        return ResponseEntity.ok(submissions);
    }

    /**
     * Endpoint for teachers to grade a submission.
     *
     * @param submissionId The ID of the submission.
     * @param gradeRequest The grade information.
     * @return The updated SubmissionDTO.
     */
    @PutMapping("/grade/{submissionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<SubmissionDTO> gradeSubmission(
            @PathVariable Long submissionId,
            @javax.validation.Valid @RequestBody GradeSubmissionRequest gradeRequest) {
        SubmissionDTO updatedSubmission = submissionService.gradeSubmission(submissionId, gradeRequest.getGrade());
        return ResponseEntity.ok(updatedSubmission);
    }

    /**
     * Endpoint for students to view their own submissions.
     *
     * @param userDetails The authenticated user details.
     * @return List of SubmissionDTOs.
     */
    @GetMapping("/my-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SubmissionDTO>> getMySubmissions(
      @AuthenticationPrincipal String email) {

        User student = userService.getUserByEmail(email);
        List<SubmissionDTO> submissions = submissionService.getSubmissionsByStudent(student.getId());
        return ResponseEntity.ok(submissions);
    }

    /**
     * Endpoint for students to view a specific submission.
     *
     * @param assignmentId The ID of the assignment.
     * @param userDetails  The authenticated user details.
     * @return The SubmissionDTO.
     */
    @GetMapping("/my-submissions/{assignmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionDTO> getMySubmissionForAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal String email) {

        User student = userService.getUserByEmail(email);
        SubmissionDTO submission = submissionService.getSubmissionForAssignmentByStudent(assignmentId, student.getId());
        return ResponseEntity.ok(submission);
    }

}
