package ClinSys.Os.api.dto;

import ClinSys.Os.domain.model.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequest {

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    @NotBlank(message = "Specialty is required")
    private String specialty;

    @NotNull(message = "Date and time is required")
    @Future(message = "Date must be in the future")
    private LocalDateTime dateTime;

    private AppointmentStatus status; // Optional for update
}
