package com.beautysalon.service;

import com.beautysalon.dto.LoginRequest;
import com.beautysalon.dto.LoginResponse;
import com.beautysalon.dto.RegisterRequest;
import com.beautysalon.entity.User;
import com.beautysalon.enums.Role;
import com.beautysalon.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.beautysalon.jwt.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {

    if(userRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("Uneseni Email se vec koristi");
    }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("Email ili lozinka nisu tocni"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Email ili lozinka nisu tocni");
        }

        String token=jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, user.getFullName(), user.getRole());
    }



}