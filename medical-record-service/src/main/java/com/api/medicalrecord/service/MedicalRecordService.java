package com.api.medicalrecord.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.medicalrecord.model.MedicalRecordModel;
import com.api.medicalrecord.repository.MedicalRecordRepository;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public List<MedicalRecordModel> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    public Optional<MedicalRecordModel> getMedicalRecordById(String id) {
        return medicalRecordRepository.findById(id);
    }

    public MedicalRecordModel createMedicalRecord(MedicalRecordModel medicalRecord) {
        return medicalRecordRepository.save(medicalRecord);
    }

    public MedicalRecordModel updateMedicalRecord(String id, MedicalRecordModel updatedRecord) {
        return medicalRecordRepository.findById(id).map(record -> {
            record.setAppointmentId(updatedRecord.getAppointmentId());
            record.setDoctorId(updatedRecord.getDoctorId());
            record.setPatientId(updatedRecord.getPatientId());
            record.setComplaints(updatedRecord.getComplaints());
            record.setDiagnosis(updatedRecord.getDiagnosis());
            record.setBloodPressure(updatedRecord.getBloodPressure());
            record.setTemperature(updatedRecord.getTemperature());
            record.setMedications(updatedRecord.getMedications());
            record.setDoctorNotes(updatedRecord.getDoctorNotes());

            return medicalRecordRepository.save(record);
        }).orElseThrow(() -> new RuntimeException("Medical Record not found with id " + id));
    }

    public void deleteMedicalRecord(String id) {
        medicalRecordRepository.deleteById(id);
    }

}
