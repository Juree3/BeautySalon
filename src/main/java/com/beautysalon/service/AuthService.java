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
import com.beautysalon.exception.ResourceNotFoundException;
import com.beautysalon.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.beautysalon.jwt.JwtService;
import java.time.LocalDateTime;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleAuthService googleAuthService;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, GoogleAuthService googleAuthService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleAuthService = googleAuthService;
        this.emailService = emailService;
    }

    public void register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email se već koristi");
        }

        String token = UUID.randomUUID().toString();

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setEmailVerified(false);
        user.setVerificationToken(token);

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Pogrešan email ili lozinka"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Pogrešan email ili lozinka");
        }

        if (!user.getEmailVerified()) {
            throw new BadRequestException("Molimo prvo potvrdite svoju email adresu prije prijave");
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
            user.setEmailVerified(true);
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
    public void verifyEmail(String token) {

        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Nevažeći ili istekao verifikacijski link"));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return;
        }

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(String token, String newPassword) {

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new BadRequestException("Nevažeći link za promjenu lozinke"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Link je istekao, zatražite novi");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }


    public void resendVerificationEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen"));

        if (user.getEmailVerified()) {
            throw new BadRequestException("Email je već verificiran");
        }

        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), newToken);
    }
}