package com.bookbazaar.hub.userservice.stores;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bookbazaar.hub.userservice.dto.UserRequestDto;


public class UserRegDetailStore {
	
	private static final Logger logger = LoggerFactory.getLogger(UserRegDetailStore.class);

	private static final int MAX_FAILED_ATTEMPTS = 3;
	
	protected ConcurrentHashMap<String, UserRequestDto> userRegDetailsMap;

	private static UserRegDetailStore instance;

	public UserRegDetailStore() {

		this.userRegDetailsMap = new ConcurrentHashMap<String, UserRequestDto>();
	}

	public static UserRegDetailStore getInstance() {

		if (instance == null) {

			instance = new UserRegDetailStore();
		}
		return instance;
	}

	public void addUserRegDetails(UserRequestDto user, String otp) {
		
//		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//		String requiredDate = df.format(user.getDateOfBirth());
		
		UserRequestDto userdetails = new UserRequestDto();
		userdetails.setFirstName(user.getFirstName());
		userdetails.setLastname(user.getLastname());
		userdetails.setAddress(user.getAddress());
		userdetails.setDateOfBirth(user.getDateOfBirth());
		userdetails.setEmail(user.getEmail());
		userdetails.setOtp(otp);
		userdetails.setPhone(user.getPhone());
		userdetails.setRoles(user.getRoles());
		userdetails.setFirstLogin(true);
		
		userRegDetailsMap.put(user.getEmail(), userdetails);
	}
	
	public UserRequestDto getUserRegDetails(String email) {

		UserRequestDto userDetails = null;

		try {

			if (userRegDetailsMap.containsKey(email)) {

				userDetails = userRegDetailsMap.get(email);
			}

		} catch (Exception e) {

			logger.error("Error in getting userdetails from map", e);
		}
		return userDetails;
	}

	public void updateFailedAttempts(int failAttempts, String email) {

		if (userRegDetailsMap.containsKey(email)) {

			UserRequestDto userdetails = getUserRegDetails(email);
			userdetails.setOtpfailedAttempt(failAttempts);
			userRegDetailsMap.put(email, userdetails);

		}

	}

	public void increaseFailedAttempts(String email) {

		UserRequestDto userdetails = getUserRegDetails(email);

		int newFailAttempts = userdetails.getOtpfailedAttempt() + 1;

		updateFailedAttempts(newFailAttempts, email);

		logger.info("increasing otpfailAttempt of user " + email + "to " + userdetails.getOtpfailedAttempt());

	}

	public void resetFailedAttempts(String email) {
		updateFailedAttempts(0, email);
		logger.info("failattempt of userid " + email + " is reseted");
	}

	public boolean checkForResendOtp(String userId) {

		UserRequestDto userdetails = getUserRegDetails(userId);
		boolean isRegnerateRequired = userdetails.getOtpfailedAttempt() < MAX_FAILED_ATTEMPTS ? false : true;
		return isRegnerateRequired;

	}

	public void removeUserRegDetails(String userId) {

		if (userRegDetailsMap.containsKey(userId)) {

			userRegDetailsMap.remove(userId);

			logger.info("user " + userId + " removed from UserRegDetailStore");
		} else {

			logger.info("removeUserRegDetails : userId " + userId + " not present in map");
		}

	}

	public void updateOTP(String email, String otp) {

		UserRequestDto userDetails = userRegDetailsMap.get(email);

		userDetails.setOtp(otp);

		userRegDetailsMap.put(email, userDetails);

		logger.info("otp " + otp + " updated for user " + email);

	}

}
