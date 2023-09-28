package com.email.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.email.model.EmailResponse;
import com.email.repo.EmailResponseRepository;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private EmailResponseRepository emailResponseRepository;

	public String loadHtmlTemplate(String templateName) throws IOException {
		ClassPathResource resource = new ClassPathResource("templates/" + templateName);
		return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
	}

	public void sendEmail(String to, String subject, String text) {
		try {
			String replyContent = loadHtmlTemplate("email-tem.html");
			replyContent = replyContent.replace("{{name}}", to);

			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(to);
			helper.setCc("amitdewangan199@gmail.com");
			helper.setSubject(subject);
			helper.setText(text);
//			message.setContent(replyContent,"text/html;charset=utf-8");
			System.out.println("Email : " + to);
			System.out.println("Subject : " + subject);
			System.out.println("Name : " + text);

			emailSender.send(message);

			// Save response to database
			EmailResponse emailResponse = new EmailResponse();
			emailResponse.setRecipient(to);
			emailResponse.setSubject("Check Mail");
			emailResponse.setBody("Sent...");
			emailResponse.setSentDate(new Date());
			emailResponse.setSentSuccessfully(true);
			emailResponseRepository.save(emailResponse);
		} catch (Exception e) {
			e.printStackTrace();

			// Save response to database if sending failed
			EmailResponse emailResponse = new EmailResponse();
			emailResponse.setRecipient(to);
			emailResponse.setSubject(subject);
			emailResponse.setBody("Not Sent...");
			emailResponse.setSentDate(new Date());
			emailResponse.setSentSuccessfully(false);
			emailResponseRepository.save(emailResponse);
		}
	}

}
