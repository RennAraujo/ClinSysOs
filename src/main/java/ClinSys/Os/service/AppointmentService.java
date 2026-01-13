package ClinSys.Os.service;

import ClinSys.Os.api.dto.AppointmentRequest;
import ClinSys.Os.api.dto.AppointmentResponse;
import ClinSys.Os.domain.model.Appointment;
import ClinSys.Os.domain.model.AppointmentStatus;
import ClinSys.Os.domain.repository.AppointmentRepository;
import ClinSys.Os.service.exception.BusinessException;
import ClinSys.Os.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;

    public AppointmentResponse create(AppointmentRequest request) {
        if (request.getDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot schedule appointment in the past");
        }

        var appointment = Appointment.builder()
                .patientName(request.getPatientName())
                .doctorName(request.getDoctorName())
                .specialty(request.getSpecialty())
                .dateTime(request.getDateTime())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        var saved = repository.save(appointment);
        return mapToResponse(saved);
    }

    public List<AppointmentResponse> findAll() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    public AppointmentResponse update(UUID id, AppointmentRequest request) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Cannot update a completed appointment");
        }

        if (request.getDateTime() != null && request.getDateTime().isBefore(LocalDateTime.now())) {
             throw new BusinessException("Cannot update appointment to a past date");
        }

        // Status transition validation logic could go here
        // For simplicity, we allow updates if not COMPLETED, but we should check target status
        if (request.getStatus() != null) {
             if (appointment.getStatus() == AppointmentStatus.CANCELED && request.getStatus() != AppointmentStatus.CANCELED) {
                 throw new BusinessException("Cannot reactivate a canceled appointment");
             }
             // More rules as per requirements?
        }

        appointment.setPatientName(request.getPatientName());
        appointment.setDoctorName(request.getDoctorName());
        appointment.setSpecialty(request.getSpecialty());
        appointment.setDateTime(request.getDateTime());
        
        if (request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
        }

        var updated = repository.save(appointment);
        return mapToResponse(updated);
    }

    public void delete(UUID id) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Cannot delete a completed appointment");
        }

        repository.delete(appointment);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientName(appointment.getPatientName())
                .doctorName(appointment.getDoctorName())
                .specialty(appointment.getSpecialty())
                .dateTime(appointment.getDateTime())
                .status(appointment.getStatus())
                .build();
    }
}
