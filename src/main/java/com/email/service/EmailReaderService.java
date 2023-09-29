package com.email.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Service
public class EmailReaderService {

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private ApplicationContext applicationContext;

	private static final String username = "emailservice199@gmail.com";

	private static final String password = "bzmn jccb gvly ifpi";

	private SearchTerm searchTerm = new SubjectTerm("Form Submission"); // Set default SearchTerm

	public String loadHtmlTemplate(String filePath) throws IOException {
		Resource resource = applicationContext.getResource("classpath:" + filePath);

		if (resource.exists()) {
			try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
				return FileCopyUtils.copyToString(reader);
			}
		} else {
			throw new IOException("HTML file not found at: " + filePath);
		}
	}

	public String readEmails() throws MessagingException, IOException {
		Properties props = new Properties();
		props.put("mail.store.protocol", "imaps");
		props.put("mail.imaps.ssl.trust", "*");

		Session emailSession = Session.getInstance(props);

		Store store = emailSession.getStore("imaps");
		store.connect("smtp.gmail.com", username, password); // Use app password here

		System.out.println("Connected with Store ...");

		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_WRITE);

		Message[] messages = inbox.search(searchTerm);
		String emailContents = "";
		for (Message message : messages) {
			String content = extractContent(message);
			emailContents += content; // Concatenate using '+='
			System.out.println("emailContents : " + emailContents);
			message.setFlag(Flags.Flag.SEEN, true);

			// If you want to extract emails, you can do it here.
			String extractedEmail = extractEmail(emailContents.toString());
			System.out.println("---mail--- " + extractedEmail);

//			Document doc = Jsoup.parse(extractedEmail);
//
//			Elements h2Tags = doc.select("h2");
//			String org = null;
//			for (Element h2 : h2Tags) {
//				org = h2.text();
//				System.out.println("org : " + org);
//				org = org.substring(5, org.length() - 1);
//			}
//			System.out.println("org : " + org);

			String replyContent = loadHtmlTemplate("email-tem.html");
			replyContent = replyContent.replace("{{name}}", extractedEmail);
			System.out.println("ReplyContent : " + replyContent);

			String replySubject = "RE: " + message.getSubject();
			sendResponse(extractedEmail, replySubject, replyContent);
		}

		inbox.close(false);
		store.close();

		return emailContents.toString();
	}

	public String extractContent(Message message) throws MessagingException, IOException {
		Object content = message.getContent();

		if (content instanceof String) {
			return (String) content;
		} else if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			StringBuilder textContent = new StringBuilder();

			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);

				if (bodyPart.getContentType().startsWith("text/plain")) {
					textContent.append(bodyPart.getContent().toString());
				}
			}

			return textContent.toString();
		} else {
			return ""; // If content type is not supported or not recognized
		}
	}

	public String extractEmail(String content) {
		String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,7}\\b";
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			return matcher.group();
		} else {
			return matcher.toString();
		}
	}

	public void sendResponse(String to, String subject, String body) {
		try {
			MimeMessage mimeMessage = emailSender.createMimeMessage();

			mimeMessage.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
			mimeMessage.setSubject(subject);
			mimeMessage.setText(body, "UTF-8", "text/html");

			emailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}