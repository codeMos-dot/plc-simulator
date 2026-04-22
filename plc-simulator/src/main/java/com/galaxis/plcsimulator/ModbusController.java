package com.galaxis.plcsimulator;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/modbus")
@RequiredArgsConstructor
public class ModbusController {

    private final ModbusServerService serverService;
    private final ModbusClientService clientService;

    // --- Server Endpoints ---

    @GetMapping("/server/status")
    public ResponseEntity<Map<String, Object>> getServerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("running", serverService.isRunning());
        status.put("port", serverService.getCurrentPort());
        return ResponseEntity.ok(status);
    }

    @PostMapping("/server/start")
    public ResponseEntity<Map<String, String>> startServer(@RequestParam(defaultValue = "5020") int port) {
        boolean success = serverService.startServer(port);
        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("message", "Server started successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Failed to start server");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/server/stop")
    public ResponseEntity<Map<String, String>> stopServer() {
        boolean success = serverService.stopServer();
        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("message", "Server stopped successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Failed to stop server");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/server/registers")
    public ResponseEntity<int[]> getServerRegisters(@RequestParam(defaultValue = "0") int offset,
                                                    @RequestParam(defaultValue = "100") int count) {
        return ResponseEntity.ok(serverService.getRegisters(offset, count));
    }

    @PostMapping("/server/registers")
    public ResponseEntity<Map<String, String>> setServerRegisters(@RequestBody RegisterData request) {
        serverService.setRegisters(request.getOffset(), request.getValues());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Registers updated successfully");
        return ResponseEntity.ok(response);
    }

    // --- Client Endpoints ---

    @GetMapping("/client/status")
    public ResponseEntity<Map<String, Object>> getClientStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("connected", clientService.isConnected());
        status.put("ip", clientService.getCurrentIp());
        status.put("port", clientService.getCurrentPort());
        return ResponseEntity.ok(status);
    }

    @PostMapping("/client/connect")
    public ResponseEntity<Map<String, String>> connectClient(@RequestParam String ip, @RequestParam(defaultValue = "5020") int port) {
        boolean success = clientService.connect(ip, port);
        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("message", "Client connected successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Failed to connect client");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/client/disconnect")
    public ResponseEntity<Map<String, String>> disconnectClient() {
        boolean success = clientService.disconnect();
        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("message", "Client disconnected successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Failed to disconnect client");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/client/read")
    public ResponseEntity<?> readClientRegisters(@RequestParam(defaultValue = "1") int unitId,
                                                 @RequestParam(defaultValue = "0") int offset,
                                                 @RequestParam(defaultValue = "10") int count) {
        try {
            log.info("Client requesting read - UnitID: {}, Offset: {}, Count: {}", unitId, offset, count);
            int[] data = clientService.readHoldingRegisters(unitId, offset, count);
            log.info("Read successful, returned {} registers", data.length);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Read registers failed for UnitID: {}, Offset: {}, Count: {}. Error: {}", 
                      unitId, offset, count, e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to read: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/client/write")
    public ResponseEntity<Map<String, String>> writeClientRegisters(@RequestBody ClientWriteData request) {
        try {
            clientService.writeMultipleRegisters(request.getUnitId(), request.getOffset(), request.getValues());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Registers written successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to write: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Data
    public static class RegisterData {
        private int offset;
        private int[] values;
    }

    @Data
    public static class ClientWriteData {
        private int unitId = 1;
        private int offset;
        private int[] values;
    }
}
