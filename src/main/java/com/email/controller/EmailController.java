package com.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.email.model.EmailRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class EmailController {

	@Autowired
	private com.email.service.EmailService emailService;

	@Autowired
	private com.email.service.EmailReaderService emailReaderService;

	@PostMapping("/send-email")
	public void sendEmail(@RequestBody EmailRequest emailRequest) {

		String email = emailRequest.getEmail();
		String name = emailRequest.getName();
		emailService.sendEmail(name, email);
	}

	@GetMapping("/read-emails")
	public String readEmails() {
		try {
			emailReaderService.readEmails();
			return "Emails processed successfully.";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error processing emails: " + e.getMessage();
		}
	}
}
