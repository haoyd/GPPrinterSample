package com.haoyd.printerlib.formats;

import com.haoyd.printerlib.utils.StringLengthUtil;

/**
 * 配置商品每列的属性
 */
public class GoodsColumnConfiger {

    private String name;                    // 列名
    private int startIndex;                 // 起始位置：如果是左对齐，起始位置按左起始位置；如果右对齐，按右起始位置算起
    private int maxLength = 0;              // 最大宽度
    private boolean alignLeft = true;       // 是否为左对齐

    public GoodsColumnConfiger(String name, int startIndex, int maxLength, boolean alignLeft) {
        this(name, startIndex, alignLeft);
        this.maxLength = maxLength;
    }

    public GoodsColumnConfiger(String name, int startIndex, boolean alignLeft) {
        this.name = name;
        this.startIndex = startIndex;
        this.alignLeft = alignLeft;
    }

    public String getName() {
        return name;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public boolean isAlignLeft() {
        return alignLeft;
    }

    /**
     * 获取列名打印位置
     * @return
     */
    public int getPrintPosition() {
        return getPrintPosition(name);
    }

    /**
     * 获取指定信息打印位置
     * @param data
     * @return
     */
    public int getPrintPosition(String data) {
        return isAlignLeft() ? getStartIndex() : getStartIndex() - StringLengthUtil.getTextLength(data) + 1;
    }


}
