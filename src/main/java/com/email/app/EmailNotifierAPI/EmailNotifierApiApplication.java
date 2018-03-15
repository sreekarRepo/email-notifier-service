package com.email.app.EmailNotifierAPI;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class EmailNotifierApiApplication {

	@Value("${email.userName}")
	private String userName;

	@Value("${email.Pswd}")
	private String password;

	public static void main(String[] args) {
		SpringApplication.run(EmailNotifierApiApplication.class, args);
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public String sendEmail(@RequestBody EmailMessage emailMessage) throws AddressException, MessagingException {
		sendmail(emailMessage);
		return "Email sent successfully";
	}

	private void sendmail(EmailMessage emailMessage) throws AddressException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		});

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(userName, false));

		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailMessage.getToAddress()));
		msg.setSubject(emailMessage.getSubject());
		msg.setContent(emailMessage.getBody(), "text/html");
		msg.setSentDate(new Date());

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(emailMessage.getBody(), "text/html");

		/**
		 * this snippet will be used for sending mails with attachments
		 */
		/*
		 * Multipart multipart = new MimeMultipart();
		 * multipart.addBodyPart(messageBodyPart); MimeBodyPart attachPart = new
		 * MimeBodyPart(); attachPart.attachFile(attachmentAbsolutepath);
		 * multipart.addBodyPart(attachPart); msg.setContent(multipart); sends the
		 * e-mail
		 */

		Transport.send(msg);
	}
}
