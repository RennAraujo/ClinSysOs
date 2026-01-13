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
public class AppointmentService {

    private final AppointmentRepository repository;

    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

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

        boolean isAdmin = hasRole("ADMIN");
        boolean isDoctor = hasRole("DOCTOR");
        boolean isReceptionist = hasRole("RECEPTIONIST");

        // Rule: RECEPTIONIST cannot update completed appointments
        if (isReceptionist && appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Receptionists cannot update completed appointments");
        }

        // Rule: DOCTOR can only update status
        if (isDoctor) {
            if (request.getStatus() != null) {
                // Validate status transition (optional but good practice)
                 if (appointment.getStatus() == AppointmentStatus.CANCELED && request.getStatus() != AppointmentStatus.CANCELED) {
                     throw new BusinessException("Cannot reactivate a canceled appointment");
                 }
                appointment.setStatus(request.getStatus());
            }
            // Ignore other fields changes for DOCTOR
            return mapToResponse(repository.save(appointment));
        }

        // For ADMIN and RECEPTIONIST (on non-completed), allow full update
        
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

    public void delete(UUID id) {
        var appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Rule: ADMIN has full access, so we remove the block for COMPLETED if it's ADMIN
        // But if logic was intended for everyone, we keep it. Requirement says "ADMIN Acesso total".
        // Assuming "Acesso total" overrides "Cannot delete completed".
        
        // However, if we want to be safe, we might keep it. But user said "Acesso total".
        // Let's remove the restriction for ADMIN.
        // Since only ADMIN can call delete (enforced by Controller), we can remove the check entirely or check if user is admin (redundant).
        // Wait, Controller has @PreAuthorize("hasRole('ADMIN')"). So ONLY ADMIN reaches here.
        // So we should remove the "Cannot delete a completed appointment" check to fulfill "Acesso total".
        
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
