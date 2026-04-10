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
        data.setPcRsnAck(getInt16(db, 4));
        data.setPcRcAck(getInt16(db, 6));
        data.setPcRqAck(getInt16(db, 8));
        data.setState(getInt16(db, 10));
        data.setTaskId(getInt16(db, 12));
        data.setDestNr(getInt16(db, 14));
        data.setContour(getInt16(db, 16));
        data.setWeight(getInt16(db, 18));
        data.setSize(getInt16(db, 20));
        data.setBarcode(getString(db, 22, 30));
        data.setType(getInt16(db, 52));
        data.setCompletionMark(getInt16(db, 54));
        data.setPcCmdSn(getInt16(db, 56));
        data.setPcCmdC(getInt16(db, 58));
        data.setSpare(getInt16(db, 60));
        return data;
    }

    public static byte[] toByteArray(UnitData data) {
        byte[] buffer = new byte[62];
        if (data == null) return buffer;
        ByteBuffer db = ByteBuffer.wrap(buffer);

        setInt16(db, 0, data.getDeviceNr());
        setInt16(db, 2, data.getOccupied());
        setInt16(db, 4, data.getPcRsnAck());
        setInt16(db, 6, data.getPcRcAck());
        setInt16(db, 8, data.getPcRqAck());
        setInt16(db, 10, data.getState());
        setInt16(db, 12, data.getTaskId());
        setInt16(db, 14, data.getDestNr());
        setInt16(db, 16, data.getContour());
        setInt16(db, 18, data.getWeight());
        setInt16(db, 20, data.getSize());
        setString(db, 22, data.getBarcode(), 30);
        setInt16(db, 52, data.getType());
        setInt16(db, 54, data.getCompletionMark());
        setInt16(db, 56, data.getPcCmdSn());
        setInt16(db, 58, data.getPcCmdC());
        setInt16(db, 60, data.getSpare());

        return buffer;
    }
}
