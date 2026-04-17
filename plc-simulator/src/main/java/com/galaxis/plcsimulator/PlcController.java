package com.galaxis.plcsimulator;

import org.springframework.web.bind.annotation.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/plc")
public class PlcController {



    @org.springframework.beans.factory.annotation.Autowired
    private PlcServerService plcServerService;

    @GetMapping("/unit/{index}")
    public UnitDataPair getUnitData(@PathVariable int index) {
        if (index < 1 || 100 + (index - 1) * 100 + 100 > 65000) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        
        int offset = 100 + (index - 1) * 100; // 绝对起始地址
        int size = 100;

        UnitDataPair pair = new UnitDataPair();
        pair.setUnitIndex(index);

        byte[] buffer10 = new byte[size];
        byte[] buffer11 = new byte[size];

        // 避免通过网络环回创建 tcp client，直接从 JVM 内存读取
        for (int i = 0; i < size; i++) {
            buffer10[i] = plcServerService.db10.get(offset + i);
            buffer11[i] = plcServerService.db11.get(offset + i);
        }

        pair.setDb10(UnitDataConverter.parseToUnitData(buffer10)); // 读到后转成对象
        pair.setDb11(UnitDataConverter.parseToUnitData(buffer11)); // 读到后转成对象

        return pair;
    }

    @PostMapping("/unit")
    public String saveUnitData(@RequestBody UnitDataPair pair) {
        if (pair == null) {
            return "Error: Request body is null";
        }
        
        int index = pair.getUnitIndex();
        if (index < 1 || 100 + (index - 1) * 100 + 100 > 65000) {
            return "Error: Index out of bounds";
        }
        
        int offset = 100 + (index - 1) * 100;
        int size = 100;

        // 直接写入到底层 Direct Buffers
        if (pair.getDb10() != null) {
            byte[] buf10 = UnitDataConverter.toByteArray(pair.getDb10()); // 先转成 100 字节数组
            for (int i = 0; i < size; i++) {
                plcServerService.db10.put(offset + i, buf10[i]);
            }
        }
        
        // 写 DB11
        if (pair.getDb11() != null) {
            byte[] buf11 = UnitDataConverter.toByteArray(pair.getDb11()); // 先转成 100 字节数组
            for (int i = 0; i < size; i++) {
                plcServerService.db11.put(offset + i, buf11[i]);
            }
        }
        
        return "Success";
    }
}