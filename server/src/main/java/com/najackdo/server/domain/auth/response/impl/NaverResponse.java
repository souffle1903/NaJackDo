package com.najackdo.server.domain.auth.response.impl;

import java.util.Map;

import com.najackdo.server.domain.auth.response.OAuth2Response;
import com.najackdo.server.domain.user.entity.ProviderType;

public class NaverResponse implements OAuth2Response {
	private final Map<String, Object> attribute;

	public NaverResponse(Map<String, Object> attribute) {
		this.attribute = (Map<String, Object>)attribute.get("response");
	}

	@Override
	public ProviderType getProvider() {
		return ProviderType.NAVER;
	}

	@Override
	public String getProviderId() {
		return attribute.get("id").toString();
	}

	@Override
	public String getEmail() {
		return attribute.get("email").toString();
	}

	@Override
	public String getName() {
		return attribute.get("name").toString();
	}

	@Override
	public String getGender() {
		return attribute.get("gender").toString();
	}

	@Override
	public String getProfileImage() {
		return attribute.get("profile_image").toString();
	}
}
