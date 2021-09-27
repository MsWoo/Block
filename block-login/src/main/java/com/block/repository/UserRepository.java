package com.block.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.block.model.ClientUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {

	private final EntityManager em;

	public void saveUser(ClientUser user){
		em.persist(user);
	}
	
	public ClientUser findUserByUsername(String username){
		TypedQuery<ClientUser> query = em.createQuery("select m from ClientUser as m where m.username = ?1", ClientUser.class)
			.setParameter(1, username);
		return query.getSingleResult();
	}
	
	public ClientUser findUserById(Long id){
		TypedQuery<ClientUser> query = em.createQuery("select m from ClientUser as m where m.id = ?1", ClientUser.class)
			.setParameter(1, id);
		return query.getSingleResult();
	}


	public ClientUser findUserByEmail(String email){
		TypedQuery<ClientUser> query = em.createQuery("select m from ClientUser as m where m.email = ?1", ClientUser.class)
			.setParameter(1, email);
		try {
	        return query.getSingleResult();
	    } catch (NoResultException nre) {
	        return null;
	    }
	}
	
	public List<ClientUser> findAllUser(){
		List<ClientUser> users = em.createQuery("SELECT u FROM ClientUser u").getResultList();
		return users;
	}
	
	public void deleteUser(Long id) {
		Query query = em.createQuery("DELETE FROM ClientUser WHERE id = ?1")
			.setParameter(1, id);
		query.executeUpdate();
	}
	
	public void deleteUserRole(Long id) {
		Query query = em.createNativeQuery("DELETE FROM client_user_roles WHERE client_user_id = ?1")
			.setParameter(1, id);
		query.executeUpdate();
	}
	
	public void giveAdmin(Long id) {
		Query query = em.createNativeQuery("INSERT INTO client_user_roles (client_user_id, roles) VALUES (?1, 1)")
		.setParameter(1, id);
		query.executeUpdate();
	}
	
}
