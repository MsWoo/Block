package com.block;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;

@SpringBootApplication
public class BlockLoginApplication  implements ServletContextInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BlockLoginApplication.class, args);
	}

//  쿠키설정
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		servletContext.getSessionCookieConfig().setName("clientsession");
	}
}
