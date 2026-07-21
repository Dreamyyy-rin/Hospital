package com.api.appointment.repository;

import com.api.appointment.model.AppointmentModel;
import com.api.appointment.model.AppointmentModel.AppointmentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository
  extends JpaRepository<AppointmentModel, Integer>
{
  List<AppointmentModel> findByPatientId(Integer patientId);
  List<AppointmentModel> findByDoctorId(Integer doctorId);
  List<AppointmentModel> findByStatus(AppointmentStatus status);
  List<AppointmentModel> findByPatientIdAndStatus(
    Integer patientId,
    AppointmentStatus status
  );
  List<AppointmentModel> findByDoctorIdAndStatus(
    Integer doctorId,
    AppointmentStatus status
  );
  List<AppointmentModel> findByScheduleId(Integer scheduleId);

  boolean existsByScheduleIdAndStatusIn(
    Integer scheduleId,
    List<AppointmentStatus> statuses
  );
}
