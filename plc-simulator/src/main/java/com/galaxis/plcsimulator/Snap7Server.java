package com.galaxis.plcsimulator;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.nio.ByteBuffer;

public class Snap7Server {

    // 强制 JNA 寻找 snap7.dll
    static {
        // 修复：防止 DLL 劫持，优先从应用受控的 lib 文件夹下加载
        java.io.File libDir = new java.io.File(System.getProperty("user.dir"), "lib");
        if (libDir.exists() && libDir.isDirectory()) {
            System.setProperty("jna.library.path", libDir.getAbsolutePath());
        } else {
            System.setProperty("jna.library.path", System.getProperty("user.dir"));
        }
    }

    // 定义 JNA 接口，精准映射底层 C++ 的四个关键函数
    public interface S7Lib extends Library {
        S7Lib INSTANCE = Native.load("snap7", S7Lib.class);

        Pointer Srv_Create();

        int Srv_RegisterArea(Pointer server, int areaCode, int index, Pointer pUsrData, int size);

        int Srv_Start(Pointer server);

        int Srv_Stop(Pointer server);
    }

    private Pointer serverPtr;
    //    public static final int srvAreaDB = 0x84;
    public static final int srvAreaDB = 5;

    public Snap7Server() {
        // 创建底层 C++ 服务端对象
        serverPtr = S7Lib.INSTANCE.Srv_Create();
    }

    // 将 Java 的堆外内存块注册给 C++，这样 PLC 读写时 Java 瞬间就能感知到
    public void registerAreaDB(int dbNumber, ByteBuffer buffer) {
        Pointer pData = Native.getDirectBufferPointer(buffer);
        // 获取注册的返回值
        int err = S7Lib.INSTANCE.Srv_RegisterArea(serverPtr, srvAreaDB, dbNumber, pData, buffer.capacity());
        if(err != 0) {
            System.err.println("DB" + dbNumber + " 底层内存注册失败！错误码：" + err);
        } else {
            System.out.println("DB" + dbNumber + " 底层内存 65536 字节注册成功！");
        }
    }

    public int start() {
        // 启动并监听 0.0.0.0:102 端口
        return S7Lib.INSTANCE.Srv_Start(serverPtr);
    }

    public void stop() {
        if (serverPtr != null) {
            S7Lib.INSTANCE.Srv_Stop(serverPtr);
        }
    }
}
