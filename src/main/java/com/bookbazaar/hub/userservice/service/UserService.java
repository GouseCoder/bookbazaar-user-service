package com.bookbazaar.hub.userservice.service;

import com.bookbazaar.hub.userservice.dto.UserRequestDto;
import com.fasterxml.jackson.databind.JsonNode;

public interface UserService {
	
	JsonNode registerUser(UserRequestDto user);

	boolean validateOtp(String userotp, String email);

	JsonNode handleOtpValidation(String oTP, String emailId);

	JsonNode regenerateOtpandSendMail(String username, String email);

	JsonNode saveUserDetailsInDB(String email, String password);

	JsonNode updatePassword(Long userId, String newPassword);

	JsonNode updateAddress(Long userId, String newAddress);

	JsonNode updateEmail(Long userId, String newEmail);

	String getUserIdFromEmail(String username);

	JsonNode getUserDetails(Long userId);
	 

}
