package com.block.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.block.model.ClientUser;

public interface ClientUserRepository extends JpaRepository<ClientUser, Long>{
	public ClientUser findByUsername(String username);
}
