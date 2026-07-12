package com.api.medicalrecord.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "medical_records")
public class MedicalRecordModel {
    @Id
    private String id;

    private String appointmentId;
    private Integer doctorId;
    private Integer patientId;
    private List<String> complaints;
    private String diagnosis;
    private String bloodPressure;
    private Double temperature;
    private List<MedicationModel> medications;
    private String doctorNotes;

}
