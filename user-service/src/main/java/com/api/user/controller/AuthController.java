package com.api.user.controller;

import com.api.user.dto.RegisterRequestDTO;
import com.api.user.dto.UserResponseDTO;
import com.api.user.model.AuthRequest;
import com.api.user.model.UserModel;
import com.api.user.repository.UserRepository;
import com.api.user.utility.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder encoder;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
    // Check if email already exists
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      return ResponseEntity.badRequest().body(
        Map.of("error", "Email is already registered")
      );
    }

    // Create new user from DTO
    UserModel user = new UserModel();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(encoder.encode(request.getPassword()));
    user.setPhone(request.getPhone());

    // Set role (default to PATIENT if not specified)
    if (request.getRole() != null && !request.getRole().isBlank()) {
      user.setRole(request.getRole().toUpperCase());
    } else {
      user.setRole("PATIENT");
    }

    UserModel savedUser = userRepository.save(user);

    // Return response DTO (without password)
    UserResponseDTO response = UserResponseDTO.builder()
      .id(savedUser.getId())
      .name(savedUser.getName())
      .email(savedUser.getEmail())
      .role(savedUser.getRole())
      .phone(savedUser.getPhone())
      .createdAt(savedUser.getCreatedAt())
      .build();

    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    try {
      Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          request.getEmail(),
          request.getPassword()
        )
      );
      UserDetails userDetails = (UserDetails) auth.getPrincipal();
      String token = jwtUtil.generateToken(userDetails);

      // Get user details for response
      UserModel user = userRepository
        .findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));

      UserResponseDTO userResponse = UserResponseDTO.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .role(user.getRole())
        .phone(user.getPhone())
        .createdAt(user.getCreatedAt())
        .build();

      Map<String, Object> response = new HashMap<>();
      response.put("token", token);
      response.put("user", userResponse);

      return ResponseEntity.ok(response);
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
        Map.of("error", "Invalid credentials")
      );
    }
  }
}
