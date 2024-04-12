package com.bookbazaar.hub.userservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.TOTP;


@Service
public class OtpService {
	
private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
	
	boolean isOtpValid = false;

	public String generateOtp(String userName, String mail) {
		
		String otpCode = null;
		
		try {
			
			byte[] secret = getSecretKey(userName, mail).getBytes();
			TOTP.Builder builder = new TOTP.Builder(secret);
			builder.withPasswordLength(6).withAlgorithm(HMACAlgorithm.SHA1);
			TOTP totp = builder.build();
			otpCode = totp.now();

			logger.info("totp generated successfully " + otpCode);
			
		} catch (Exception e) {
			logger.error("Exception in generateOtp ", e);
		}
		
		return otpCode;

	}

	public String getSecretKey(String userName, String mail) {
		
		return userName + mail;

	}

}
