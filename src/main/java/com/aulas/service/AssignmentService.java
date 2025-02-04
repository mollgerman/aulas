package com.aulas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aulas.model.Assignment;
import com.aulas.model.ClassEntity;
import com.aulas.model.dto.AssignmentDTO;
import com.aulas.model.dto.AssignmentWithClassDTO;
import com.aulas.repository.AssignmentRepository;
import com.aulas.repository.ClassRepository;
import com.aulas.repository.EnrollmentRepository;
import com.aulas.exception.AccessDeniedException;
import com.aulas.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    /**
     * Creates a new assignment for a given class.
     *
     * @param classId    The ID of the class to assign the assignment to.
     * @param assignment The assignment details.
     * @return The created Assignment entity.
     */
    public Assignment createAssignment(Long classId, Assignment assignment) {
        ClassEntity assignedClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
        assignment.setAssignedClass(assignedClass);
        return assignmentRepository.save(assignment);
    }

    /**
     * Retrieves assignments for a class if the student is enrolled.
     *
     * @param classId    The ID of the class.
     * @param studentId  The ID of the student.
     * @return A list of assignments for the class.
     */
    @Transactional
    public List<AssignmentDTO> getAssignmentsForClass(Long classId, Long studentId) {
        boolean isStudentEnrolled = enrollmentRepository.findByStudentId(studentId).stream()
        .anyMatch(enrollment -> {
            boolean match = enrollment.getClassEntity().getId().equals(classId);
            System.out.println("Checking enrollment: " + enrollment.getClassEntity().getId() + " == " + classId + " -> " + match);
            return match;
        });
        if (!isStudentEnrolled) {
            throw new AccessDeniedException("Access denied: Student not enrolled in this class.");
        }

        List<Assignment> assignments = assignmentRepository.findByAssignedClass_Id(classId);

        // Map Assignment entities to AssignmentDTOs
        return assignments.stream()
                .map(a -> new AssignmentDTO(a.getId(), a.getTitle(), a.getDescription(), a.getAssignedClass().getId()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all assignments for a given class.
     *
     * @param classId The ID of the class.
     * @return A list of assignments for the class.
     */
    public List<Assignment> getAssignmentsForClass(Long classId) {
        return assignmentRepository.findByAssignedClass_Id(classId);
    }

    /**
     * Retrieves assignments for a student across all enrolled classes.
     *
     * @param studentId The ID of the student.
     * @return A list of assignments for the student.
     */
    @Transactional
    public List<Assignment> getAssignmentsForStudent(Long studentId) {
        List<ClassEntity> enrolledClasses = enrollmentRepository.findClassesByStudentId(studentId);
        return enrolledClasses.stream()
                .flatMap(classEntity -> assignmentRepository.findByAssignedClass_Id(classEntity.getId()).stream())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AssignmentWithClassDTO> getAssignmentsWithoutSubmission(Long studentId) {
        return enrollmentRepository.findAssignmentsWithoutSubmissionForStudent(studentId);
    }

}
