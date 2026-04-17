package com.galaxis.plcsimulator;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
public class PlcClientController {

    private final com.galaxis.wes.trayline.common.moka7.S7Client s7Client = new com.galaxis.wes.trayline.common.moka7.S7Client();
    private boolean isConnected = false;

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    // 工具方法：字节数组转十六进制字符串 (带空格)
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        StringBuilder hexChars = new StringBuilder(bytes.length * 3);
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars.append(HEX_ARRAY[v >>> 4]);
            hexChars.append(HEX_ARRAY[v & 0x0F]);
            hexChars.append(" ");
        }
        return hexChars.toString().trim();
    }

    // 工具方法：十六进制字符串转字节数组
    public static byte[] hexToBytes(String s) {
        s = s.replaceAll("\\s+", "").toUpperCase();
        int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even number of characters");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @PostMapping("/connect")
    public synchronized String connectPlc(@RequestBody Map<String, Object> req) {
        String ip = (String) req.getOrDefault("ip", "127.0.0.1");
        int rack = Integer.parseInt(req.getOrDefault("rack", "0").toString());
        int slot = Integer.parseInt(req.getOrDefault("slot", "0").toString());

        if (isConnected) {
            s7Client.Disconnect();
            isConnected = false;
        }

        s7Client.SetConnectionType(com.galaxis.wes.trayline.common.moka7.S7.PG);
        int res = s7Client.ConnectTo(ip, rack, slot);
        
        if (res == 0) {
            isConnected = true;
            return "SUCCESS";
        } else {
            return "ERROR: Connect Failed (Error Code: " + res + ")";
        }
    }

    @PostMapping("/disconnect")
    public synchronized String disconnectPlc() {
        if (isConnected) {
            s7Client.Disconnect();
            isConnected = false;
        }
        return "SUCCESS";
    }

    @PostMapping("/read")
    public synchronized String readPlc(@RequestBody Map<String, Object> req) {
        if (!isConnected) return "ERROR: NOT_CONNECTED";
        
        int dbNum = Integer.parseInt(req.getOrDefault("dbNum", "10").toString());
        int offset = Integer.parseInt(req.getOrDefault("offset", "0").toString());
        int size = Integer.parseInt(req.getOrDefault("size", "10").toString());

        byte[] buffer = new byte[size];
        int readRes = s7Client.ReadArea(com.galaxis.wes.trayline.common.moka7.S7.S7AreaDB, dbNum, offset, size, buffer);
        if (readRes != 0) {
            return "ERROR: Read Failed (" + readRes + ")";
        }
        return bytesToHex(buffer);
    }

    @PostMapping("/write")
    public synchronized String writePlc(@RequestBody Map<String, Object> req) {
        if (!isConnected) return "ERROR: NOT_CONNECTED";
        
        int dbNum = Integer.parseInt(req.getOrDefault("dbNum", "10").toString());
        int offset = Integer.parseInt(req.getOrDefault("offset", "0").toString());
        String hexData = (String) req.getOrDefault("data", "");

        byte[] buffer;
        try {
            buffer = hexToBytes(hexData);
        } catch (Exception e) {
            return "ERROR: Invalid Hex Format";
        }

        if (buffer.length == 0) {
            return "ERROR: No data to write";
        }

        int writeRes = s7Client.WriteArea(com.galaxis.wes.trayline.common.moka7.S7.S7AreaDB, dbNum, offset, buffer.length, buffer);
        if (writeRes != 0) {
            return "ERROR: Write Failed (" + writeRes + ")";
        }
        return "SUCCESS";
    }
    @PostMapping("/readUnit")
    public synchronized UnitData readUnitDataPlc(@RequestBody Map<String, Object> req) {
        if (!isConnected) return null;
        
        int dbNum = Integer.parseInt(req.getOrDefault("dbNum", "10").toString());
        int offset = Integer.parseInt(req.getOrDefault("offset", "0").toString());
        
        byte[] buffer = new byte[100];
        int readRes = s7Client.ReadArea(com.galaxis.wes.trayline.common.moka7.S7.S7AreaDB, dbNum, offset, 100, buffer);
        if (readRes != 0) {
            return null;
        }
        return UnitDataConverter.parseToUnitData(buffer);
    }

    @PostMapping("/writeUnit")
    public synchronized String writeUnitDataPlc(@RequestBody Map<String, Object> req) {
        if (!isConnected) return "ERROR: NOT_CONNECTED";
        
        int dbNum = Integer.parseInt(req.getOrDefault("dbNum", "10").toString());
        int offset = Integer.parseInt(req.getOrDefault("offset", "0").toString());
        
        Map<String, Object> struct = (Map<String, Object>) req.get("struct");
        if (struct == null) return "ERROR: No struct provided";

        // 第一步：先读取 PLC 现有的完整块数据 (Read)
        byte[] currentBuffer = new byte[100];
        int readRes = s7Client.ReadArea(com.galaxis.wes.trayline.common.moka7.S7.S7AreaDB, dbNum, offset, 100, currentBuffer);
        if (readRes != 0) {
            return "ERROR: Pre-read Failed (" + readRes + ")";
        }

        UnitData data = UnitDataConverter.parseToUnitData(currentBuffer);

        // 第二步：根据前端提交的值，进行覆盖 (Modify) 只有在值不是 null 或 空时覆盖
        if (hasValue(struct, "deviceNr")) data.setDeviceNr(Integer.parseInt(struct.get("deviceNr").toString()));
        if (hasValue(struct, "occupied")) data.setOccupied(Integer.parseInt(struct.get("occupied").toString()));
        if (hasValue(struct, "rsn")) data.setRsn(Integer.parseInt(struct.get("rsn").toString()));
        if (hasValue(struct, "rc")) data.setRc(Integer.parseInt(struct.get("rc").toString()));
        if (hasValue(struct, "rq")) data.setRq(Integer.parseInt(struct.get("rq").toString()));
        if (hasValue(struct, "spare1")) data.setSpare1(Integer.parseInt(struct.get("spare1").toString()));
        if (hasValue(struct, "taskId")) data.setTaskId(Integer.parseInt(struct.get("taskId").toString()));
        if (hasValue(struct, "destNr")) data.setDestNr(Integer.parseInt(struct.get("destNr").toString()));
        if (hasValue(struct, "countour")) data.setCountour(Integer.parseInt(struct.get("countour").toString()));
        if (hasValue(struct, "weight")) data.setWeight(Integer.parseInt(struct.get("weight").toString()));
        if (hasValue(struct, "barcode")) data.setBarcode(struct.get("barcode").toString());
        if (hasValue(struct, "barcodeSpare")) data.setBarcodeSpare(struct.get("barcodeSpare").toString());
        if (hasValue(struct, "type")) data.setType(Integer.parseInt(struct.get("type").toString()));
        if (hasValue(struct, "completionMark")) data.setCompletionMark(Integer.parseInt(struct.get("completionMark").toString()));
        if (hasValue(struct, "pcCmdSnAck")) data.setPcCmdSnAck(Integer.parseInt(struct.get("pcCmdSnAck").toString()));
        if (hasValue(struct, "pcCmdCrlAck")) data.setPcCmdCrlAck(Integer.parseInt(struct.get("pcCmdCrlAck").toString()));
        if (hasValue(struct, "dataValid")) data.setDataValid(Integer.parseInt(struct.get("dataValid").toString()));

        // 第三步：转成最终字节数组，写回 PLC (Write)
        byte[] writeBuffer = UnitDataConverter.toByteArray(data);

        int writeRes = s7Client.WriteArea(com.galaxis.wes.trayline.common.moka7.S7.S7AreaDB, dbNum, offset, writeBuffer.length, writeBuffer);
        if (writeRes != 0) {
            return "ERROR: Write Failed (" + writeRes + ")";
        }
        return "SUCCESS";
    }

    private boolean hasValue(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null && !val.toString().trim().isEmpty();
    }
}
