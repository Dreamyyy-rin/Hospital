package com.api.doctor.service;

import com.api.doctor.model.DoctorModel;
import com.api.doctor.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<DoctorModel> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<DoctorModel> getDoctorById(Integer id) {
        return doctorRepository.findById(id);
    }

    public DoctorModel createDoctor(DoctorModel doctor) {
        return doctorRepository.save(doctor);
    }

    public DoctorModel updateDoctor(Integer id, DoctorModel updatedDoctor) {
        return doctorRepository.findById(id).map(doctor -> {
            doctor.setUser(updatedDoctor.getUser());
            doctor.setSpecialization(updatedDoctor.getSpecialization());
            doctor.setLicenseNumber(updatedDoctor.getLicenseNumber());
            return doctorRepository.save(doctor);
        }).orElseThrow(() -> new RuntimeException("Doctor not found with id " + id));
    }

    public void deleteDoctor(Integer id) {
        doctorRepository.deleteById(id);
    }
}
