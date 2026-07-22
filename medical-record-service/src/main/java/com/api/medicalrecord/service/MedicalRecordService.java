package com.api.medicalrecord.service;

import com.api.medicalrecord.dto.MedicalRecordRequestDTO;
import com.api.medicalrecord.dto.MedicalRecordResponseDTO;
import com.api.medicalrecord.model.MedicalRecordModel;
import com.api.medicalrecord.repository.MedicalRecordRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordService {

  private final MedicalRecordRepository medicalRecordRepository;

  public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
    this.medicalRecordRepository = medicalRecordRepository;
  }

  public List<MedicalRecordResponseDTO> getAllMedicalRecords() {
    return medicalRecordRepository
      .findAll()
      .stream()
      .map(this::toDTO)
      .collect(Collectors.toList());
  }

  public Optional<MedicalRecordResponseDTO> getMedicalRecordById(String id) {
    return medicalRecordRepository.findById(id).map(this::toDTO);
  }

  public MedicalRecordResponseDTO createMedicalRecord(
    MedicalRecordRequestDTO request
  ) {
    MedicalRecordModel model = new MedicalRecordModel();
    model.setAppointmentId(request.getAppointmentId());
    model.setDoctorId(request.getDoctorId());
    model.setPatientId(request.getPatientId());
    model.setComplaints(request.getComplaints());
    model.setDiagnosis(request.getDiagnosis());
    model.setBloodPressure(request.getBloodPressure());
    model.setTemperature(request.getTemperature());
    model.setMedications(request.getMedications());
    model.setDoctorNotes(request.getDoctorNotes());

    MedicalRecordModel saved = medicalRecordRepository.save(model);
    return toDTO(saved);
  }

  public Optional<MedicalRecordResponseDTO> updateMedicalRecord(
    String id,
    MedicalRecordRequestDTO request
  ) {
    return medicalRecordRepository.findById(id).map(record -> {
      record.setAppointmentId(request.getAppointmentId());
      record.setDoctorId(request.getDoctorId());
      record.setPatientId(request.getPatientId());
      record.setComplaints(request.getComplaints());
      record.setDiagnosis(request.getDiagnosis());
      record.setBloodPressure(request.getBloodPressure());
      record.setTemperature(request.getTemperature());
      record.setMedications(request.getMedications());
      record.setDoctorNotes(request.getDoctorNotes());

      MedicalRecordModel saved = medicalRecordRepository.save(record);
      return toDTO(saved);
    });
  }

  public void deleteMedicalRecord(String id) {
    medicalRecordRepository.deleteById(id);
  }

  public MedicalRecordResponseDTO toDTO(MedicalRecordModel model) {
    return MedicalRecordResponseDTO.builder()
      .id(model.getId())
      .appointmentId(model.getAppointmentId())
      .doctorId(model.getDoctorId())
      .patientId(model.getPatientId())
      .complaints(model.getComplaints())
      .diagnosis(model.getDiagnosis())
      .bloodPressure(model.getBloodPressure())
      .temperature(model.getTemperature())
      .medications(model.getMedications())
      .doctorNotes(model.getDoctorNotes())
      .createdAt(model.getCreatedAt())
      .updatedAt(model.getUpdatedAt())
      .build();
  }
}
