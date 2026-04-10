package com.galaxis.plcsimulator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class PlcSimulatorApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testPlcDbReadWrite() {
        com.galaxis.wes.trayline.common.moka7.S7Client client = new com.galaxis.wes.trayline.common.moka7.S7Client();
        client.SetConnectionType(com.galaxis.wes.trayline.common.moka7.S7.PG);

        try {
            // 1. 连接到本地测试服务端
            int connectResult = client.ConnectTo("127.0.0.1", 0, 0);
            if (connectResult != 0) throw new RuntimeException("连接本地 PLC 失败");

            // 2. 准备要写入的测试数据 (长度 10)
            int testOffset = 100;
            int testSize = 10;
            byte[] writeBuffer = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

            // 3. 写入 DB10
            int writeResult = client.WriteArea(com.galaxis.wes.trayline.common.moka7.S7.S7AreaDB, 10, testOffset, testSize, writeBuffer);
            if (writeResult != 0) throw new RuntimeException("写入 DB10 数据失败");

            // 4. 读出并且比对
            byte[] readBuffer = new byte[testSize];
            int readResult = client.ReadArea(com.galaxis.wes.trayline.common.moka7.S7.S7AreaDB, 10, testOffset, testSize, readBuffer);
            if (readResult != 0) throw new RuntimeException("读取 DB10 数据失败");
            
            if (!Arrays.equals(writeBuffer, readBuffer)) {
                throw new RuntimeException("读取出来的数据与写入的数据不一致！");
            }
            
            System.out.println("===== S7Client DB读写全流程测试通过！ =====");
        } finally {
            client.Disconnect();
        }
    }
}
