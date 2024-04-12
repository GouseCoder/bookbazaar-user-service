package com.bookbazaar.hub.userservice.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bookbazaar.hub.userservice.entity.UserInfo;
import com.bookbazaar.hub.userservice.entity.UserRole;

public class CustomUserDetails extends UserInfo implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	private String username;
    private String password;
    Collection<? extends GrantedAuthority> authorities;
    
    public CustomUserDetails(UserInfo byUsername) {
        this.username = byUsername.getFirstName();
        this.password= byUsername.getPassword();
        List<GrantedAuthority> auths = new ArrayList<>();

        for(UserRole role : byUsername.getRoles()){

            auths.add(new SimpleGrantedAuthority(role.getName().toUpperCase()));
        }
        this.authorities = auths;
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}
	
	@Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
