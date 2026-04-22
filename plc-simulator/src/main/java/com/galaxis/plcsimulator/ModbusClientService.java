package com.galaxis.plcsimulator;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Slf4j
@Service
public class ModbusClientService {

    private ModbusTCPMaster master;
    private boolean isConnected = false;
    private String currentIp;
    private int currentPort;

    public synchronized boolean connect(String ip, int port) {
        if (isConnected) {
            log.warn("Client is already connected to {}:{}", currentIp, currentPort);
            return false;
        }

        try {
            this.currentIp = ip;
            this.currentPort = port;
            master = new ModbusTCPMaster(ip, port);
            master.connect();
            isConnected = true;
            log.info("Modbus client connected to {}:{}", ip, port);
            return true;
        } catch (Exception e) {
            log.error("Failed to connect Modbus client to {}:{}: {}", ip, port, e.getMessage(), e);
            return false;
        }
    }

    public synchronized boolean disconnect() {
        if (!isConnected) {
            log.warn("Client is not connected");
            return false;
        }

        try {
            if (master != null) {
                master.disconnect();
            }
            isConnected = false;
            log.info("Modbus client disconnected");
            return true;
        } catch (Exception e) {
            log.error("Failed to disconnect Modbus client: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getCurrentIp() {
        return currentIp;
    }

    public int getCurrentPort() {
        return currentPort;
    }

    public int[] readHoldingRegisters(int unitId, int offset, int count) {
        if (!isConnected) {
            throw new IllegalStateException("Client is not connected");
        }

        try {
            Register[] registers = master.readMultipleRegisters(unitId, offset, count);
            int[] data = new int[registers.length];
            for (int i = 0; i < registers.length; i++) {
                data[i] = registers[i].getValue();
            }
            return data;
        } catch (com.ghgande.j2mod.modbus.ModbusSlaveException e) {
            log.error("Modbus slave exception: Type={}, Error Code={}", e.getMessage(), e.getType());
            throw new RuntimeException("Modbus slave exception: " + e.getMessage() + " (Type " + e.getType() + ")", e);
        } catch (Exception e) {
            log.error("Failed to read holding registers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read registers: " + e.toString(), e);
        }
    }

    public void writeMultipleRegisters(int unitId, int offset, int[] values) {
        if (!isConnected) {
            throw new IllegalStateException("Client is not connected");
        }

        try {
            Register[] registers = new Register[values.length];
            for (int i = 0; i < values.length; i++) {
                registers[i] = new SimpleRegister(values[i]);
            }
            master.writeMultipleRegisters(unitId, offset, registers);
        } catch (com.ghgande.j2mod.modbus.ModbusSlaveException e) {
            log.error("Modbus slave exception during write: Type={}, Error Code={}", e.getMessage(), e.getType());
            throw new RuntimeException("Modbus slave exception: " + e.getMessage() + " (Type " + e.getType() + ")", e);
        } catch (Exception e) {
            log.error("Failed to write holding registers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to write registers: " + e.toString(), e);
        }
    }

    @PreDestroy
    public void cleanup() {
        disconnect();
    }
}
