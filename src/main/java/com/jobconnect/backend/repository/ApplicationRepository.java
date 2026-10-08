package com.jobconnect.backend.repository;

import com.jobconnect.backend.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    List<Application> findByApplicantId(Long applicantId);

    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);
}