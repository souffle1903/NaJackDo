package com.najackdo.server.domain.auth.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.najackdo.server.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User, UserDetails {
	private final User user;

	@Override
	public Map<String, Object> getAttributes() {
		return Map.of("username", user.getUsername(), "authorities", getAuthorities());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add((GrantedAuthority) () -> user.getRoleType().name());
		return collection;
	}

	@Override
	public String getPassword() {
		return user.getProviderId();
	}

	public String getName() {return user.getName();}

	public String getUsername() {
		return user.getUsername();
	}
}
