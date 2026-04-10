package com.galaxis.plcsimulator;


public class UnitDataPair {
    private int unitIndex;
    private UnitData db10;
    private UnitData db11;

    // 手动生成的 Getter 和 Setter，绝对不会失效
    public int getUnitIndex() { return unitIndex; }
    public void setUnitIndex(int unitIndex) { this.unitIndex = unitIndex; }

    public UnitData getDb10() { return db10; }
    public void setDb10(UnitData db10) { this.db10 = db10; }

    public UnitData getDb11() { return db11; }
    public void setDb11(UnitData db11) { this.db11 = db11; }
}