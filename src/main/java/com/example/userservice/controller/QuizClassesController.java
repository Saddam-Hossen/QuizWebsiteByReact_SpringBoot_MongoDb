package com.example.userservice.controller;

import com.example.userservice.model.QuizAttendance;
import com.example.userservice.model.QuizClasses;
import com.example.userservice.model.QuizFeedback;
import com.example.userservice.repository.QuizAttendanceRepository;
import com.example.userservice.repository.QuizClassesRepository;
import com.example.userservice.repository.QuizFeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizClasses")
public class QuizClassesController {

    @Autowired
    private QuizClassesRepository quizClassesRepository;
    @Autowired
    private QuizAttendanceRepository quizAttendanceRepository;
    @Autowired
    private QuizFeedbackRepository quizFeedbackRepository;


    @PostMapping("/insert")
    public QuizClasses insertNotice(@RequestBody QuizClasses notice) {
        /*QuizAttendance attendance = new QuizAttendance();
        attendance.setDatetime(notice.getDatetime().toString());
        attendance.setClassName(notice.getClassName());
        attendance.setClassNumber(notice.getClassNumber());
        quizAttendanceRepository.save(attendance);*/

        /*QuizFeedback feedback = new QuizFeedback();
        feedback.setClassName(notice.getClassName());
        feedback.setClassNumber(notice.getClassNumber());
        feedback.setTrainerName(notice.getTrainerName());
        quizFeedbackRepository.save(feedback);*/

        return quizClassesRepository.save(notice);
    }

    @GetMapping("/getAll")
    public List<QuizClasses> getAllNotices() {
        return quizClassesRepository.findAll();
    }

    @PostMapping("/indel")
    public ResponseEntity<String> deleteNotice(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        System.out.println("Deleting class with ID: " + id);

        if (quizClassesRepository.existsById(id)) {
            quizClassesRepository.deleteById(id);
            return ResponseEntity.ok("Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }
    }




    @PostMapping("/status")
    public Optional<QuizClasses> updateStatus(@RequestBody Map<String, String> payload) {
        String id = payload.get("id");
        String status = payload.get("status");
        return  null;
    }
}
