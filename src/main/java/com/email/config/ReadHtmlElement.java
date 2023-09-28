package com.email.config;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReadHtmlElement {
	public static void main(String[] args) {
		String htmlString = "<!DOCTYPE html>\r\n" + "<html lang=\"en\">\r\n" + "\r\n" + "<head>\r\n"
				+ "	<meta charset=\"UTF-8\">\r\n"
				+ "	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
				+ "	<title>Welcome!</title>\r\n" + "	<style>\r\n" + "		body {\r\n"
				+ "			font-family: Arial, sans-serif;\r\n" + "			line-height: 1.6;\r\n" + "		}\r\n"
				+ "\r\n" + "		.container {\r\n" + "			max-width: 600px;\r\n"
				+ "			margin: auto;\r\n" + "			padding: 20px;\r\n" + "		}\r\n" + "\r\n"
				+ "		.header {\r\n" + "			background-color: #007bff;\r\n" + "			color: #fff;\r\n"
				+ "			text-align: center;\r\n" + "			padding: 10px;\r\n" + "		}\r\n" + "\r\n"
				+ "		.content {\r\n" + "			padding: 20px 0;\r\n" + "		}\r\n" + "\r\n"
				+ "		.footer {\r\n" + "			background-color: #f8f9fa;\r\n" + "			text-align: center;\r\n"
				+ "			padding: 10px;\r\n" + "		}\r\n" + "	</style>\r\n" + "</head>\r\n" + "\r\n"
				+ "<body>\r\n" + "	<div class=\"container\">\r\n" + "		<div class=\"header\">\r\n"
				+ "			<h1>Eidiko Systems Integrators</h1>\r\n" + "			<p>Welcome to our feed!</p>\r\n"
				+ "		</div>\r\n" + "		<div class=\"content\">\r\n"
				+ "			<h2>Dear amit.dewangan@eidiko-india.com,</h2>\r\n" + "			<p>\r\n"
				+ "				Thank you for subscribing to our service. We're excited to have you on board!\r\n"
				+ "			</p>\r\n" + "			<p>\r\n"
				+ "				Here's some content that you might find interesting.\r\n" + "			</p>\r\n"
				+ "		</div>\r\n" + "		<div class=\"footer\">\r\n"
				+ "			<p>© 2023 Eidiko Systems Integrators</p>\r\n" + "		</div>\r\n" + "	</div>\r\n"
				+ "</body>\r\n" + "\r\n" + "</html>";

		Document doc = Jsoup.parse(htmlString);

		Elements h2Tags = doc.select("h2");
		String org = null ;
		for (Element h2 : h2Tags) {
			 org = h2.text();
			org=org.substring(5, org.length()-1);
		}
		System.out.println("org : " + org);
	}
}
