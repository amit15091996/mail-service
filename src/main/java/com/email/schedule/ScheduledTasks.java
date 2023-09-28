package com.email.schedule;

import java.io.IOException;
import java.text.ParseException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.email.service.EmailReaderService;

@Component
public class ScheduledTasks {

	@Autowired
	private EmailReaderService emailReadService;

	public ScheduledTasks(EmailReaderService emailReadService) {
		this.emailReadService = emailReadService;
	}

	@Scheduled(fixedRate = 5000) // 5 sec
	public void readUnreadEmails() throws MessagingException, IOException, ParseException {
		System.out.println("scheduler");
		emailReadService.replyToEmails();
	}
}