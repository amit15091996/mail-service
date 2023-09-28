package com.email.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class EmailReaderService {
	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	EmailService emailService;

	public String loadHtmlTemplate(String templateName) throws IOException {
		ClassPathResource resource = new ClassPathResource("templates/" + templateName);
		return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
	}

	public void replyToEmails() throws MessagingException, IOException, ParseException {
		Properties properties = new Properties();
		properties.put("mail.store.protocol", "imaps");

		Session session = Session.getInstance(properties);

		Store store = session.getStore();
		store.connect("imap.gmail.com", "amitdewangan199@gmail.com", "hbxf bjfa zbyy izfa");

		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_WRITE);

		Message[] messages = inbox.search(new SubjectTerm("Check Mail"));
		for (Message message : messages) {
			Address[] fromAddresses = message.getFrom();
			for (Address fromAddress : fromAddresses) {
				String sender = fromAddress.toString();
				String subject = message.getSubject();
				System.out.println("subject :" + subject);
				String name = message.getContentType();
				
//				System.out.println("name : " + name);

				String mailBody = "";
				Object content = message.getContent();

				 if (content instanceof Multipart) {

	                    // If the message has multiple parts (e.g., text and attachments)

	                    Multipart multipart = (Multipart) content;

	                    for (int i = 0; i < multipart.getCount(); i++) {

	                        BodyPart bodyPart = multipart.getBodyPart(i);

	                        if (bodyPart.getContentType().startsWith("text/plain")) {

	                            // This is a plain text part

	                            String messageBody = bodyPart.getContent().toString();
//	                            System.out.println("Mail Body :"+messageBody);

	                            mailBody=messageBody;

	                        }

	                    }

	                } else if (content instanceof String) {

	                    // If the message is plain text

	                    String messageBody = content.toString();
	                    System.out.println("Mail Body 2 :"+messageBody);
	                    mailBody=messageBody;

	                }
//				mailBody="amit.dewangan@eidiko-india.com";
//				System.out.println("body" + mailBody);
				Document doc = Jsoup.parse(mailBody);

				Elements h2Tags = doc.select("h2");
				String org = null ;
				for (Element h2 : h2Tags) {
					 org = h2.text();
					 System.out.println("org : " + org);
					org=org.substring(5, org.length()-1);
				}
				System.out.println("org : " + org);
				
				String replyContent = loadHtmlTemplate("email-tem.html");
		        replyContent = replyContent.replace("{{name}}", "amit.dewangan@eidiko-india.com");
		        
				this.emailService.sendEmail(org, subject, replyContent);
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