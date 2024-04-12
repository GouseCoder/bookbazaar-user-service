package com.bookbazaar.hub.userservice.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookbazaar.hub.userservice.dto.UserRequestDto;
import com.bookbazaar.hub.userservice.entity.UserInfo;
import com.bookbazaar.hub.userservice.entity.UserRole;
import com.bookbazaar.hub.userservice.repo.UserRepository;
import com.bookbazaar.hub.userservice.repo.UserRoleRepository;
import com.bookbazaar.hub.userservice.service.EmailService;
import com.bookbazaar.hub.userservice.service.OtpService;
import com.bookbazaar.hub.userservice.service.UserService;
import com.bookbazaar.hub.userservice.stores.UserRegDetailStore;
import com.bookbazaar.hub.userservice.utils.AppConstants;
import com.bookbazaar.hub.userservice.utils.CommonsUtils;
import com.bookbazaar.hub.userservice.utils.ResponseConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	CommonsUtils commonsUtils;
	
	@Autowired
	OtpService otpService;
	
	@Autowired 
	EmailService emailService;
	
	Map<String, UserRequestDto> userRegDetailsMap = new HashMap<>();

	@Override
	public JsonNode registerUser(UserRequestDto user) {
		
		ObjectNode resultNode = commonsUtils.createResultNode();
		ObjectNode dataObject = (ObjectNode) resultNode.get(AppConstants.DATA_OBJECT);
		ObjectNode errorObject = (ObjectNode) resultNode.get(AppConstants.ERROR_OBJECT);
		
		try {
			
			if (userRepository.findByEmail(user.getEmail()).isPresent()) {
				logger.info("Email already registered");
				errorObject.put(AppConstants.ERROR_CODE, ResponseConstants.EMAIL_ALREADY_REGISTRED);
				errorObject.put(AppConstants.ERROR_REASON, "Email Already Registered");
			} else {
				
				String generatedotp = otpService.generateOtp(user.getFirstName(), user.getEmail());
				if(isOtpSentToUser(user.getFirstName(), user.getEmail(), generatedotp)) {
					logger.info("OTP sent to user " + user.getEmail());
					saveUserInfoIntocache(userRegDetailsMap, user, generatedotp);
					
					dataObject.put(AppConstants.ERROR_CODE, ResponseConstants.EMAIL_SENT_WITH_OTP);
					dataObject.put(AppConstants.ERROR_REASON, "Otp sent on email succesfully!!");
					
				}
				else {
					errorObject.put(AppConstants.ERROR_CODE, ResponseConstants.OTP_NOT_SENT);
					errorObject.put(AppConstants.ERROR_REASON, "Unable to sent Otp");
				}
			}
			
		} catch (Exception e) {
			logger.error("Exception in registerUser ", e);
		}
		
		return resultNode;
	}

	private boolean isOtpSentToUser(String username, String email, String generatedotp) {
		
		return emailService.sendOtpMail(username, email, generatedotp, emailService.bodyForOtpSignupMail(username, generatedotp));
		
	}

	private void saveUserInfoIntocache(Map<String, UserRequestDto> userRegDetailsMap2, UserRequestDto user, String otp) {
		
		UserRegDetailStore.getInstance().addUserRegDetails(user, otp);
		
	}
	
	public boolean validateOtp(String otp, String email) { 
		
		String generatedOtp = UserRegDetailStore.getInstance().getUserRegDetails(email).getOtp();
		
		logger.info("generatedOtp from localstore" + generatedOtp);
		
		if(otp.equals(generatedOtp)) {
		
			return true;
		}
		else {
		
			return false;
		}

	}

	@Override
	public JsonNode handleOtpValidation(String OTP, String emailId) {
		
		ObjectNode resultNode = commonsUtils.createResultNode();
		ObjectNode dataObject = (ObjectNode) resultNode.get(AppConstants.DATA_OBJECT);
		
		int errCode = 0;
		
		try {
			
			if (validateOtp(OTP, emailId)) {
				logger.info("OTP is validated");
				errCode = otpRegnerationNeeded(emailId) ? ResponseConstants.REGENERATE_OTP : ResponseConstants.OTP_VALID;
			}
			else {
				errCode = ResponseConstants.OTP_INVALID;
				logger.info("Invalid Otp");
				UserRegDetailStore.getInstance().increaseFailedAttempts(emailId);
				if (otpRegnerationNeeded(emailId)) {
					logger.info("Otp regeneration required");
					errCode = ResponseConstants.REGENERATE_OTP;
				}
			}
			
			dataObject.put(AppConstants.ERROR_CODE, errCode);
			
		} catch (Exception e) {
			logger.error("Exception in handleOtpValidation ", e);
		}
		
		return resultNode;
	}

	private boolean otpRegnerationNeeded(String emailId) {
		return UserRegDetailStore.getInstance().checkForResendOtp(emailId);
	}

	@Override
	public JsonNode regenerateOtpandSendMail(String userName, String email) {
		
		ObjectNode resultNode = commonsUtils.createResultNode();
		ObjectNode dataObject = (ObjectNode) resultNode.get(AppConstants.DATA_OBJECT);
		ObjectNode errorObject = (ObjectNode) resultNode.get(AppConstants.ERROR_OBJECT);
		
		try {
			
			String generatedotp = otpService.generateOtp(userName, email);
			if(isOtpSentToUser(userName, email, generatedotp)) {
				UserRegDetailStore.getInstance().updateOTP(generatedotp, generatedotp);
				UserRegDetailStore.getInstance().resetFailedAttempts(email);
				dataObject.put(AppConstants.ERROR_CODE, ResponseConstants.EMAIL_SENT_WITH_OTP);
				dataObject.put(AppConstants.ERROR_REASON, "Otp sent on email ");
			}
			else {
				errorObject.put(AppConstants.ERROR_CODE, ResponseConstants.OTP_NOT_SENT);
				errorObject.put(AppConstants.ERROR_REASON, "Unable to send OTP ");
			}
			
		} catch (Exception e) {
			logger.error("Exception in regenerateOtpandSendMail ", e);
		}
		
		return resultNode;
		
	}

	@Override
	public JsonNode saveUserDetailsInDB(String email, String password) {
		
		ObjectNode resultNode = commonsUtils.createResultNode();
		ObjectNode dataObject = (ObjectNode) resultNode.get(AppConstants.DATA_OBJECT);
		ObjectNode errorObject = (ObjectNode) resultNode.get(AppConstants.ERROR_OBJECT);
		
		try {
		
			String encryptedPassword = passwordEncoder.encode(password);
			UserRequestDto userDto = UserRegDetailStore.getInstance().getUserRegDetails(email);

			for (UserRole role : userDto.getRoles()) {
				UserRole existingRole = userRoleRepository.findByName(role.getName());
				if (existingRole == null) {
					// Role doesn't exist, so save it
					userRoleRepository.save(role);
				} else {
					// Role already exists, set the existing role to avoid transient instance issue
					role.setId(existingRole.getId());
				}

			}
			
			UserInfo userInfo = new UserInfo();
			userInfo.setAddress(userDto.getAddress());
			userInfo.setDateOfBirth(userDto.getDateOfBirth());
			userInfo.setEmail(email);
			userInfo.setFirstName(userDto.getFirstName());
			userInfo.setLastname(userDto.getLastname());
			userInfo.setPassword(encryptedPassword);
			userInfo.setPhone(userDto.getPhone());
			userInfo.setRoles(userDto.getRoles());
			userInfo.setFirstLogin(userDto.isFirstLogin());
			
			userRepository.save(userInfo);
						
			UserRegDetailStore.getInstance().removeUserRegDetails(email);
			
			dataObject.put(AppConstants.ERROR_CODE, ResponseConstants.USER_ADDED_IN_DB);
			
		} catch (Exception e) {
			errorObject.put(AppConstants.ERROR_CODE, ResponseConstants.USER_NOT_ADDED_IN_DB);
			logger.error("Exception in saveUserDetailsInDB ", e);
		}
		
		return resultNode;
	}

	@Override
	public JsonNode updatePassword(Long userId, String newPassword) {
		
		ObjectNode resultNode = commonsUtils.createResultNode();
		ObjectNode dataObject = (ObjectNode) resultNode.get(AppConstants.DATA_OBJECT);
		ObjectNode errorObject = (ObjectNode) resultNode.get(AppConstants.ERROR_OBJECT);
		
		try {
			Optional<UserInfo> optionalUser = userRepository.findById(userId);
			if(optionalUser.isPresent()) {
				UserInfo user = optionalUser.get();
				user.setPassword(passwordEncoder.encode(newPassword));
				userRepository.save(user);
				dataObject.put(AppConstants.STATUS_CODE, ResponseConstants.PASSWORD_CHANGED);
			}
			else {
				errorObject.put(AppConstants.ERROR_CODE, ResponseConstants.INVALID_USER);
				errorObject.put(AppConstants.ERROR_REASON, "User Not Found");
			}
			
		} catch (Exception e) {
			logger.error("Exception in updatePassword ", e);
		}
		
		return resultNode;
	}

	@Override
	public JsonNode updateAddress(Long userId, String newAddress) {
		
		ObjectNode resultNode = commonsUtils.createResultNode();
		ObjectNode dataObject = (ObjectNode) resultNode.get(AppConstants.DATA_OBJECT);
		ObjectNode errorObject = (ObjectNode) resultNode.get(AppConstants.ERROR_OBJECT);
		
		try {
			Optional<UserInfo> optionalUser = userRepository.findById(userId);
			if(optionalUser.isPresent()) {
				UserInfo user = optionalUser.get();
				user.setAddress(newAddress);
				userRepository.save(user);
				dataObject.put(AppConstants.STATUS_CODE, ResponseConstants.ADDRESS_CHANGED);
			}
			else {
				errorObject.put(AppConstants.ERROR_CODE, ResponseConstants.INVALID_USER);
				errorObject.put(AppConstants.ERROR_REASON, "User Not Found");
			}
			
		} catch (Exception e) {
			logger.error("Exception in updateAddress ", e);
		}
		
		return resultNode;
	}

	@Override
	public JsonNode updateEmail(Long userId, String newEmail) {
		
		ObjectNode resultNode = commonsUtils.createResultNode();
		ObjectNode dataObject = (ObjectNode) resultNode.get(AppConstants.DATA_OBJECT);
		ObjectNode errorObject = (ObjectNode) resultNode.get(AppConstants.ERROR_OBJECT);
		
		try {
			Optional<UserInfo> optionalUser = userRepository.findById(userId);
			if(optionalUser.isPresent()) {
				UserInfo user = optionalUser.get();
				user.setEmail(newEmail);
				userRepository.save(user);
				dataObject.put(AppConstants.STATUS_CODE, ResponseConstants.EMAIL_CHANGED);
			}
			else {
				errorObject.put(AppConstants.ERROR_CODE, ResponseConstants.INVALID_USER);
				errorObject.put(AppConstants.ERROR_REASON, "User Not Found");
			}
			
		} catch (Exception e) {
			logger.error("Exception in updatePhone ", e);
		}
		
		return resultNode;
	}

	@Override
	public String getUserIdFromEmail(String emailId) {
		
		Optional<UserInfo> userInfo = userRepository.findByEmail(emailId);
		return userInfo.get().getUserId().toString();
	}
	
	@Override
	public JsonNode getUserDetails(Long userId) {
		
		ObjectNode resultNode = commonsUtils.createResultNode();
		ObjectNode dataObject = (ObjectNode) resultNode.get(AppConstants.DATA_OBJECT);
		
		try {
			
			Optional<UserInfo> optionalUser = userRepository.findById(userId);
			if(optionalUser.isPresent()) {
				UserInfo user = optionalUser.get();
				dataObject.put("userId", user.getUserId());
				dataObject.put("firstname", user.getFirstName());
				dataObject.put("lastname", user.getLastname());
				dataObject.put("dateOfBirth", user.getDateOfBirth());
				dataObject.put("address", user.getAddress());
				dataObject.put("email", user.getEmail());
				dataObject.put("phone", user.getPhone());
			}
			
		} catch (Exception e) {
			logger.error("Exception in getUserDetails " + e);
		}
		
		return resultNode;
	}
	
}
