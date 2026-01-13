package ClinSys.Os.service;

import ClinSys.Os.api.dto.AppointmentRequest;
import ClinSys.Os.domain.model.Appointment;
import ClinSys.Os.domain.model.AppointmentStatus;
import ClinSys.Os.domain.repository.AppointmentRepository;
import ClinSys.Os.service.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @InjectMocks
    private AppointmentService service;

    @Test
    void shouldCreateAppointmentSuccessfully() {
        var request = AppointmentRequest.builder()
                .patientName("John Doe")
                .doctorName("Dr. House")
                .specialty("Cardiology")
                .dateTime(LocalDateTime.now().plusDays(1))
                .build();

        var appointment = Appointment.builder()
                .id(UUID.randomUUID())
                .patientName(request.getPatientName())
                .doctorName(request.getDoctorName())
                .specialty(request.getSpecialty())
                .dateTime(request.getDateTime())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        when(repository.save(any(Appointment.class))).thenReturn(appointment);

        var response = service.create(request);

        assertNotNull(response);
        assertEquals(AppointmentStatus.SCHEDULED, response.getStatus());
        verify(repository).save(any(Appointment.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingPastAppointment() {
        var request = AppointmentRequest.builder()
                .dateTime(LocalDateTime.now().minusDays(1))
                .build();

        assertThrows(BusinessException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingCompletedAppointment() {
        var id = UUID.randomUUID();
        var appointment = Appointment.builder()
                .id(id)
                .status(AppointmentStatus.COMPLETED)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(appointment));

        var request = AppointmentRequest.builder().build();

        assertThrows(BusinessException.class, () -> service.update(id, request));
    }
}
