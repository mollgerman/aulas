package com.aulas.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aulas.model.ClassEntity;
import com.aulas.model.Enrollment;
import com.aulas.model.User;
import com.aulas.model.dto.ClassWithTeacherDTO;
import com.aulas.repository.ClassRepository;
import com.aulas.repository.EnrollmentRepository;
import com.aulas.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public ClassEntity createClass(ClassEntity classEntity) {
        if (classEntity.getTeacher() == null || classEntity.getTeacher().getId() == null) {
            throw new IllegalArgumentException("Teacher ID must be provided");
        }

        // Fetch the teacher from the database
        User teacher = userRepository.findById(classEntity.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Set the teacher in the ClassEntity
        classEntity.setTeacher(teacher);

        // Save the class
        return classRepository.save(classEntity);
    }

    @Transactional
    public String addStudentToClass(Long classId, Long studentId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Check if there's already an enrollment
        List<Enrollment> existing = enrollmentRepository.findByStudentId(studentId);
        boolean alreadyEnrolled = existing.stream()
                .anyMatch(en -> en.getClassEntity().getId().equals(classId));

        if (alreadyEnrolled) {
            return "Student is already enrolled in this class.";
        }

        // Otherwise, create a new Enrollment
        Enrollment enrollment = new Enrollment(student, classEntity);
        enrollmentRepository.save(enrollment);

        return "Student added to class successfully!";
    }

    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }

    // Retrieve classes for a specific student
    @Transactional
    public List<ClassEntity> getClassesForStudent(Long studentId) {
        // Directly use the custom query in the EnrollmentRepository
        return enrollmentRepository.findClassesByStudentId(studentId);
    }

    @Transactional
    public List<ClassWithTeacherDTO> getClassesForStudentWithTeacher(Long studentId) {
        // Directly use the custom query in the EnrollmentRepository
        return enrollmentRepository.findClassesByStudentIdWithTeacher(studentId);
    }

    @Transactional
    public ClassWithTeacherDTO getClassbyId(Long classId) {
        return classRepository.findClassByClassIdWithTeacher(classId);
    }
    // @Transactional
    // public List<ClassWithStudentCountDTO> findAllClassesWithStudentCount() {
    //     List<Object[]> results = classRepository.findAllClassesWithStudentCount();
    //     return results.stream()
    //             .map(result -> {
    //                 ClassEntity classEntity = (ClassEntity) result[0];
    //                 Long studentCount = (Long) result[1];
    //                 return new ClassWithStudentCountDTO(
    //                         classEntity.getId(),
    //                         classEntity.getTitle(),
    //                         classEntity.getDescription(),
    //                         studentCount
    //                 );
    //             })
    //             .collect(Collectors.toList());
    // }

    // Retrieve classes for a specific teacher
    public List<ClassEntity> getClassesForTeacher(Long teacherId) {
        return classRepository.findByTeacherId(teacherId);
    }
}
