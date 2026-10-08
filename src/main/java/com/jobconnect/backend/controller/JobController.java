package com.jobconnect.backend.controller;

import com.jobconnect.backend.model.Job;
import com.jobconnect.backend.model.User;
import com.jobconnect.backend.repository.JobRepository;
import com.jobconnect.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:5173")
public class JobController {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobController(
            JobRepository jobRepository,
            UserRepository userRepository) {

        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // GET ALL JOBS
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobRepository.findAll());
    }

    // GET JOB BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {

        return jobRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .build()
                );
    }

    // CREATE JOB
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job) {

        if (job.getRecruiter() == null ||
                job.getRecruiter().getId() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Recruiter ID is required");
        }

        User recruiter = userRepository
                .findById(job.getRecruiter().getId())
                .orElse(null);

        if (recruiter == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Recruiter not found");
        }

        job.setRecruiter(recruiter);

        Job savedJob = jobRepository.save(job);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedJob);
    }

    // DELETE JOB
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {

        if (!jobRepository.existsById(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Job not found");
        }

        jobRepository.deleteById(id);

        return ResponseEntity.ok("Job deleted successfully");
    }
}