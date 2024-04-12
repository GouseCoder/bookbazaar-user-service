package com.bookbazaar.hub.userservice.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
	
	 @Bean
	    public JavaMailSender javaMailSender() {
	        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	        mailSender.setHost("smtp.gmail.com"); // Set your SMTP host
	        mailSender.setPort(587); // Set your SMTP port
	        mailSender.setUsername("bookbazaarhub189@gmail.com"); // Set your SMTP username
	        mailSender.setPassword("kdcilmlkdmrixfjp"); // Set your SMTP password
	        
	        Properties props = mailSender.getJavaMailProperties();
	        props.put("mail.transport.protocol", "smtp");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.debug", "true");  // Set this to true for debugging purposes

	        return mailSender;
	    }

}
