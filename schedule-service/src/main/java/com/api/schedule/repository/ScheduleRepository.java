package com.api.schedule.repository;

import com.api.schedule.model.ScheduleModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository
  extends JpaRepository<ScheduleModel, Integer>
{
  List<ScheduleModel> findByDoctorId(Integer doctorId);
  List<ScheduleModel> findByStatus(String status);
}
