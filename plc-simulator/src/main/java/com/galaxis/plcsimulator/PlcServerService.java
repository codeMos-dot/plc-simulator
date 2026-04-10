package com.galaxis.plcsimulator;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.ByteBuffer;

@Service
public class PlcServerService {

    private Snap7Server server;

    // 使用 ByteBuffer 开辟堆外共享内存（64KB）
    public final ByteBuffer db10 = ByteBuffer.allocateDirect(65000); // 避开 65536 的溢出坑
    public final ByteBuffer db11 = ByteBuffer.allocateDirect(65000);

    @PostConstruct
    public void startServer() {
        server = new Snap7Server();

        // 把内存块交接给 C++ 底层
        server.registerAreaDB(10, db10);
        server.registerAreaDB(11, db11);

        // 启动监听
        int error = server.start();
        if (error == 0) {
            System.out.println("====== [手撸版 Snap7] S7服务端启动成功，正在监听 TCP 102 端口 ======");
        } else {
            System.err.println("====== S7服务端启动失败！错误码: " + error + " ======");
        }
    }

    @PreDestroy
    public void stopServer() {
        if (server != null) {
            server.stop();
        }
    }
}