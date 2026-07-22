package com.api.appointment.service;

import com.api.appointment.client.DoctorClient;
import com.api.appointment.client.MedicalRecordClient;
import com.api.appointment.client.PaymentClient;
import com.api.appointment.client.ScheduleClient;
import com.api.appointment.client.UserClient;
import com.api.appointment.dto.AppointmentRejectDTO;
import com.api.appointment.dto.AppointmentRequestDTO;
import com.api.appointment.dto.AppointmentResponseDTO;
import com.api.appointment.model.AppointmentModel;
import com.api.appointment.model.AppointmentModel.AppointmentStatus;
import com.api.appointment.repository.AppointmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final UserClient userClient;
  private final DoctorClient doctorClient;
  private final ScheduleClient scheduleClient;
  private final PaymentClient paymentClient;
  private final MedicalRecordClient medicalRecordClient;

  public AppointmentService(
    AppointmentRepository appointmentRepository,
    UserClient userClient,
    DoctorClient doctorClient,
    ScheduleClient scheduleClient,
    PaymentClient paymentClient,
    MedicalRecordClient medicalRecordClient
  ) {
    this.appointmentRepository = appointmentRepository;
    this.userClient = userClient;
    this.doctorClient = doctorClient;
    this.scheduleClient = scheduleClient;
    this.paymentClient = paymentClient;
    this.medicalRecordClient = medicalRecordClient;
  }

  public List<AppointmentResponseDTO> getAllAppointments() {
    return appointmentRepository
      .findAll()
      .stream()
      .map(this::toDTO)
      .collect(Collectors.toList());
  }

  public Optional<AppointmentResponseDTO> getAppointmentById(Integer id) {
    return appointmentRepository.findById(id).map(this::toDTO);
  }

  public ResponseEntity<AppointmentResponseDTO> createAppointment(
    AppointmentRequestDTO request
  ) {
    // Verify patient exists
    ResponseEntity<UserClient.UserResponse> patientResponse =
      userClient.getUserById(request.getPatientId());
    if (!patientResponse.getStatusCode().is2xxSuccessful()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    String patientName = patientResponse.getBody().getName();

    // Verify doctor exists
    ResponseEntity<DoctorClient.DoctorResponse> doctorResponse =
      doctorClient.getDoctorById(request.getDoctorId());
    if (!doctorResponse.getStatusCode().is2xxSuccessful()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    String doctorName = doctorResponse.getBody().getUserName();

    // Verify schedule exists
    ResponseEntity<ScheduleClient.ScheduleResponse> scheduleResponse =
      scheduleClient.getScheduleById(request.getScheduleId());
    if (!scheduleResponse.getStatusCode().is2xxSuccessful()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    ScheduleClient.ScheduleResponse schedule = scheduleResponse.getBody();

    // Create appointment
    AppointmentModel appointment = new AppointmentModel();
    appointment.setPatientId(request.getPatientId());
    appointment.setDoctorId(request.getDoctorId());
    appointment.setScheduleId(request.getScheduleId());
    appointment.setComplaint(request.getComplaint());
    appointment.setStatus(AppointmentStatus.REQUESTED);
    appointment.setAppointmentDate(LocalDateTime.now());
    appointment.setPrice(new java.math.BigDecimal("150000")); // Default price
    appointment.setCreatedAt(LocalDateTime.now());
    appointment.setUpdatedAt(LocalDateTime.now());

    AppointmentModel saved = appointmentRepository.save(appointment);
    AppointmentResponseDTO dto = toDTO(saved);
    dto.setPatientName(patientName);
    dto.setDoctorName(doctorName);
    dto.setAppointmentDateTime(
      schedule.getDate() + " " + schedule.getStartTime()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  public List<AppointmentResponseDTO> getPendingAppointments() {
    return appointmentRepository
      .findByStatus(AppointmentStatus.REQUESTED)
      .stream()
      .map(this::toDTO)
      .collect(Collectors.toList());
  }

  public Optional<AppointmentResponseDTO> approveAppointment(Integer id) {
    return appointmentRepository
      .findById(id)
      .filter(a -> a.getStatus() == AppointmentStatus.REQUESTED)
      .map(appointment -> {
        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setUpdatedAt(LocalDateTime.now());
        AppointmentModel saved = appointmentRepository.save(appointment);
        return toDTO(saved);
      });
  }

  public Optional<AppointmentResponseDTO> rejectAppointment(
    Integer id,
    AppointmentRejectDTO request
  ) {
    return appointmentRepository
      .findById(id)
      .filter(a -> a.getStatus() == AppointmentStatus.REQUESTED)
      .map(appointment -> {
        appointment.setStatus(AppointmentStatus.REJECTED);
        appointment.setRejectionReason(request.getReason());
        appointment.setUpdatedAt(LocalDateTime.now());
        AppointmentModel saved = appointmentRepository.save(appointment);
        return toDTO(saved);
      });
  }

  public AppointmentResponseDTO toDTO(AppointmentModel appointment) {
    AppointmentResponseDTO.AppointmentResponseDTOBuilder builder =
      AppointmentResponseDTO.builder()
        .id(appointment.getId())
        .patientId(appointment.getPatientId())
        .doctorId(appointment.getDoctorId())
        .scheduleId(appointment.getScheduleId())
        .complaint(appointment.getComplaint())
        .status(appointment.getStatus().name())
        .rejectionReason(appointment.getRejectionReason())
        .price(appointment.getPrice())
        .paymentId(appointment.getPaymentId())
        .medicalRecordId(appointment.getMedicalRecordId())
        .createdAt(appointment.getCreatedAt())
        .updatedAt(appointment.getUpdatedAt());

    // Enrich with patient name
    try {
      ResponseEntity<UserClient.UserResponse> patient = userClient.getUserById(
        appointment.getPatientId()
      );
      if (
        patient.getStatusCode().is2xxSuccessful() && patient.getBody() != null
      ) {
        builder.patientName(patient.getBody().getName());
      }
    } catch (Exception e) {
      // Ignore - name will be null
    }

    // Enrich with doctor name
    try {
      ResponseEntity<DoctorClient.DoctorResponse> doctor =
        doctorClient.getDoctorById(appointment.getDoctorId());
      if (
        doctor.getStatusCode().is2xxSuccessful() && doctor.getBody() != null
      ) {
        builder.doctorName(doctor.getBody().getUserName());
      }
    } catch (Exception e) {
      // Ignore - name will be null
    }

    // Enrich with schedule datetime
    try {
      ResponseEntity<ScheduleClient.ScheduleResponse> schedule =
        scheduleClient.getScheduleById(appointment.getScheduleId());
      if (
        schedule.getStatusCode().is2xxSuccessful() && schedule.getBody() != null
      ) {
        ScheduleClient.ScheduleResponse s = schedule.getBody();
        builder.appointmentDateTime(s.getDate() + " " + s.getStartTime());
      }
    } catch (Exception e) {
      // Ignore - datetime will be null
    }

    // Enrich with payment status
    if (appointment.getPaymentId() != null) {
      try {
        ResponseEntity<PaymentClient.PaymentResponse> payment =
          paymentClient.getPaymentById(appointment.getPaymentId());
        if (
          payment.getStatusCode().is2xxSuccessful() && payment.getBody() != null
        ) {
          builder.paymentStatus(payment.getBody().getStatus());
        }
      } catch (Exception e) {
        // Ignore - status will be null
      }
    }

    return builder.build();
  }
}
