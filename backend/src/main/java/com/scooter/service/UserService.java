package com.scooter.service;

import com.scooter.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDTO register(UserDTO userDTO);
    
    UserDTO findById(Long id);
    
    UserDTO findByUsername(String username);
    
    List<UserDTO> findAll();
    
    UserDTO update(Long id, UserDTO userDTO);
    
    void delete(Long id);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    long countCustomers();
} 