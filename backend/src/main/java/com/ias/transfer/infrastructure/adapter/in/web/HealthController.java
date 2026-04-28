package com.ias.transfer.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "Health", description = "Endpoint de salud del servicio")
@RestController
public class HealthController {

    @Operation(summary = "Health check", description = "Verifica que el servicio esta activo")
    @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    @GetMapping("/api/v1/health")
    public Mono<String> health() {
        return Mono.just("OK");
    }
}