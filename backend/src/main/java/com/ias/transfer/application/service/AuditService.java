package com.ias.transfer.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    public void auditTransferProcessed(
            UUID eventId,
            UUID transferId,
            UUID clientId,
            BigDecimal amount,
            String status,
            String traceId
    ) {
        MDC.put("traceId", traceId);
        MDC.put("eventId", eventId.toString());

        try {
            log.info("AUDITORIA: Procesando auditoria de transferencia | transferId={} | clientId={} | amount={} | status={}",
                    transferId, clientId, amount, status);

            saveAuditLog(transferId, clientId, amount, status);

            sendNotification(clientId, transferId, status);

            updateReadModel(transferId, status);

            log.info("AUDITORIA: Auditoria de transferencia completada correctamente | transferId={} | eventId={}",
                    transferId, eventId);

        } catch (Exception e) {
            log.error("AUDITORIA: Error procesando auditoria de transferencia | transferId={} | eventId={} | error={}",
                    transferId, eventId, e.getMessage(), e);
            throw e;
        } finally {
            MDC.remove("traceId");
            MDC.remove("eventId");
        }
    }

    private void saveAuditLog(UUID transferId, UUID clientId, BigDecimal amount, String status) {
        log.debug("Registro de auditoria guardado | transferId={} | clientId={} | amount={} | status={}",
                transferId, clientId, amount, status);
    }

    private void sendNotification(UUID clientId, UUID transferId, String status) {
        log.debug("Notificacion enviada | clientId={} | transferId={} | status={}",
                clientId, transferId, status);
    }

    private void updateReadModel(UUID transferId, String status) {
        log.debug("Modelo de lectura actualizado | transferId={} | status={}", transferId, status);
    }
}
