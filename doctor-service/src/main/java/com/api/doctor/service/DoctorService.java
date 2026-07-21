package com.api.doctor.service;

import com.api.doctor.client.RegisterRequest;
import com.api.doctor.client.UserClient;
import com.api.doctor.dto.DoctorRequestDTO;
import com.api.doctor.dto.DoctorResponseDTO;
import com.api.doctor.dto.DoctorWithUserRequestDTO;
import com.api.doctor.dto.DoctorWithUserResponseDTO;
import com.api.doctor.model.DoctorModel;
import com.api.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserClient userClient;

    public List<DoctorModel> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<DoctorModel> getDoctorById(Integer id) {
        return doctorRepository.findById(id);
    }

    public Optional<DoctorModel> getDoctorByUserId(Integer userId) {
        return doctorRepository.findByUserId(userId);
    }

    public List<DoctorModel> getActiveDoctors() {
        return doctorRepository.findByStatus("ACTIVE");
    }

    public DoctorResponseDTO createDoctor(DoctorRequestDTO request) {
        DoctorModel doctor = new DoctorModel();
        doctor.setUserId(request.getUserId());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");

        DoctorModel saved = doctorRepository.save(doctor);
        return toDTO(saved);
    }

    /**
     * Admin method: Create both user and doctor in one call
     */
    public DoctorWithUserResponseDTO createDoctorWithUser(DoctorWithUserRequestDTO request) {
        // Step 1: Create user via Feign
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName(request.getName());
        registerRequest.setEmail(request.getEmail());
        registerRequest.setPassword(request.getPassword());
        registerRequest.setPhone(request.getPhone());
        registerRequest.setRole("DOCTOR");

        ResponseEntity<UserClient.UserResponse> userResponse = userClient.register(registerRequest);

        if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
            throw new RuntimeException("Failed to create user account");
        }

        UserClient.UserResponse createdUser = userResponse.getBody();

        // Step 2: Create doctor with the created user ID
        DoctorModel doctor = new DoctorModel();
        doctor.setUserId(createdUser.getId());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");

        DoctorModel savedDoctor = doctorRepository.save(doctor);

        // Step 3: Build response
        DoctorWithUserResponseDTO.UserDTO userDTO = DoctorWithUserResponseDTO.UserDTO.builder()
                .id(createdUser.getId())
                .name(createdUser.getName())
                .email(createdUser.getEmail())
                .role(createdUser.getRole())
                .phone(createdUser.getPhone())
                .build();

        DoctorWithUserResponseDTO doctorWithUser = DoctorWithUserResponseDTO.builder()
                .user(userDTO)
                .doctor(toDTO(savedDoctor))
                .build();

        return doctorWithUser;
    }

    public DoctorResponseDTO updateDoctor(Integer id, DoctorRequestDTO request) {
        DoctorModel doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id " + id));

        if (request.getUserId() != null) {
            doctor.setUserId(request.getUserId());
        }
        if (request.getSpecialization() != null) {
            doctor.setSpecialization(request.getSpecialization());
        }
        if (request.getLicenseNumber() != null) {
            doctor.setLicenseNumber(request.getLicenseNumber());
        }
        if (request.getStatus() != null) {
            doctor.setStatus(request.getStatus());
        }

        DoctorModel updated = doctorRepository.save(doctor);
        return toDTO(updated);
    }

    public void deleteDoctor(Integer id) {
        doctorRepository.deleteById(id);
    }

    public DoctorResponseDTO toDTO(DoctorModel doctor) {
        DoctorResponseDTO dto = new DoctorResponseDTO();
        dto.setId(doctor.getId());
        dto.setUserId(doctor.getUserId());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setStatus(doctor.getStatus());

        // Fetch user info via Feign
        if (doctor.getUserId() != null) {
            try {
                ResponseEntity<UserClient.UserResponse> userResponse = userClient.getUserById(doctor.getUserId());
                if (userResponse.getBody() != null) {
                    dto.setUserName(userResponse.getBody().getName());
                    dto.setUserEmail(userResponse.getBody().getEmail());
                }
            } catch (Exception e) {
                // If user service is unavailable, just skip user info
            }
        }

        return dto;
    }
}
