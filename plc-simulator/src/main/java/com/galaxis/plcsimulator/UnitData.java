package com.galaxis.plcsimulator;

public class UnitData {
    private int deviceNr;
    private int occupied;
    private int rsn;
    private int rc;
    private int rq;
    private int spare1;
    private int taskId;
    private int destNr;
    private int countour;
    private int weight; // DINT (4 Bytes)
    private String barcode; // 16 Bytes
    private String barcodeSpare; // 16 Bytes
    private int type;
    private int completionMark;
    private int pcCmdSnAck;
    private int pcCmdCrlAck;
    private int dataValid;

    // ================= Getter & Setter =================

    public int getDeviceNr() { return deviceNr; }
    public void setDeviceNr(int deviceNr) { this.deviceNr = deviceNr; }

    public int getOccupied() { return occupied; }
    public void setOccupied(int occupied) { this.occupied = occupied; }

    public int getRsn() { return rsn; }
    public void setRsn(int rsn) { this.rsn = rsn; }

    public int getRc() { return rc; }
    public void setRc(int rc) { this.rc = rc; }

    public int getRq() { return rq; }
    public void setRq(int rq) { this.rq = rq; }

    public int getSpare1() { return spare1; }
    public void setSpare1(int spare1) { this.spare1 = spare1; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public int getDestNr() { return destNr; }
    public void setDestNr(int destNr) { this.destNr = destNr; }

    public int getCountour() { return countour; }
    public void setCountour(int countour) { this.countour = countour; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getBarcodeSpare() { return barcodeSpare; }
    public void setBarcodeSpare(String barcodeSpare) { this.barcodeSpare = barcodeSpare; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public int getCompletionMark() { return completionMark; }
    public void setCompletionMark(int completionMark) { this.completionMark = completionMark; }

    public int getPcCmdSnAck() { return pcCmdSnAck; }
    public void setPcCmdSnAck(int pcCmdSnAck) { this.pcCmdSnAck = pcCmdSnAck; }

    public int getPcCmdCrlAck() { return pcCmdCrlAck; }
    public void setPcCmdCrlAck(int pcCmdCrlAck) { this.pcCmdCrlAck = pcCmdCrlAck; }

    public int getDataValid() { return dataValid; }
    public void setDataValid(int dataValid) { this.dataValid = dataValid; }
}
