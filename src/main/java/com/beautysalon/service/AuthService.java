package com.beautysalon.service;

import com.beautysalon.dto.GoogleLoginRequest;
import com.beautysalon.dto.LoginRequest;
import com.beautysalon.dto.LoginResponse;
import com.beautysalon.dto.RegisterRequest;
import com.beautysalon.entity.User;
import com.beautysalon.enums.Role;
import com.beautysalon.exception.EmailAlreadyExistsException;
import com.beautysalon.exception.InvalidCredentialsException;
import com.beautysalon.exception.BadRequestException;
import com.beautysalon.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.beautysalon.jwt.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleAuthService googleAuthService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, GoogleAuthService googleAuthService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleAuthService = googleAuthService;
    }

    public void register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email se već koristi");
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

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Pogrešan email ili lozinka"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Pogrešan email ili lozinka");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, user.getFullName(), user.getRole(), user.getPhone(), false);
    }

    public LoginResponse googleLogin(GoogleLoginRequest request) {

        GoogleAuthService.GoogleUserInfo googleUser = googleAuthService.verifyToken(request.getCredential());

        User user = userRepository.findByEmail(googleUser.getEmail()).orElse(null);

        if (user != null && user.getPasswordHash() != null) {
            throw new BadRequestException("Ovaj email je već registriran. Prijavite se lozinkom.");
        }

        if (user == null) {
            user = new User();
            user.setFullName(googleUser.getFullName());
            user.setEmail(googleUser.getEmail());
            user.setPasswordHash(null);
            user.setRole(Role.CUSTOMER);
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        boolean needsPhoneNumber = (user.getPhone() == null);

        return new LoginResponse(token, user.getFullName(), user.getRole(), user.getPhone(), needsPhoneNumber);
    }

    public void updatePhone(String email, String phone) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Korisnik nije pronađen"));

        user.setPhone(phone);
        userRepository.save(user);
    }
}