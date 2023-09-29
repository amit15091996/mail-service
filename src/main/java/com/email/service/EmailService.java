package com.email.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.email.model.EmailResponse;
import com.email.repo.EmailResponseRepository;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private EmailResponseRepository emailResponseRepository;
// emailservice199@gmail.com

	public void sendEmail(String name, String email) {
		try {

			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo("emailservice199@gmail.com"); // Your email address
			message.setSubject("Form Submission");
			message.setText("Name: " + name + "\nEmail: " + email);

			emailSender.send(message);

			// Save response to database
			EmailResponse emailResponse = new EmailResponse();
			emailResponse.setRecipient(email);
			emailResponse.setSubject("Form Submission");
			emailResponse.setBody("Sent...");
			emailResponse.setSentDate(new Date());
			emailResponse.setSentSuccessfully(true);
			emailResponseRepository.save(emailResponse);
		} catch (Exception e) {
			e.printStackTrace();

			// Save response to database if sending failed
			EmailResponse emailResponse = new EmailResponse();
			emailResponse.setRecipient(email);
			emailResponse.setSubject("Form Submission");
			emailResponse.setBody("Not Sent...");
			emailResponse.setSentDate(new Date());
			emailResponse.setSentSuccessfully(false);
			emailResponseRepository.save(emailResponse);
		}
	}

}
