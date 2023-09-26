package com.email.service;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SubjectTerm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.email.config.Constant;

@Service
public class EmailReaderService {
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	EmailService emailService;

	public void replyToEmails() throws MessagingException, IOException {
		Properties properties = new Properties();
		properties.put("mail.store.protocol", "imaps");

		Session session = Session.getInstance(properties);

		Store store = session.getStore();
		store.connect("imap.gmail.com", "amitdewangan199@gmail.com", "hbxf bjfa zbyy izfa");

		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_WRITE);

		Message[] messages = inbox.search(new SubjectTerm("Salary Credited"));
		for (Message message : messages) {
			Address[] fromAddresses = message.getFrom();
			for (Address fromAddress : fromAddresses) {
				String sender = fromAddress.toString();
				String subject = message.getSubject();
				System.out.println("subject :"+subject);
				String name = message.getDescription();
				
				
				String mailBody="";
				Object content = message.getContent();

                if (content instanceof Multipart) {

                    // If the message has multiple parts (e.g., text and attachments)

                    Multipart multipart = (Multipart) content;

                    for (int i = 0; i < multipart.getCount(); i++) {

                        BodyPart bodyPart = multipart.getBodyPart(i);

                        if (bodyPart.getContentType().startsWith("text/plain")) {

                            // This is a plain text part

                            String messageBody = bodyPart.getContent().toString();

                            mailBody=messageBody;

                        }

                    }

                } else if (content instanceof String) {

                    // If the message is plain text

                    String messageBody = content.toString();

                    mailBody=messageBody;

                }
				
				System.out.println("body"+mailBody);
				String toEmail=mailBody.substring(3,mailBody.length());
				System.out.println("email"+toEmail);
				
				String replyContent = Constant.getLine1() + toEmail + "," + "\n" + Constant.getLine2() + "\n"
						+ Constant.getLine3() + "\n" + Constant.getLine4() + "\n" + Constant.getLine5();

				// Reply to the original email
				//replyToEmail(message, replyContent, name);
				this.emailService.sendEmail(toEmail, subject, replyContent);
			}

			// Mark the message as read (optional)
			message.setFlag(Flags.Flag.SEEN, true);
		}

		inbox.close(true);
		store.close();
	}

	public void replyToEmail(Message originalMessage, String replyContent, String name) throws MessagingException {
		MimeMessage replyMessage = (MimeMessage) originalMessage.reply(false);

		// Set the reply content
		String response = replyContent + " " + name;
		replyMessage.setText(response);

		// Send the reply
		javaMailSender.send(replyMessage);
	}
}