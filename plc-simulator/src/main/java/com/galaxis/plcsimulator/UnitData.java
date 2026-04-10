package com.galaxis.plcsimulator;

public class UnitData {
    private int deviceNr;
    private int occupied;
    private int pcRsnAck;
    private int pcRcAck;
    private int pcRqAck;
    private int state;
    private int taskId;
    private int destNr;
    private int contour;
    private int weight;
    private int size;
    private String barcode;
    private int type;
    private int completionMark;
    private int pcCmdSn;
    private int pcCmdC;
    private int spare;

    // ================= Getter & Setter =================

    public int getDeviceNr() { return deviceNr; }
    public void setDeviceNr(int deviceNr) { this.deviceNr = deviceNr; }

    public int getOccupied() { return occupied; }
    public void setOccupied(int occupied) { this.occupied = occupied; }

    public int getPcRsnAck() { return pcRsnAck; }
    public void setPcRsnAck(int pcRsnAck) { this.pcRsnAck = pcRsnAck; }

    public int getPcRcAck() { return pcRcAck; }
    public void setPcRcAck(int pcRcAck) { this.pcRcAck = pcRcAck; }

    public int getPcRqAck() { return pcRqAck; }
    public void setPcRqAck(int pcRqAck) { this.pcRqAck = pcRqAck; }

    public int getState() { return state; }
    public void setState(int state) { this.state = state; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public int getDestNr() { return destNr; }
    public void setDestNr(int destNr) { this.destNr = destNr; }

    public int getContour() { return contour; }
    public void setContour(int contour) { this.contour = contour; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public int getCompletionMark() { return completionMark; }
    public void setCompletionMark(int completionMark) { this.completionMark = completionMark; }

    public int getPcCmdSn() { return pcCmdSn; }
    public void setPcCmdSn(int pcCmdSn) { this.pcCmdSn = pcCmdSn; }

    public int getPcCmdC() { return pcCmdC; }
    public void setPcCmdC(int pcCmdC) { this.pcCmdC = pcCmdC; }

    public int getSpare() { return spare; }
    public void setSpare(int spare) { this.spare = spare; }
}
