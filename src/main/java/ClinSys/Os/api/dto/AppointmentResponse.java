package ClinSys.Os.api.dto;

import ClinSys.Os.domain.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {
    private UUID id;
    private String patientName;
    private String doctorName;
    private String specialty;
    private LocalDateTime dateTime;
    private AppointmentStatus status;
}
