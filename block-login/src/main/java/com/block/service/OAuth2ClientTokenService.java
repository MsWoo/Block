package com.block.service;
//package com.example.client.service;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.Date;
//
//import org.apache.commons.lang3.StringUtils;
//import com.example.client.entity.ClientUser;
//import com.example.client.repository.ClientUserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
//import org.springframework.security.oauth2.client.token.ClientTokenServices;
//import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
//import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.stereotype.Service;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class OAuth2ClientTokenService implements ClientTokenServices {
//	
//	@Autowired private ClientUserRepository repository;
//	
//	
//	@Override
//	public OAuth2AccessToken getAccessToken(OAuth2ProtectedResourceDetails resource, Authentication authentication) {
//		log.info("OAuth2ClientTokenService.getAccessToken ::::");
//		ClientUser findUser = (ClientUser) getUser(authentication);
//		
//		String accessToken = findUser.getAccess_token();
//		LocalDateTime expirationDate = findUser.getAccess_token_validity();
//		
//		if(StringUtils.isEmpty(accessToken)) {
//			return null;
//		}
//		
//		DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
//		oAuth2AccessToken.setExpiration(Date.from(expirationDate.atZone(ZoneId.systemDefault()).toInstant()));
//		oAuth2AccessToken.setRefreshToken(new DefaultOAuth2RefreshToken(findUser.getRefresh_token()));
////		
//		System.out.println("get " + accessToken);
////		
//		return oAuth2AccessToken;
//	}
//
//	@Override
//	public void saveAccessToken(OAuth2ProtectedResourceDetails resource, Authentication authentication,
//			OAuth2AccessToken accessToken) {
//		log.info("OAuth2ClientTokenService.saveAccessToken ::::");
//		Date expiration = accessToken.getExpiration();
//		
//		ClientUser findUser = (ClientUser) getUser(authentication);
//		findUser.setAccess_token(accessToken.getValue());
//		findUser.setAccess_token_validity(LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault()));
//		findUser.setRefresh_token(accessToken.getRefreshToken().getValue());
////		
//		System.out.println("save " + accessToken.getValue());
////		
//		repository.save(findUser);
//	}
//
//	@Override
//	public void removeAccessToken(OAuth2ProtectedResourceDetails resource, Authentication authentication) {
//		log.info("OAuth2ClientTokenService.removeAccessToken ::::");
//		ClientUser findUser = (ClientUser) getUser(authentication);
//		findUser.setAccess_token(null);
//		findUser.setRefresh_token(null);
//		findUser.setAccess_token_validity(null);
//		
//		repository.save(findUser);
//	}
//	
//	private UserDetails getUser(Authentication authentication) {
//		log.info("OAuth2ClientTokenService.getUser ::::");
//		ClientUser resourceOwner = (ClientUser) authentication.getPrincipal();
//		
//		Long userId = resourceOwner.getId();
//		
//		return repository.findById(userId).orElseThrow(()->{
//			return new UsernameNotFoundException("사용자 아이디를 찾을 수 없습니다.");
//		});
//		
//	}
//
//}
