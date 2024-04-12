package com.bookbazaar.hub.userservice.dto;

import java.util.HashSet;
import java.util.Set;

import com.bookbazaar.hub.userservice.entity.UserRole;

public class UserRequestDto {
	
	private Long userId;
    private String firstName;
    private String lastname;
    private String dateOfBirth;
    private long phone;
    private String email;
    private String address;
    private String otp;
    private int otpfailedAttempt;
    private boolean firstLogin;
    private Set<UserRole> roles = new HashSet<>();
    
	public UserRequestDto() {}
	
	public void setPhone(long phone) {
		this.phone = phone;
	}

	public UserRequestDto(String firstName, String lastname, String dateOfBirth, long phone,
			String email, String address, Set<UserRole> roles) {
		this.firstName = firstName;
		this.lastname = lastname;
		this.dateOfBirth = dateOfBirth;
		this.phone = phone;
		this.email = email;
		this.address = address;
		this.roles = roles;
	}



	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastname() {
		return lastname;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public long getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Set<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
	}

	public int getOtpfailedAttempt() {
		return otpfailedAttempt;
	}

	public void setOtpfailedAttempt(int otpfailedAttempt) {
		this.otpfailedAttempt = otpfailedAttempt;
	}

	public boolean isFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}
    
	
    


}
