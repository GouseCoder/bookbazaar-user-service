package com.bookbazaar.hub.userservice.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.bookbazaar.hub.userservice.utils.EmailConstants;


@Service
public class EmailService {
	
	@Autowired
	JavaMailSender javaMailSender;
	
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	final String smtp_username = EmailConstants.SMTP_USERNAME;
	
	public boolean sendOtpMail(String username, String email, String otp, String messageBody) {
		
		boolean mailsent = false;
	
        try {

        	MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setFrom(smtp_username);
			helper.setTo(email);
			
			String subject = "OTP for BookBazaarHub Registeration";
			helper.setSubject(subject);
			helper.setText(messageBody,true);
			javaMailSender.send(message);
			
			mailsent = true;
			
        } catch (MessagingException e) {
        	logger.error("Exception in sending mail", e);
        	return false;
        }
        
        return mailsent;
		
	}
	
	public boolean sendSimpleMail(String userId, String email, String subject, String messageBody) {
		
		boolean mailsent = false;
		
		  try {

	        	MimeMessage message = javaMailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message);
				helper.setFrom(smtp_username);
				helper.setTo(email);
				
				helper.setSubject(subject);
				
				helper.setText(messageBody,true);
				
				javaMailSender.send(message);
				
				logger.info("Email sent to " + userId);
				
				mailsent = true;


	        } catch (MessagingException e) {
	        	
	        	logger.error("Exception in sending mail", e);
	        	
	        	return false;
	        }
		  
		  return mailsent;
		
	}
	
	
	public String bodyForOtpSignupMail(String username, String otp) {
		
	StringBuffer sb = new StringBuffer("Hello " + username + ",");
	
	sb.append("<br></br>");
	sb.append("Your registeration process has been initated and your OTP is ");
	sb.append("<h2><b>");
	sb.append(otp);
	sb.append("</b></h2>");
	sb.append("Put this OTP to complete your registeration process.");
	sb.append("<br></br>");
	sb.append("<br></br>");
	sb.append("<br></br>");
	sb.append("Best,");
	sb.append("<br></br>");
	sb.append("Team BookBazaarHub");
	
	return sb.toString();
	
	}
	
	public String bodyForLoginMail(String userId) {
		
		StringBuffer sb = new StringBuffer("Your ");
		sb.append("registeration process is completed.");
		sb.append("<br></br>");
		sb.append("Your userId is ");
		sb.append("<h2><b>");
		sb.append(userId);
		sb.append("</b></h2>");
		sb.append("Use this userId and the password you have generated during regesteration ");
		sb.append("<br></br>");
		sb.append("process for Login to ShoppingCart Application.");
		sb.append("Best,");
		sb.append("<br></br>");
		sb.append("Team ShoppingCart");
		
		return sb.toString();
		
	}

}
