package com.api.doctor.service;

import com.api.doctor.dto.DoctorRequestDTO;
import com.api.doctor.dto.DoctorResponseDTO;
import com.api.doctor.model.DoctorModel;
import com.api.doctor.model.UserModel;
import com.api.doctor.repository.DoctorRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {

  private final DoctorRepository doctorRepository;

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
    UserModel user = new UserModel();
    user.setId(request.getUserId());

    DoctorModel doctor = new DoctorModel();
    doctor.setUser(user);
    doctor.setSpecialization(request.getSpecialization());
    doctor.setLicenseNumber(request.getLicenseNumber());
    doctor.setStatus(
      request.getStatus() != null ? request.getStatus() : "ACTIVE"
    );

    DoctorModel saved = doctorRepository.save(doctor);
    return toDTO(saved);
  }

  public DoctorResponseDTO updateDoctor(Integer id, DoctorRequestDTO request) {
    DoctorModel doctor = doctorRepository
      .findById(id)
      .orElseThrow(() ->
        new RuntimeException("Doctor not found with id " + id)
      );

    if (request.getUserId() != null) {
      doctor.getUser().setId(request.getUserId());
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
    if (doctor.getUser() != null) {
      dto.setUserId(doctor.getUser().getId());
    }
    dto.setSpecialization(doctor.getSpecialization());
    dto.setLicenseNumber(doctor.getLicenseNumber());
    dto.setStatus(doctor.getStatus());
    return dto;
  }
}
