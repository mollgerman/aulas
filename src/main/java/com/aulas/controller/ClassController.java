package com.aulas.controller;

import java.sql.Date;
import java.util.List;
import java.util.Map;

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

import com.aulas.model.ClassEntity;
import com.aulas.model.User;
import com.aulas.model.dto.ClassWithTeacherDTO;
import com.aulas.service.ClassService;
import com.aulas.service.UserService;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassService classService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ClassEntity> createClass(@RequestBody Map<String, Object> requestBody) {
        String title = (String) requestBody.get("title");
        String description = (String) requestBody.get("description");
        Long teacherId = Long.valueOf(requestBody.get("teacher_id").toString());
        String place = (String) requestBody.get("place");
        Date startDate = Date.valueOf(requestBody.get("startDate").toString());
        Date endDate = Date.valueOf(requestBody.get("endDate").toString());

        // Create a ClassEntity and set the title and description
        ClassEntity classEntity = new ClassEntity();
        classEntity.setTitle(title);
        classEntity.setDescription(description);
        classEntity.setPlace(place);
        classEntity.setStartDate(startDate);
        classEntity.setEndDate(endDate);
        // Set the teacher's ID in the ClassEntity
        User teacher = new User();
        teacher.setId(teacherId);
        classEntity.setTeacher(teacher);

        // Call the service to create the class
        ClassEntity createdClass = classService.createClass(classEntity);

        return ResponseEntity.ok(createdClass);
    }


    @GetMapping
    public ResponseEntity<List<ClassEntity>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }


    @PostMapping("/add-student")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> addStudentToClass(@RequestBody Map<String, Long> requestBody) {
        Long classId = requestBody.get("classId");
        Long studentId = requestBody.get("studentId");
        return ResponseEntity.ok(classService.addStudentToClass(classId, studentId));
    }

    @GetMapping("/classId/{classId}")
    public ResponseEntity<ClassWithTeacherDTO> getClassById(@PathVariable Long classId) {
        return ResponseEntity.ok(classService.getClassbyId(classId));
    }

    @GetMapping("/student-classes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ClassEntity>> getClassesForStudent(@AuthenticationPrincipal String email) {
        User user = userService.getUserByEmail(email);
        List<ClassEntity> assignedClasses = classService.getClassesForStudent(user.getId());
        return ResponseEntity.ok(assignedClasses);
    }

    @GetMapping("/student-classes/with-teacher")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ClassWithTeacherDTO>> getClassesForStudentWithTeacher(@AuthenticationPrincipal String email) {
        User user = userService.getUserByEmail(email);
        List<ClassWithTeacherDTO> assignedClasses = classService.getClassesForStudentWithTeacher(user.getId());
        return ResponseEntity.ok(assignedClasses);
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<ClassEntity>> getClassesForTeacher(@AuthenticationPrincipal String email) {
        User user = userService.getUserByEmail(email);
        List<ClassEntity> createdClasses = classService.getClassesForTeacher(user.getId());
        return ResponseEntity.ok(createdClasses);
    }


}
