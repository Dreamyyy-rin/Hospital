package com.api.doctor.repository;

import com.api.doctor.model.DoctorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorModel, Integer> {

    Optional<DoctorModel> findByUserId(Integer userId);

    List<DoctorModel> findByStatus(String status);
}
