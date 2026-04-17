package com.galaxis.plcsimulator;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UnitDataConverter {

    public static int getInt16(ByteBuffer db, int offset) {
        return db.getShort(offset);
    }

    public static void setInt16(ByteBuffer db, int offset, int value) {
        db.putShort(offset, (short) value);
    }
    
    public static int getInt32(ByteBuffer db, int offset) {
        return db.getInt(offset);
    }

    public static void setInt32(ByteBuffer db, int offset, int value) {
        db.putInt(offset, value);
    }

    public static String getString(ByteBuffer db, int offset, int length) {
        byte[] bytes = new byte[length];
        for(int i = 0; i < length; i++){
            bytes[i] = db.get(offset + i);
        }
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    public static void setString(ByteBuffer db, int offset, String value, int length) {
        byte[] strBytes = new byte[length]; // 默认补0
        if (value != null) {
            byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(valueBytes, 0, strBytes, 0, Math.min(valueBytes.length, length));
        }
        for(int i = 0; i < length; i++){
            db.put(offset + i, strBytes[i]);
        }
    }

    public static UnitData parseToUnitData(byte[] buffer) {
        ByteBuffer db = ByteBuffer.wrap(buffer);
        UnitData data = new UnitData();
        data.setDeviceNr(getInt16(db, 0));
        data.setOccupied(getInt16(db, 2));
        data.setRsn(getInt16(db, 4));
        data.setRc(getInt16(db, 6));
        data.setRq(getInt16(db, 8));
        data.setSpare1(getInt16(db, 10));
        data.setTaskId(getInt16(db, 12));
        data.setDestNr(getInt16(db, 14));
        data.setCountour(getInt16(db, 16));
        data.setWeight(getInt32(db, 18));
        data.setBarcode(getString(db, 22, 16));
        data.setBarcodeSpare(getString(db, 38, 16));
        data.setType(getInt16(db, 90));
        data.setCompletionMark(getInt16(db, 92));
        data.setPcCmdSnAck(getInt16(db, 94));
        data.setPcCmdCrlAck(getInt16(db, 96));
        data.setDataValid(getInt16(db, 98));
        return data;
    }

    public static byte[] toByteArray(UnitData data) {
        byte[] buffer = new byte[100];
        if (data == null) return buffer;
        ByteBuffer db = ByteBuffer.wrap(buffer);

        setInt16(db, 0, data.getDeviceNr());
        setInt16(db, 2, data.getOccupied());
        setInt16(db, 4, data.getRsn());
        setInt16(db, 6, data.getRc());
        setInt16(db, 8, data.getRq());
        setInt16(db, 10, data.getSpare1());
        setInt16(db, 12, data.getTaskId());
        setInt16(db, 14, data.getDestNr());
        setInt16(db, 16, data.getCountour());
        setInt32(db, 18, data.getWeight());
        setString(db, 22, data.getBarcode(), 16);
        setString(db, 38, data.getBarcodeSpare(), 16);
        setInt16(db, 90, data.getType());
        setInt16(db, 92, data.getCompletionMark());
        setInt16(db, 94, data.getPcCmdSnAck());
        setInt16(db, 96, data.getPcCmdCrlAck());
        setInt16(db, 98, data.getDataValid());

        return buffer;
    }
}
