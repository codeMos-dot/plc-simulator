package com.galaxis.plcsimulator;

import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;
import com.ghgande.j2mod.modbus.procimg.SimpleProcessImage;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.Register;

public class TestModbus {
    public static void main(String[] args) {
        try {
            System.out.println("Starting server...");
            SimpleProcessImage spi = new SimpleProcessImage(1);
            for (int i = 0; i < 100; i++) {
                spi.addRegister(new SimpleRegister(i));
            }
            ModbusSlave slave = ModbusSlaveFactory.createTCPSlave(5021, 5);
            slave.addProcessImage(1, spi);
            slave.open();
            System.out.println("Server started.");

            System.out.println("Connecting client...");
            ModbusTCPMaster master = new ModbusTCPMaster("127.0.0.1", 5021);
            master.connect();
            System.out.println("Client connected.");

            System.out.println("Reading registers...");
            Register[] regs = master.readMultipleRegisters(1, 0, 10);
            for (int i = 0; i < regs.length; i++) {
                System.out.print(regs[i].getValue() + " ");
            }
            System.out.println("\nRead successful.");
            
            master.disconnect();
            slave.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
