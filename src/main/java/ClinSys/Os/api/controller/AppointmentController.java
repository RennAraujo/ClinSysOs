package ClinSys.Os.api.controller;

import ClinSys.Os.api.dto.AppointmentRequest;
import ClinSys.Os.api.dto.AppointmentResponse;
import ClinSys.Os.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Endpoints para gerenciamento de atendimentos")
@SecurityRequirement(name = "bearer-key")
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @Operation(summary = "Criar atendimento", description = "Cadastra um novo atendimento (Status inicial: SCHEDULED)")
    public ResponseEntity<AppointmentResponse> create(@RequestBody @Valid AppointmentRequest request) {
        var response = service.create(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    @Operation(summary = "Listar atendimentos", description = "Retorna todos os atendimentos cadastrados")
    public ResponseEntity<List<AppointmentResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    @Operation(summary = "Buscar atendimento por ID", description = "Retorna os detalhes de um atendimento específico")
    public ResponseEntity<AppointmentResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    @Operation(summary = "Atualizar atendimento", description = "Atualiza dados de um atendimento existente")
    public ResponseEntity<AppointmentResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid AppointmentRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover atendimento", description = "Remove um atendimento (apenas se não estiver finalizado)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
