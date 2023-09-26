package com.email.service;

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

	public void sendEmail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setCc("amitdewangan199@gmail.com");
			message.setSubject(subject);
			message.setText(text);
			emailSender.send(message);

			// Save response to database
			EmailResponse emailResponse = new EmailResponse();
			emailResponse.setRecipient(to);
		
			emailResponse.setSubject(subject);
			emailResponse.setBody(text);
			emailResponse.setSentSuccessfully(true);
			emailResponseRepository.save(emailResponse);
		} catch (Exception e) {
			e.printStackTrace();

			// Save response to database if sending failed
			EmailResponse emailResponse = new EmailResponse();
			emailResponse.setRecipient(to);
			emailResponse.setSubject(subject);
			emailResponse.setBody(text);
			emailResponse.setSentSuccessfully(false);
			emailResponseRepository.save(emailResponse);
		}
	}
}
