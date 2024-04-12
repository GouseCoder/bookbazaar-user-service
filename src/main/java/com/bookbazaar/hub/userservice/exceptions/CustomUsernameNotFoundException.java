package com.bookbazaar.hub.userservice.exceptions;

public class CustomUsernameNotFoundException extends RuntimeException{
	
	private String message;

	public CustomUsernameNotFoundException(String message) {
		super();
		this.message = message;
	}
	
	

}
