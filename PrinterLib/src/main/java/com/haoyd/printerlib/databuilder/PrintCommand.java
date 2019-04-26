package com.haoyd.printerlib.databuilder;

import android.text.TextUtils;
import android.util.Base64;

import com.gprinter.command.EscCommand;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.command.GpUtils;

import java.util.Vector;

public class PrintCommand {

    private EscCommand esc;

    protected static final String PRINT_LINE = "------------------------------------------------\n";
    protected static final String PRINT_PAGE_END_LINE = "- - - - - - x - - - - - - - - - -x- - - - - - - \n";
    protected static final String PRINT_ONLINE_PAY = "-----已在线支付-----\n\n";
    protected static final String PRINT_PAY_PREFIX = "-----";
    protected static final String PRINT_PAY_POSTFIX = "-----\n\n";
    protected static final int PRINT_TOTAL_LENGTH = 48 * 3;
    protected static final int MAX_GOODS_NAME_LENGTH = 22 * 3;
    protected static final short PRINT_UNIT = 43;

    protected static final short PRINT_POSITION_0 = 0;
    protected static final short PRINT_POSITION_1 = 26 * 3;
    protected static final short PRINT_POSITION_2 = 32 * 3;
    protected static final short PRINT_POSITION_3 = 42 * 3;

    public PrintCommand() {
        this.esc = new EscCommand();
    }

    /**
     * 设置大字体
     */
    public PrintCommand setFontBig() {
        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);
        return this;
    }

    /**
     * 设置小字体
     */
    public PrintCommand setFontSmall() {
        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);
        return this;
    }

    /**
     * 打印分隔线
     */
    public PrintCommand addLine() {
        esc.addText(PRINT_LINE);
        return this;
    }

    /**
     * 左对齐
     */
    public PrintCommand alignLeft() {
        esc.addSelectJustification(JUSTIFICATION.LEFT);
        return this;
    }

    /**
     * 居中对齐
     */
    public PrintCommand alignCenter() {
        esc.addSelectJustification(JUSTIFICATION.CENTER);
        return this;
    }

    /**
     * 右对齐
     */
    public PrintCommand alignRight() {
        esc.addSelectJustification(JUSTIFICATION.RIGHT);
        return this;
    }

    /**
     * 分隔线
     */
    public PrintCommand addDivider() {
        esc.addText(PRINT_PAGE_END_LINE);
        return this;
    }

    /**
     * 换行
     */
    public PrintCommand lineFeed() {
        esc.addPrintAndLineFeed();
        return this;
    }

    /**
     * 添加多个空行
     * @param lines
     */
    public PrintCommand addMutiFeedLines(short lines) {
        esc.addPrintAndFeedLines((byte) lines);
        return this;
    }

    /**
     * 添加结束线
     */
    public PrintCommand addEndLine() {
        esc.addText(PRINT_PAGE_END_LINE);
        return this;
    }

    /**
     * 设置绝对位置
     */
    public PrintCommand setAbsolutePosition(short position) {
        esc.addSetHorAndVerMotionUnits((byte) PRINT_UNIT, (byte) 0);
        esc.addSetAbsolutePrintPosition((short) (position * 3));
        return this;
    }

    /**
     * 添加初始空行
     */
    public PrintCommand addInitLine() {
        esc.addInitializePrinter();
        return this;
    }

    // --------------------------------------------------------------------------------------------

    /**
     * 添加打印数据
     * @param msg
     */
    public PrintCommand addText(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return this;
        }

        esc.addText(msg);
        return this;
    }

    /**
     * 添加打印状态广播回调
     */
    public PrintCommand addPrintStatusBack() {
        esc.addQueryPrinterStatus();
        return this;
    }

    /**
     * 获取最终的数据
     * @return
     */
    public String build() {
        Vector<Byte> datas = esc.getCommand();
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}
