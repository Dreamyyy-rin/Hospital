package com.api.schedule.repository;

import com.api.schedule.model.ScheduleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleModel, Integer> {
    List<ScheduleModel> findByDoctorId(Integer doctorId);
}
