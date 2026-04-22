package com.galaxis.plcsimulator;

import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;
import com.ghgande.j2mod.modbus.procimg.SimpleProcessImage;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.procimg.SimpleDigitalIn;
import com.ghgande.j2mod.modbus.procimg.SimpleDigitalOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Service
public class ModbusServerService {

    private ModbusSlave slave;
    // Set Unit ID 1 explicitly
    private final SimpleProcessImage processImage = new SimpleProcessImage(1);
    private boolean isRunning = false;
    private int currentPort = 5020;
    private boolean isInitialized = false;

    private synchronized void ensureInitialized() {
        if (!isInitialized) {
            log.info("Pre-populating Modbus process image with 1000 registers...");
            for (int i = 0; i < 1000; i++) {
                processImage.addRegister(new SimpleRegister(0));
                processImage.addInputRegister(new SimpleRegister(0));
                processImage.addDigitalOut(new SimpleDigitalOut(false));
                processImage.addDigitalIn(new SimpleDigitalIn(false));
            }
            log.info("Modbus process image initialized successfully.");
            isInitialized = true;
        }
    }

    public synchronized boolean startServer(int port) {
        if (isRunning) {
            stopServer();
        }

        try {
            ensureInitialized();
            this.currentPort = port;
            slave = ModbusSlaveFactory.createTCPSlave(port, 5);
            
            // Map the process image to Unit ID 1 (Standard)
            slave.addProcessImage(1, processImage);
            
            slave.open();
            isRunning = true;
            log.info("Modbus server started on port {} (Unit ID 1 mapped, Holding Registers: {}, Input Registers: {}, Coils: {}, Discrete Inputs: {})", 
                     currentPort, processImage.getRegisterCount(), processImage.getInputRegisterCount(), 
                     processImage.getDigitalOutCount(), processImage.getDigitalInCount());
            return true;
        } catch (Exception e) {
            log.error("Failed to start Modbus server on port {}: {}", port, e.getMessage());
            return false;
        }
    }

    public synchronized boolean stopServer() {
        if (slave != null) {
            try {
                slave.close();
            } catch (Exception e) {
                log.error("Error closing Modbus slave: {}", e.getMessage());
            }
            slave = null;
        }
        isRunning = false;
        log.info("Modbus server stopped.");
        return true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getCurrentPort() {
        return currentPort;
    }

    public int[] getRegisters(int offset, int count) {
        int[] data = new int[count];
        int registerCount = processImage.getRegisterCount();
        for (int i = 0; i < count; i++) {
            int addr = offset + i;
            if (addr >= 0 && addr < registerCount) {
                data[i] = processImage.getRegister(addr).getValue();
            } else {
                data[i] = 0;
            }
        }
        return data;
    }

    public void setRegisters(int offset, int[] data) {
        int registerCount = processImage.getRegisterCount();
        for (int i = 0; i < data.length; i++) {
            int addr = offset + i;
            if (addr >= 0 && addr < registerCount) {
                try {
                    processImage.getRegister(addr).setValue(data[i]);
                } catch (Exception e) {
                    log.error("Failed to set register {}: {}", addr, e.getMessage());
                }
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        stopServer();
    }
}
