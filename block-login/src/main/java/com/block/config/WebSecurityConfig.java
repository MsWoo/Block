package com.block.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.block.handler.CustomAuthenticationFailureHandler;
import com.block.handler.CustomAuthenticationSuccessHandler;
import com.block.handler.ResourceOwnerAuthenticationFilter;
import com.block.model.CustomOAuth2User;
import com.block.service.CustomOAuth2UserService;
import com.block.service.UserDetailsServiceImpl;
import com.block.service.UserService;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomOAuth2UserService oauthUserService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {	
		web.ignoring()
		   .antMatchers("/css/**")
		   .antMatchers("/vendor/**")
		   .antMatchers("/js/**")
		   .antMatchers("/favicon*/**")
		   .antMatchers("/img/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {	
		http.cors()
					.and()
			.authorizeRequests()
			.antMatchers("/", "/oauth/**", "/home", "/signup").permitAll()
			.antMatchers("/login*/**").permitAll()
			.antMatchers("/mypage", "/board/BoardList").hasRole("USER")
			.antMatchers("/list").hasRole("ADMIN")
			.anyRequest().authenticated()
				.and()
			.csrf().disable()
			.addFilter(authenticationFilter())
			.exceptionHandling()
			.authenticationEntryPoint(authenticationEntryPoint())
			.accessDeniedHandler((request,response,exception)->{
				response.setContentType("application/json;charset=UTF-8");
		        response.setHeader("Cache-Control", "no-cache");	          
		        PrintWriter writer = response.getWriter();
		        writer.println(new AccessDeniedException("access denied !"));
			})
					.and()
			.oauth2Login()
				.userInfoEndpoint()
				.userService(oauthUserService)
					.and()
				.successHandler(new AuthenticationSuccessHandler() {
					@Override
					public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
							Authentication authentication) throws IOException, ServletException {
						
//						authentication : 로그인 성공한 OAuth User의 정보
						String message = authentication.getName().toString();
						String[] info = message.split(",");
						String client = info[0];
//						캡스톤
						if(client.equals("capstone")) {
							String name = info[1];
							String email = info[2];
							userService.processOAuthPostLogin(email, name);
						}
//						구글
						else {
							CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
							userService.processOAuthPostLogin(oauthUser);
						}
					
						response.sendRedirect("/");					
					}
				})
					.and()
			.logout().logoutSuccessUrl("/loginPage");	  
	
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		
		return source;
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return authenticationProvider;
	}
	
	@Bean
	public ResourceOwnerAuthenticationFilter authenticationFilter() throws Exception {
		ResourceOwnerAuthenticationFilter filter = new ResourceOwnerAuthenticationFilter(authenticationManager());
		filter.setFilterProcessesUrl("/login");
		filter.setUsernameParameter("username");
		filter.setPasswordParameter("password");
		
		filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
		filter.setAuthenticationFailureHandler(authenticationFailureHandler());
		
		filter.afterPropertiesSet();
		
		return filter;
	}
	
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new LoginUrlAuthenticationEntryPoint("/loginPage");
	}
	
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
		successHandler.setDefaultTargetUrl("/");
		
		return successHandler;
	}
	
	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		CustomAuthenticationFailureHandler failureHandler = new CustomAuthenticationFailureHandler();
		failureHandler.setDefaultFailureUrl("/loginPage?error=loginfali");
		
		return failureHandler;
	}

}
