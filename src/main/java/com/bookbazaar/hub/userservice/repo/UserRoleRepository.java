package com.bookbazaar.hub.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookbazaar.hub.userservice.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long>{

	UserRole findByName(String name);

}
