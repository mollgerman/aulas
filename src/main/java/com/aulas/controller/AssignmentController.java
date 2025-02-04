package com.aulas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aulas.model.Assignment;
import com.aulas.model.User;
import com.aulas.model.dto.AssignmentDTO;
import com.aulas.model.dto.AssignmentWithClassDTO;
import com.aulas.service.AssignmentService;
import com.aulas.service.UserService;


@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    /**
     * Endpoint to create a new assignment for a class.
     *
     * @param classId    The ID of the class.
     * @param requestBody The assignment details.
     * @return The created Assignment.
     */
    @PostMapping("/create/{classId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Assignment> createAssignment(
            @PathVariable Long classId,
            @RequestBody Assignment assignment) {
        Assignment createdAssignment = assignmentService.createAssignment(classId, assignment);
        return ResponseEntity.ok(createdAssignment);
    }

    /**
     * Endpoint to get assignments for a class as a specific student.
     *
     * @param classId   The ID of the class.
     * @param studentId The ID of the student.
     * @return A list of assignments.
     */
    @GetMapping("/class/{classId}/student/{studentId}")
     public ResponseEntity<List<AssignmentDTO>> getAssignmentsForClassAndStudent(
            @PathVariable Long classId,
            @PathVariable Long studentId) {
        List<AssignmentDTO> assignments = assignmentService.getAssignmentsForClass(classId, studentId);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Endpoint to get all assignments for a class.
     *
     * @param classId The ID of the class.
     * @return A list of assignments.
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Assignment>> getAssignmentsForClass(@PathVariable Long classId) {
        List<Assignment> assignments = assignmentService.getAssignmentsForClass(classId);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Endpoint to get all assignments for a student across all classes.
     *
     * @param studentId The ID of the student.
     * @return A list of assignments.
     */
    @GetMapping("/student-assignments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Assignment>> getAssignmentsForStudent(@AuthenticationPrincipal String email) {
        User user = userService.getUserByEmail(email);
        List<Assignment> assignments = assignmentService.getAssignmentsForStudent(user.getId());
        return ResponseEntity.ok(assignments);
    }


    @GetMapping("/student-pending-assignments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AssignmentWithClassDTO>> getPendingAssignmentsForStudent(@AuthenticationPrincipal String email) {
        User user = userService.getUserByEmail(email);
        List<AssignmentWithClassDTO> assignments = assignmentService.getAssignmentsWithoutSubmission(user.getId());
        return ResponseEntity.ok(assignments);
    }
}