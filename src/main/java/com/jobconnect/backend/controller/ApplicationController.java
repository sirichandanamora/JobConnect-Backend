package com.jobconnect.backend.controller;

import com.jobconnect.backend.model.Application;
import com.jobconnect.backend.model.Job;
import com.jobconnect.backend.model.User;
import com.jobconnect.backend.repository.ApplicationRepository;
import com.jobconnect.backend.repository.JobRepository;
import com.jobconnect.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicationController(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository) {

        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> applyForJob(
            @RequestParam Long jobId,
            @RequestParam Long applicantId) {

        Job job = jobRepository.findById(jobId)
                .orElse(null);

        if (job == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Job not found");
        }

        User applicant = userRepository.findById(applicantId)
                .orElse(null);

        if (applicant == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Applicant not found");
        }

        if (applicationRepository
                .existsByJobIdAndApplicantId(jobId, applicantId)) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("You have already applied for this job");
        }

        Application application = new Application();

        application.setJob(job);
        application.setApplicant(applicant);
        application.setStatus("APPLIED");

        Application savedApplication =
                applicationRepository.save(application);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedApplication);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Application>> getUserApplications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                applicationRepository.findByApplicantId(userId)
        );
    }

    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications() {

        return ResponseEntity.ok(
                applicationRepository.findAll()
        );
    }
}