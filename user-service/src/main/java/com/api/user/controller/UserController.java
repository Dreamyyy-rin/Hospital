package com.api.user.controller;

import com.api.user.dto.UserResponseDTO;
import com.api.user.dto.UserUpdateDTO;
import com.api.user.model.UserModel;
import com.api.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

  private final UserRepository userRepository;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
    List<UserResponseDTO> users = userRepository
      .findAll()
      .stream()
      .map(this::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
    UserModel user = userRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found with id " + id)
      );
    return ResponseEntity.ok(toDTO(user));
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<UserResponseDTO> getUserByEmail(
    @PathVariable String email
  ) {
    UserModel user = userRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found with email " + email)
      );
    return ResponseEntity.ok(toDTO(user));
  }

  @GetMapping("/role/{role}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserResponseDTO>> getUsersByRole(
    @PathVariable String role
  ) {
    List<UserResponseDTO> users = userRepository
      .findAll()
      .stream()
      .filter(u -> u.getRole() != null && u.getRole().equalsIgnoreCase(role))
      .map(this::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(users);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponseDTO> updateUser(
    @PathVariable Integer id,
    @RequestBody UserUpdateDTO request
  ) {
    UserModel user = userRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found with id " + id)
      );

    if (request.getName() != null && !request.getName().isBlank()) {
      user.setName(request.getName());
    }
    if (request.getPhone() != null) {
      user.setPhone(request.getPhone());
    }

    UserModel updatedUser = userRepository.save(user);
    return ResponseEntity.ok(toDTO(updatedUser));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException("User not found with id " + id);
    }
    userRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private UserResponseDTO toDTO(UserModel user) {
    return UserResponseDTO.builder()
      .id(user.getId())
      .name(user.getName())
      .email(user.getEmail())
      .role(user.getRole())
      .phone(user.getPhone())
      .createdAt(user.getCreatedAt())
      .build();
  }
}
