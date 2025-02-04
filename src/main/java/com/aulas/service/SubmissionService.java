package com.aulas.service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aulas.exception.ResourceNotFoundException;
import com.aulas.exception.BadRequestException;
import com.aulas.model.Assignment;
import com.aulas.model.Submission;
import com.aulas.model.User;
import com.aulas.model.dto.SubmissionDTO;
import com.aulas.model.dto.SubmissionDTOWithStudentName;
import com.aulas.repository.AssignmentRepository;
import com.aulas.repository.SubmissionRepository;
import com.aulas.repository.UserRepository;
import com.aulas.config.FileStorageProperties;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SubmissionService {
    
    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final Path fileStorageLocation;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public SubmissionService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            logger.info("File storage directory set to: {}", this.fileStorageLocation.toString());
        } catch (Exception ex) {
            logger.error("Could not create the directory where the uploaded files will be stored.", ex);
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Transactional
    public SubmissionDTO submitAssignment(Long assignmentId, Long studentId, MultipartFile file) {
        logger.debug("Student ID {} is submitting assignment ID {}", studentId, assignmentId);

        // Verify assignment exists
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        // Verify student exists
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Check if student is enrolled in the class
        // boolean isEnrolled = assignment.getAssignedClass().getEnrollments().stream()
        //         .anyMatch(enrollment -> enrollment.getStudent().getId().equals(studentId));
        // if (!isEnrolled) {
        //     throw new BadRequestException("Student is not enrolled in the class for this assignment.");
        // }

        // Check if submission already exists
        if (submissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId).isPresent()) {
            throw new BadRequestException("Submission already exists for this assignment by the student.");
        }

        // Store the file and get the file name/path
        String fileName = storeFile(file);

        // Create and save submission
        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmissionFile(fileName);
        submission.setSubmissionDate(LocalDateTime.now());

        Submission savedSubmission = submissionRepository.save(submission);
        logger.info("Submission saved with ID {}", savedSubmission.getId());

        // Map to DTO
        return mapToDTO(savedSubmission);
    }

    private String storeFile(MultipartFile file) {
        String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();

        // Generate a unique file name to prevent collisions
        String fileExtension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalFileName.substring(i);
        }
        String uniqueFileName = java.util.UUID.randomUUID().toString() + fileExtension;

        try {
            if (originalFileName.contains("..")) {
                throw new BadRequestException("Filename contains invalid path sequence " + originalFileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("File stored at {}", targetLocation.toString());
            return uniqueFileName;
        } catch (IOException ex) {
            logger.error("Could not store file {}. Please try again!", originalFileName, ex);
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public List<SubmissionDTOWithStudentName> getSubmissionsForAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentIdWithStudentName(assignmentId);
    }
    

    public SubmissionDTO getSubmissionById(Long submissionId) {
        logger.debug("Fetching submission by ID {}", submissionId);
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));
        return mapToDTO(submission);
    }

    @Transactional
    public SubmissionDTO gradeSubmission(Long submissionId, Integer grade) {
        logger.debug("Grading submission ID {} with grade {}", submissionId, grade);
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));

        submission.setGrade(grade);
        Submission updatedSubmission = submissionRepository.save(submission);
        logger.info("Submission ID {} graded with {}", submissionId, grade);
        return mapToDTO(updatedSubmission);
    }

    public List<SubmissionDTO> getSubmissionsByStudent(Long studentId) {
        logger.debug("Fetching submissions for student ID {}", studentId);
        List<Submission> submissions = submissionRepository.findAll().stream()
                .filter(submission -> submission.getStudent().getId().equals(studentId))
                .collect(Collectors.toList());
        return submissions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SubmissionDTO getSubmissionForAssignmentByStudent(Long assignmentId, Long studentId) {
        logger.debug("Fetching submission for student ID {} and assignment ID {}", studentId, assignmentId);
        Submission submission = submissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found for student ID " + studentId + " and assignment ID " + assignmentId));
        return mapToDTO(submission);
    }

    private SubmissionDTO mapToDTO(Submission submission) {
        return new SubmissionDTO(
                submission.getId(),
                submission.getSubmissionFile(),
                submission.getGrade(),
                submission.getSubmissionDate(),
                submission.getAssignment().getId(),
                submission.getStudent().getId()                
        );
    }
}
