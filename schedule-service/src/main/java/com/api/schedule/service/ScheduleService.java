package com.api.schedule.service;

import com.api.schedule.model.ScheduleModel;
import com.api.schedule.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<ScheduleModel> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<ScheduleModel> getScheduleById(Integer id) {
        return scheduleRepository.findById(id);
    }

    public List<ScheduleModel> getSchedulesByDoctorId(Integer doctorId) {
        return scheduleRepository.findByDoctorId(doctorId);
    }

    public ScheduleModel createSchedule(ScheduleModel schedule) {
        return scheduleRepository.save(schedule);
    }

    public ScheduleModel updateSchedule(Integer id, ScheduleModel updatedSchedule) {
        return scheduleRepository.findById(id).map(schedule -> {
            schedule.setDoctorId(updatedSchedule.getDoctorId());
            schedule.setDate(updatedSchedule.getDate());
            schedule.setStartTime(updatedSchedule.getStartTime());
            schedule.setEndTime(updatedSchedule.getEndTime());
            schedule.setRoom(updatedSchedule.getRoom());
            schedule.setStatus(updatedSchedule.getStatus());
            return scheduleRepository.save(schedule);
        }).orElseThrow(() -> new RuntimeException("Schedule not found with id " + id));
    }

    public void deleteSchedule(Integer id) {
        scheduleRepository.deleteById(id);
    }
}
