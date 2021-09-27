package com.block.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.block.model.ClientUser;
import com.block.model.CustomOAuth2User;
import com.block.model.UserRole;
import com.block.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ClientController {
	
	private final UserService service;
	
	
	@RequestMapping("/")
	public String home(Model model, Authentication authentication) {
//		로그인을 했으면
		if(authentication != null) {
//			MyUserDetail
			if(authentication.getPrincipal() instanceof ClientUser) {
				ClientUser userDetail = (ClientUser) authentication.getPrincipal();
				model.addAttribute("name", userDetail.getUsername());
			}
//			CustomOAuth2User
			else if(authentication.getPrincipal() instanceof CustomOAuth2User){
				CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
				model.addAttribute("name", oauthUser.getName());
			}
//			Capstone User
			else {
				String userInfo = authentication.getName();
				String name = userInfo.split(",")[1];
				model.addAttribute("name", name);
			}
		}
		return "home";
	}
	@RequestMapping("/loginPage")
	public String loginForm() {
	  return "login";
	}


	@GetMapping("/signup")
	public String signUpForm() {
		return "signup";
	}
	
	@PostMapping("/signup")
    public String signUp(ClientUser user) {
    	user.addUserRole(UserRole.ROLE_USER);
//		user.addUserRole(UserRole.ROLE_ADMIN);
        user.setFromSocial("");
        service.joinUser(user);
        return "redirect:/loginPage";
    }
	
	@RequestMapping("/mypage")
	private String mypage(Model model, Authentication authentication) {
//		MyUserDetail
		if(authentication.getPrincipal() instanceof ClientUser) {
			ClientUser userDetail = (ClientUser) authentication.getPrincipal();
            model.addAttribute("email", userDetail.getEmail()); 
            model.addAttribute("name", userDetail.getUsername());
    	}
//    	CustomOAuth2User (Google)
    	else if(authentication.getPrincipal() instanceof CustomOAuth2User){
    		CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        	model.addAttribute("email", oauthUser.getEmail());
        	model.addAttribute("name", oauthUser.getName());
    	}
//		Capstone User
    	else {
    		String userInfo = authentication.getName();
    		String name = userInfo.split(",")[1];
    		String email = userInfo.split(",")[2];
    		model.addAttribute("email", email);
        	model.addAttribute("name", name);
    	}    	
		return "mypage";
	}

    @RequestMapping("/list")
	private String userList(Model model, HttpServletRequest request) {
		List<ClientUser> userList = new ArrayList<>();
		userList = service.findAll();
		model.addAttribute("userlist", userList);
		return "list";
	}
    
    @RequestMapping("/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Long id) {
		service.delete(id);
		return "redirect:/list";
	}
    
    @RequestMapping("/update/{id}")
	public String updateUser(@PathVariable(name = "id") Long id) {
    	service.giveAdmin(id);
		return "redirect:/list";	
	}

	
}
