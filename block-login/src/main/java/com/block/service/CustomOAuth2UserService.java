package com.block.service;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.block.model.CustomOAuth2User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final HttpSession httpSession;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

//		System.out.println("1 : " + userRequest.getClientRegistration());
//		System.out.println("2 : " + userRequest.getClientRegistration().getProviderDetails().getAuthorizationUri());
//		System.out.println("3 : " + userRequest.getClientRegistration().getProviderDetails().getJwkSetUri());
//		System.out.println("4 : " + userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
		OAuth2User user = super.loadUser(userRequest);
		
//		캡스톤만 if 들어감, 구글은 if문 거치지않고 바로 CustomOAuth2User로 리턴
		if(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName().equals("response")) {
			Map<String, Object> attributes = user.getAttributes();
			log.info("attributes :: "+attributes);    	
	    	
	        httpSession.setAttribute("login_info", attributes);
			
	        return new DefaultOAuth2User( Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes, "response");
		}

		return new CustomOAuth2User(user);
	}
	
}
