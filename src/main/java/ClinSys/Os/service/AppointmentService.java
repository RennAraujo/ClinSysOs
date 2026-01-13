package ClinSys.Os.service;

import ClinSys.Os.api.dto.AppointmentRequest;
import ClinSys.Os.api.dto.AppointmentResponse;
import ClinSys.Os.domain.model.Appointment;
import ClinSys.Os.domain.model.AppointmentStatus;
import ClinSys.Os.domain.repository.AppointmentRepository;
import ClinSys.Os.service.exception.BusinessException;
import ClinSys.Os.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
/**
 * Service class for managing appointments.
 * Handles business logic for creating, retrieving, updating, and deleting appointments.
 */
public class AppointmentService {

    private final AppointmentRepository repository;

    /**
     * Checks if the current authenticated user has a specific role.
     *
     * @param role The role to check (without "ROLE_" prefix).
     * @return true if the user has the role, false otherwise.
     */
    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Creates a new appointment.
     *
     * @param request The appointment creation request.
     * @return The created appointment response.
     * @throws BusinessException if the appointment date is in the past.
     */
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

    /**
     * Retrieves all appointments.
     *
     * @return A list of appointment responses.
     */
    public List<AppointmentResponse> findAll() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an appointment by its ID.
     *
     * @param id The UUID of the appointment.
     * @return The appointment response.
     * @throws ResourceNotFoundException if the appointment is not found.
     */
    public AppointmentResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    /**
     * Updates an existing appointment based on role-specific rules.
     *
     * @param id The UUID of the appointment to update.
     * @param request The update request data.
     * @return The updated appointment response.
     * @throws ResourceNotFoundException if the appointment is not found.
     * @throws BusinessException if business rules are violated.
     */
    public AppointmentResponse update(UUID id, AppointmentRequest request) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        boolean isAdmin = hasRole("ADMIN");
        boolean isDoctor = hasRole("DOCTOR");
        boolean isReceptionist = hasRole("RECEPTIONIST");

        if (isReceptionist) {
            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                throw new BusinessException("Receptionists cannot update completed appointments");
            }
            if (request.getStatus() == null || request.getStatus() != AppointmentStatus.CANCELED) {
                throw new BusinessException("Receptionists can only cancel appointments");
            }
            appointment.setStatus(AppointmentStatus.CANCELED);
            return mapToResponse(repository.save(appointment));
        }

        if (isDoctor) {
            if (request.getStatus() == AppointmentStatus.CANCELED) {
                throw new BusinessException("Doctors cannot cancel appointments");
            }
            if (request.getStatus() != null) {
                 if (appointment.getStatus() == AppointmentStatus.CANCELED && request.getStatus() != AppointmentStatus.CANCELED) {
                     throw new BusinessException("Cannot reactivate a canceled appointment");
                 }
                appointment.setStatus(request.getStatus());
            }
            return mapToResponse(repository.save(appointment));
        }

        if (request.getDateTime() != null && request.getDateTime().isBefore(LocalDateTime.now())) {
             throw new BusinessException("Cannot update appointment to a past date");
        }

        if (request.getStatus() != null) {
             if (appointment.getStatus() == AppointmentStatus.CANCELED && request.getStatus() != AppointmentStatus.CANCELED) {
                 throw new BusinessException("Cannot reactivate a canceled appointment");
             }
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

    /**
     * Deletes an appointment by its ID.
     *
     * @param id The UUID of the appointment to delete.
     * @throws ResourceNotFoundException if the appointment is not found.
     */
    public void delete(UUID id) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
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
