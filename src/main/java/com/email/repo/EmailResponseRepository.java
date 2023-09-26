package com.email.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.email.model.EmailResponse;

public interface EmailResponseRepository extends JpaRepository<EmailResponse, Long> {
}