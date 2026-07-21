package com.api.schedule.service;

import com.api.schedule.client.DoctorClient;
import com.api.schedule.dto.ScheduleRequestDTO;
import com.api.schedule.dto.ScheduleResponseDTO;
import com.api.schedule.model.ScheduleModel;
import com.api.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorClient doctorClient;

    public List<ScheduleModel> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<ScheduleModel> getScheduleById(Integer id) {
        return scheduleRepository.findById(id);
    }

    public List<ScheduleModel> getSchedulesByDoctorId(Integer doctorId) {
        return scheduleRepository.findByDoctorId(doctorId);
    }

    public List<ScheduleModel> getAvailableSchedules() {
        return scheduleRepository.findByStatus("AVAILABLE");
    }

    public ScheduleResponseDTO createSchedule(ScheduleRequestDTO request) {
        ScheduleModel schedule = new ScheduleModel();
        schedule.setDoctorId(request.getDoctorId());
        schedule.setDate(request.getDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setRoom(request.getRoom());
        schedule.setStatus(request.getStatus() != null ? request.getStatus() : "AVAILABLE");

        ScheduleModel saved = scheduleRepository.save(schedule);
        return toDTO(saved);
    }

    public ScheduleResponseDTO updateSchedule(Integer id, ScheduleRequestDTO request) {
        ScheduleModel schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id " + id));

        if (request.getDoctorId() != null) {
            schedule.setDoctorId(request.getDoctorId());
        }
        if (request.getDate() != null) {
            schedule.setDate(request.getDate());
        }
        if (request.getStartTime() != null) {
            schedule.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            schedule.setEndTime(request.getEndTime());
        }
        if (request.getRoom() != null) {
            schedule.setRoom(request.getRoom());
        }
        if (request.getStatus() != null) {
            schedule.setStatus(request.getStatus());
        }

        ScheduleModel updated = scheduleRepository.save(schedule);
        return toDTO(updated);
    }

    public ScheduleResponseDTO updateStatus(Integer id, String status) {
        ScheduleModel schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id " + id));

        schedule.setStatus(status);
        ScheduleModel updated = scheduleRepository.save(schedule);
        return toDTO(updated);
    }

    public void deleteSchedule(Integer id) {
        scheduleRepository.deleteById(id);
    }

    public ScheduleResponseDTO toDTO(ScheduleModel schedule) {
        ScheduleResponseDTO dto = ScheduleResponseDTO.builder()
                .id(schedule.getId())
                .doctorId(schedule.getDoctorId())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .room(schedule.getRoom())
                .status(schedule.getStatus())
                .build();

        // Fetch doctor info via Feign
        if (schedule.getDoctorId() != null) {
            try {
                ResponseEntity<DoctorClient.DoctorResponse> doctorResponse = doctorClient.getDoctorById(schedule.getDoctorId());
                if (doctorResponse.getBody() != null) {
                    dto.setDoctorName(doctorResponse.getBody().getUserName());
                    dto.setDoctorSpecialization(doctorResponse.getBody().getSpecialization());
                }
            } catch (Exception e) {
                // If doctor service is unavailable, just skip doctor info
            }
        }

        return dto;
    }
}
