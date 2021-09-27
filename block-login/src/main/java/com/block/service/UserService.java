package com.block.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.block.model.ClientUser;
import com.block.model.CustomOAuth2User;
import com.block.model.UserRole;
import com.block.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	
	private final UserRepository repo;
	
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	
	
	// 구글로그인인 경우에 DB Entity의 나머지 빈 값들을 채워준다.
	// 초기비밀번호는 1234, 한번 구글로그인해서 DB에 들어오면 사이트내 1234입력해서 로그인 가능.
	public void processOAuthPostLogin(CustomOAuth2User oauthUser) {
		String email = oauthUser.getEmail();
		String name = oauthUser.getName();

		ClientUser existUser = repo.findUserByEmail(email);

		if (existUser == null) {
			ClientUser newUser = new ClientUser();
			newUser.setEmail(email);
			newUser.setUsername(name);
			newUser.setFromSocial("GOOGLE");
			newUser.addUserRole(UserRole.ROLE_USER);
			newUser.setPassword("1234");
			newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
			
			repo.saveUser(newUser);
		}
	}
	
	//캡스톤
	public void processOAuthPostLogin(String email, String name) {

		ClientUser existUser = repo.findUserByEmail(email);

		if (existUser == null) {
			ClientUser newUser = new ClientUser();
			newUser.setEmail(email);
			newUser.setUsername(name);
			newUser.setFromSocial("CAPSTONE");
			newUser.addUserRole(UserRole.ROLE_USER);
			newUser.setPassword("1234");
			newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
			
			repo.saveUser(newUser);
		}
	}
	
	
	@Transactional
	public void joinUser(ClientUser user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		repo.saveUser(user);
	}

	public List<ClientUser> findAll() {
		return repo.findAllUser();
	}
	
	public ClientUser findUserByEmail(String Email) {
		return repo.findUserByEmail(Email);
	}

	public void delete(Long id) {
		repo.deleteUserRole(id);
		repo.deleteUser(id);
	}
	
	public void giveAdmin(Long id) {
		repo.giveAdmin(id);
	}
}
