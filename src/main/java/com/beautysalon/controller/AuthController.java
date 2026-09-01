package com.beautysalon.controller;

import com.beautysalon.dto.*;
import com.beautysalon.service.AuthService;
import com.beautysalon.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RateLimitService rateLimitService;

    public AuthController(AuthService authService, RateLimitService rateLimitService) {
        this.authService = authService;
        this.rateLimitService = rateLimitService;
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {

        String ip = getClientIp(httpRequest);
        if (!rateLimitService.isAllowedPerHour("register:" + ip, 7)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        String ip = getClientIp(httpRequest);
        String key = ip + ":" + request.getEmail();

        if (!rateLimitService.isLoginAllowed(key)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        try {
            LoginResponse response = authService.login(request);
            rateLimitService.recordSuccessfulLogin(key);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            rateLimitService.recordFailedLogin(key);
            throw e;
        }
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        LoginResponse response = authService.googleLogin(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest) {

        String ip = getClientIp(httpRequest);
        if (!rateLimitService.isAllowedPerHour("forgot-password:" + ip, 5)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request.getToken());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/phone")
    public ResponseEntity<Void> updatePhone(
            @RequestBody UpdatePhoneRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        authService.updatePhone(email, request.getPhone());

        return ResponseEntity.ok().build();
    }
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest) {

        String ip = getClientIp(httpRequest);
        if (!rateLimitService.isAllowedPerHour("resend-verification:" + ip, 4)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        authService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }
}