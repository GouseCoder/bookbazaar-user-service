package com.bookbazaar.hub.userservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookbazaar.hub.userservice.dto.JwtResponse;
import com.bookbazaar.hub.userservice.dto.LoginRequest;
import com.bookbazaar.hub.userservice.dto.UserRequestDto;
import com.bookbazaar.hub.userservice.exceptions.CustomUsernameNotFoundException;
import com.bookbazaar.hub.userservice.service.UserService;
import com.bookbazaar.hub.userservice.utils.ApiHttpResponse;
import com.bookbazaar.hub.userservice.utils.AppConstants;
import com.bookbazaar.hub.userservice.utils.CommonsUtils;
import com.bookbazaar.hub.userservice.utils.JacksonUtil;
import com.bookbazaar.hub.userservice.utils.JwtService;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.ApiOperation;

@RestController
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
    private UserService userService;
	
	@Autowired
	CommonsUtils commonsUtils;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtService jwtService;

    @PostMapping("/register")
    @ApiOperation(value = "Register account")
    public ResponseEntity<ApiHttpResponse> registerUser(@RequestBody UserRequestDto user) {
    	
    	JsonNode resultNode = JacksonUtil.mapper.createArrayNode();
    	
    	try {
    		logger.info("Request from user for registration : {}" , JacksonUtil.mapper.writeValueAsString(user));
    		resultNode = userService.registerUser(user);
    		logger.debug("resultNode " + resultNode);
    		
    		return new ResponseEntity<>(new ApiHttpResponse(commonsUtils.getStatusCode(resultNode), 
    				resultNode.get(AppConstants.ERROR_OBJECT), 
					resultNode.get(AppConstants.DATA_OBJECT)), HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<>(new ApiHttpResponse(AppConstants.INTERNAL_SERVER_ERROR, 
					resultNode.get(AppConstants.ERROR_OBJECT)), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	
    
    }
    
    @GetMapping("/validateotp")
	public ResponseEntity<ApiHttpResponse> validateOtp(@RequestParam(value = "emailId") String emailId, @RequestParam(value = "OTP") String OTP) {
    	
    	JsonNode resultNode = JacksonUtil.mapper.createArrayNode();

		try {
			
			resultNode = userService.handleOtpValidation(OTP, emailId);
			return new ResponseEntity<>(new ApiHttpResponse(commonsUtils.getStatusCode(resultNode), 
    				resultNode.get(AppConstants.ERROR_OBJECT), 
					resultNode.get(AppConstants.DATA_OBJECT)), HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(new ApiHttpResponse(AppConstants.INTERNAL_SERVER_ERROR, 
					resultNode.get(AppConstants.ERROR_OBJECT)), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

	@GetMapping("/regenerateotp")
	public ResponseEntity<ApiHttpResponse> resendotp(@RequestParam(value = "firstName") String username, @RequestParam(value = "email") String email) {
		
		JsonNode resultNode = JacksonUtil.mapper.createArrayNode();
		
		try {
			
			resultNode = userService.regenerateOtpandSendMail(username, email);
			return new ResponseEntity<>(new ApiHttpResponse(commonsUtils.getStatusCode(resultNode), 
    				resultNode.get(AppConstants.ERROR_OBJECT), 
					resultNode.get(AppConstants.DATA_OBJECT)), HttpStatus.OK);
						
		} catch (Exception e) {
			
			logger.error("error in regenerating otp ", e);
			
			return new ResponseEntity<>(new ApiHttpResponse(AppConstants.INTERNAL_SERVER_ERROR, 
					resultNode.get(AppConstants.ERROR_OBJECT)), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping("/addUserDetails")
	public ResponseEntity<ApiHttpResponse> addNewUser(@RequestParam String email, @RequestParam String password) {

		JsonNode resultNode = JacksonUtil.mapper.createArrayNode();

		try {

			resultNode = userService.saveUserDetailsInDB(email, password);
			return new ResponseEntity<>(new ApiHttpResponse(commonsUtils.getStatusCode(resultNode), 
    				resultNode.get(AppConstants.ERROR_OBJECT), 
					resultNode.get(AppConstants.DATA_OBJECT)), HttpStatus.OK);

		} catch (Exception e) {

			logger.error("Exception in creating user : ", e);
			return new ResponseEntity<>(new ApiHttpResponse(AppConstants.INTERNAL_SERVER_ERROR, 
					resultNode.get(AppConstants.ERROR_OBJECT)), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    
    @PostMapping("/login")
    public JwtResponse AuthenticateAndGetToken(@RequestBody LoginRequest authRequestDTO){
    	
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getEmailId(), authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
        	System.out.println("user authenticated");
        	String userId = userService.getUserIdFromEmail(authRequestDTO.getEmailId());
           return new JwtResponse(jwtService.GenerateToken(userId));
        } else {
        	System.out.println("user not authenticated");
            throw new CustomUsernameNotFoundException("invalid user request..!!");
        }
    }
    
    @GetMapping("/v1/logout")
    public String logout() {
        // You can perform additional cleanup or logging out logic here
        return "Logout successful";
    }

}
