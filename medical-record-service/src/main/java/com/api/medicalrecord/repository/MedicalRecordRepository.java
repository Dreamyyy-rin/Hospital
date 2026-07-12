package com.api.medicalrecord.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.api.medicalrecord.model.MedicalRecordModel;

public interface MedicalRecordRepository extends MongoRepository<MedicalRecordModel, String> {

}
