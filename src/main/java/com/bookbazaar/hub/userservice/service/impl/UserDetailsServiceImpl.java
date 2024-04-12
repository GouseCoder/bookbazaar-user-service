package com.bookbazaar.hub.userservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.bookbazaar.hub.userservice.dto.CustomUserDetails;
import com.bookbazaar.hub.userservice.entity.UserInfo;
import com.bookbazaar.hub.userservice.repo.UserRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
    private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
		
		UserInfo user = userRepository.findByEmail(emailId).get();
        if(user == null){
            logger.error("Email not found: " + emailId);
            throw new UsernameNotFoundException("could not found user..!!");
        }
        logger.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);
	}


}
