package com.aulas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aulas.model.User;
import com.aulas.model.enums.Role;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

   List<User> findByRole(Role role);
    

}
