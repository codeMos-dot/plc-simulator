package com.galaxis.plcsimulator;


/**
 * @ClassName PlcMemory
 * @Description PLC 内存模型预留类
 *              目前使用 ByteBuffer 直接管理内存，此类预留用于未来扩展
 * @Author w
 * @Date 2026/4/1 15:15
 */
public class PlcMemory {
    
    // 内存区域常量定义
    public static final int DB10_START = 0;
    public static final int DB10_SIZE = 65000;
    public static final int DB11_START = 0;
    public static final int DB11_SIZE = 65000;
    
    // 单元数据常量定义
    public static final int UNIT_SIZE = 62;
    public static final int UNIT_DATA_START_OFFSET = 50;
    public static final int MAX_UNIT_INDEX = 1047;
    
    /**
     * 计算指定单元的内存偏移量
     * @param unitIndex 单元索引（从1开始）
     * @return 内存偏移量
     */
    public static int getUnitOffset(int unitIndex) {
        if (unitIndex < 1 || unitIndex > MAX_UNIT_INDEX) {
            throw new IllegalArgumentException("Unit index out of bounds: " + unitIndex);
        }
        return UNIT_DATA_START_OFFSET + (unitIndex - 1) * UNIT_SIZE;
    }
    
    /**
     * 验证单元索引是否有效
     * @param unitIndex 单元索引
     * @return 是否有效
     */
    public static boolean isValidUnitIndex(int unitIndex) {
        return unitIndex >= 1 && unitIndex <= MAX_UNIT_INDEX;
    }
}
