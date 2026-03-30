package com.example.firblockchainbackend.controller;

import com.example.firblockchainbackend.model.PasswordResetToken;
import com.example.firblockchainbackend.model.Role;
import com.example.firblockchainbackend.model.User;
import com.example.firblockchainbackend.payload.*;
import com.example.firblockchainbackend.repository.PasswordResetTokenRepository;
import com.example.firblockchainbackend.repository.RoleRepository;
import com.example.firblockchainbackend.repository.UserRepository;
import com.example.firblockchainbackend.security.JwtUtils;
import com.example.firblockchainbackend.security.UserDetailsImpl;
import com.example.firblockchainbackend.service.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordResetTokenRepository tokenRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        System.out.println("DEBUG: forgot-password request for: " + request.getEmail());
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            System.out.println("DEBUG: User not found for email: " + request.getEmail());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User with this email does not exist!"));
        }

        // Generate 6-digit OTP
        String otp = String.format("%06d", new SecureRandom().nextInt(999999));
        
        // Delete old tokens for this user
        tokenRepository.deleteAllTokensForUser(user);
        
        // Save new token
        PasswordResetToken token = new PasswordResetToken(otp, user, 10);
        tokenRepository.save(token);

        // Send Email
        try {
            System.out.println("DEBUG: Attempting to send OTP to " + user.getEmail());
            emailService.sendEmail(user.getEmail(), 
                "POLICE PLATFORM: Secure Reset OTP", 
                "Your identity verification code is: " + otp + "\n\nThis code expires in 10 minutes. If you did not request this, please ignore this message.");
            System.out.println("DEBUG: OTP sent successfully!");
        } catch (Exception e) {
            System.err.println("ERROR: SMTP Failure: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new MessageResponse("Error: Could not send email. Please check your SMTP settings in application.properties (Are you using an App Password?)"));
        }

        return ResponseEntity.ok(new MessageResponse("OTP sent successfully to " + user.getEmail()));
    }

    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(request.getOtp());
        
        if (token == null || token.isExpired()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid or expired OTP!"));
        }

        User user = token.getUser();
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Clean up token
        tokenRepository.delete(token);

        return ResponseEntity.ok(new MessageResponse("Password reset successful!"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.getUsername()) != null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.findByEmail(signUpRequest.getEmail()) != null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName("ROLE_USER");
            if(userRole == null) {
                userRole = new Role(null, "ROLE_USER");
                roleRepository.save(userRole);
            }
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleRepository.findByName("ROLE_ADMIN");
                    if(adminRole == null) {
                        adminRole = new Role(null, "ROLE_ADMIN");
                        roleRepository.save(adminRole);
                    }
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName("ROLE_USER");
                    if(userRole == null) {
                        userRole = new Role(null, "ROLE_USER");
                        roleRepository.save(userRole);
                    }
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
