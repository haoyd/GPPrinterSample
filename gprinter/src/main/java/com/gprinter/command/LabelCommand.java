// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.command;

import android.util.Log;
import android.graphics.Bitmap;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class LabelCommand
{
    private static final String DEBUG_TAG = "LabelCommand";
    Vector<Byte> Command;
    
    public LabelCommand() {
        this.Command = null;
        this.Command = new Vector<Byte>();
    }
    
    public LabelCommand(final int width, final int height, final int gap) {
        this.Command = null;
        this.Command = new Vector<Byte>(4096, 1024);
        this.addSize(width, height);
        this.addGap(gap);
    }
    
    public void clrCommand() {
        this.Command.clear();
    }
    
    private void addStrToCommand(final String str) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < bs.length; ++i) {
                this.Command.add(bs[i]);
            }
        }
    }
    
    private void addStrToCommand(final String str, final FONTTYPE font) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                switch (font) {
                    case SIMPLIFIED_CHINESE: {
                        bs = str.getBytes("gb18030");
                        break;
                    }
                    case TRADITIONAL_CHINESE: {
                        bs = str.getBytes("big5");
                        break;
                    }
                    case KOREAN: {
                        bs = str.getBytes("cp949");
                        break;
                    }
                    default: {
                        bs = str.getBytes("gb2312");
                        break;
                    }
                }
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < bs.length; ++i) {
                this.Command.add(bs[i]);
            }
        }
    }
    
    public void addGap(final int gap) {
        final String str = "GAP " + gap + " mm," + 0 + " mm\r\n";
        this.addStrToCommand(str);
    }
    
    public void addSize(final int width, final int height) {
        final String str = "SIZE " + width + " mm," + height + " mm\r\n";
        this.addStrToCommand(str);
    }
    
    public void addCashdrwer(final FOOT m, final int t1, final int t2) {
        final String str = "CASHDRAWER " + m.getValue() + "," + t1 + "," + t2 + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addOffset(final int offset) {
        final String str = "OFFSET " + offset + " mm\r\n";
        this.addStrToCommand(str);
    }
    
    public void addSpeed(final SPEED speed) {
        final String str = "SPEED " + speed.getValue() + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addDensity(final DENSITY density) {
        final String str = "DENSITY " + density.getValue() + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addDirection(final DIRECTION direction, final MIRROR mirror) {
        final String str = "DIRECTION " + direction.getValue() + ',' + mirror.getValue() + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addReference(final int x, final int y) {
        final String str = "REFERENCE " + x + "," + y + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addShif(final int shift) {
        final String str = "SHIFT " + shift + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addCls() {
        final String str = "CLS\r\n";
        this.addStrToCommand(str);
    }
    
    public void addFeed(final int dot) {
        final String str = "FEED " + dot + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addBackFeed(final int dot) {
        final String str = "BACKFEED " + dot + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addFormFeed() {
        final String str = "FORMFEED\r\n";
        this.addStrToCommand(str);
    }
    
    public void addHome() {
        final String str = "HOME\r\n";
        this.addStrToCommand(str);
    }
    
    public void addPrint(final int m, final int n) {
        final String str = "PRINT " + m + "," + n + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addPrint(final int m) {
        final String str = "PRINT " + m + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addCodePage(final CODEPAGE page) {
        final String str = "CODEPAGE " + page.getValue() + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addSound(final int level, final int interval) {
        final String str = "SOUND " + level + "," + interval + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addLimitFeed(final int n) {
        final String str = "LIMITFEED " + n + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addSelfTest() {
        final String str = "SELFTEST\r\n";
        this.addStrToCommand(str);
    }
    
    public void addBar(final int x, final int y, final int width, final int height) {
        final String str = "BAR " + x + "," + y + "," + width + "," + height + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addText(final int x, final int y, final FONTTYPE font, final ROTATION rotation, final FONTMUL Xscal, final FONTMUL Yscal, final String text) {
        final String str = "TEXT " + x + "," + y + ",\"" + font.getValue() + "\"," + rotation.getValue() + "," + Xscal.getValue() + "," + Yscal.getValue() + ",\"" + text + "\"\r\n";
        this.addStrToCommand(str, font);
    }
    
    public void add1DBarcode(final int x, final int y, final BARCODETYPE type, final int height, final READABEL readable, final ROTATION rotation, final String content) {
        final int narrow = 2;
        final int width = 2;
        final String str = "BARCODE " + x + "," + y + ",\"" + type.getValue() + "\"," + height + "," + readable.getValue() + "," + rotation.getValue() + "," + narrow + "," + width + ",\"" + content + "\"\r\n";
        this.addStrToCommand(str);
    }
    
    public void add1DBarcode(final int x, final int y, final BARCODETYPE type, final int height, final READABEL readable, final ROTATION rotation, final int narrow, final int width, final String content) {
        final String str = "BARCODE " + x + "," + y + ",\"" + type.getValue() + "\"," + height + "," + readable.getValue() + "," + rotation.getValue() + "," + narrow + "," + width + ",\"" + content + "\"\r\n";
        this.addStrToCommand(str);
    }
    
    public void addBox(final int x, final int y, final int xend, final int yend, final int thickness) {
        final String str = "BOX " + x + "," + y + "," + xend + "," + yend + "," + thickness + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addBitmap(final int x, final int y, final BITMAP_MODE mode, final int nWidth, final Bitmap b) {
        if (b != null) {
            int width = (nWidth + 7) / 8 * 8;
            int height = b.getHeight() * width / b.getWidth();
            Log.d("BMP", "bmp.getWidth() " + b.getWidth());
            final Bitmap grayBitmap = GpUtils.toGrayscale(b);
            final Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
            final byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
            height = src.length / width;
            width /= 8;
            final String str = "BITMAP " + x + "," + y + "," + width + "," + height + "," + mode.getValue() + ",";
            this.addStrToCommand(str);
            final byte[] codecontent = GpUtils.pixToLabelCmd(src);
            for (int k = 0; k < codecontent.length; ++k) {
                this.Command.add(codecontent[k]);
            }
            Log.d("LabelCommand", "codecontent" + codecontent);
        }
    }
    
    public void addBitmapByMethod(final int x, final int y, final BITMAP_MODE mode, final int nWidth, final Bitmap b) {
        if (b != null) {
            int width = (nWidth + 7) / 8 * 8;
            int height = b.getHeight() * width / b.getWidth();
            Log.d("BMP", "bmp.getWidth() " + b.getWidth());
            final Bitmap rszBitmap = GpUtils.resizeImage(b, width, height);
            final Bitmap grayBitmap = GpUtils.filter(rszBitmap, width, height);
            final byte[] src = GpUtils.bitmapToBWPix(grayBitmap);
            height = src.length / width;
            width /= 8;
            final String str = "BITMAP " + x + "," + y + "," + width + "," + height + "," + mode.getValue() + ",";
            this.addStrToCommand(str);
            final byte[] codecontent = GpUtils.pixToLabelCmd(src);
            for (int k = 0; k < codecontent.length; ++k) {
                this.Command.add(codecontent[k]);
            }
            Log.d("LabelCommand", "codecontent" + codecontent);
        }
    }
    
    public void addBitmap(final int x, final int y, final int nWidth, final Bitmap bmp) {
        if (bmp != null) {
            final int width = (nWidth + 7) / 8 * 8;
            final int height = bmp.getHeight() * width / bmp.getWidth();
            Log.d("BMP", "bmp.getWidth() " + bmp.getWidth());
            final Bitmap rszBitmap = GpUtils.resizeImage(bmp, width, height);
            final byte[] bytes = GpUtils.printTscDraw(x, y, BITMAP_MODE.OVERWRITE, rszBitmap);
            for (int i = 0; i < bytes.length; ++i) {
                this.Command.add(bytes[i]);
            }
            this.addStrToCommand("\r\n");
        }
    }
    
    public void addErase(final int x, final int y, final int xwidth, final int yheight) {
        final String str = "ERASE " + x + "," + y + "," + xwidth + "," + yheight + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addReverse(final int x, final int y, final int xwidth, final int yheight) {
        final String str = "REVERSE " + x + "," + y + "," + xwidth + "," + yheight + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addQRCode(final int x, final int y, final EEC level, final int cellwidth, final ROTATION rotation, final String data) {
        final String str = "QRCODE " + x + "," + y + "," + level.getValue() + "," + cellwidth + "," + 'A' + "," + rotation.getValue() + ",\"" + data + "\"\r\n";
        this.addStrToCommand(str);
    }
    
    public Vector<Byte> getCommand() {
        return this.Command;
    }
    
    public void addQueryPrinterType() {
        String str = new String();
        str = "~!T\r\n";
        this.addStrToCommand(str);
    }
    
    public void addQueryPrinterStatus() {
        this.Command.add((byte)27);
        this.Command.add((byte)33);
        this.Command.add((byte)63);
    }
    
    public void addResetPrinter() {
        this.Command.add((byte)27);
        this.Command.add((byte)33);
        this.Command.add((byte)82);
    }
    
    public void addQueryPrinterLife() {
        final String str = "~!@\r\n";
        this.addStrToCommand(str);
    }
    
    public void addQueryPrinterMemory() {
        final String str = "~!A\r\n";
        this.addStrToCommand(str);
    }
    
    public void addQueryPrinterFile() {
        final String str = "~!F\r\n";
        this.addStrToCommand(str);
    }
    
    public void addQueryPrinterCodePage() {
        final String str = "~!I\r\n";
        this.addStrToCommand(str);
    }
    
    public void addPeel(final EscCommand.ENABLE enable) {
        if (enable.getValue() == 0) {
            final String str = "SET PEEL " + enable.getValue() + "\r\n";
            this.addStrToCommand(str);
        }
    }
    
    public void addTear(final EscCommand.ENABLE enable) {
        final String str = "SET TEAR " + enable.getValue() + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addCutter(final EscCommand.ENABLE enable) {
        final String str = "SET CUTTER " + enable.getValue() + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addCutterBatch() {
        final String str = "SET CUTTER BATCH\r\n";
        this.addStrToCommand(str);
    }
    
    public void addCutterPieces(final short number) {
        final String str = "SET CUTTER " + number + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addReprint(final EscCommand.ENABLE enable) {
        final String str = "SET REPRINT " + enable.getValue() + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addPrintKey(final EscCommand.ENABLE enable) {
        final String str = "SET PRINTKEY " + enable.getValue() + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addPrintKey(final int m) {
        final String str = "SET PRINTKEY " + m + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addPartialCutter(final EscCommand.ENABLE enable) {
        final String str = "SET PARTIAL_CUTTER " + enable.getValue() + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addQueryPrinterStatus(final RESPONSE_MODE mode) {
        final String str = "SET RESPONSE " + mode.getValue() + "\r\n";
        this.addStrToCommand(str);
    }
    
    public void addUserCommand(final String command) {
        this.addStrToCommand(command);
    }
    
    public enum FOOT
    {
        F2(0), 
        F5(1);
        
        private final int value;
        
        private FOOT(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public enum SPEED
    {
        SPEED1DIV5(1.5f), 
        SPEED2(2.0f), 
        SPEED3(3.0f), 
        SPEED4(4.0f);
        
        private final float value;
        
        private SPEED(final float value) {
            this.value = value;
        }
        
        public float getValue() {
            return this.value;
        }
    }
    
    public enum READABEL
    {
        DISABLE(0), 
        EANBEL(1);
        
        private final int value;
        
        private READABEL(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public enum BITMAP_MODE
    {
        OVERWRITE(0), 
        OR(1), 
        XOR(2);
        
        private final int value;
        
        private BITMAP_MODE(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public enum DENSITY
    {
        DNESITY0(0), 
        DNESITY1(1), 
        DNESITY2(2), 
        DNESITY3(3), 
        DNESITY4(4), 
        DNESITY5(5), 
        DNESITY6(6), 
        DNESITY7(7), 
        DNESITY8(8), 
        DNESITY9(9), 
        DNESITY10(10), 
        DNESITY11(11), 
        DNESITY12(12), 
        DNESITY13(13), 
        DNESITY14(14), 
        DNESITY15(15);
        
        private final int value;
        
        private DENSITY(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public enum DIRECTION
    {
        FORWARD(0), 
        BACKWARD(1);
        
        private final int value;
        
        private DIRECTION(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public enum MIRROR
    {
        NORMAL(0), 
        MIRROR(1);
        
        private final int value;
        
        private MIRROR(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public enum CODEPAGE
    {
        PC437(437), 
        PC850(850), 
        PC852(852), 
        PC860(860), 
        PC863(863), 
        PC865(865), 
        WPC1250(1250), 
        WPC1252(1252), 
        WPC1253(1253), 
        WPC1254(1254);
        
        private final int value;
        
        private CODEPAGE(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public enum FONTMUL
    {
        MUL_1(1), 
        MUL_2(2), 
        MUL_3(3), 
        MUL_4(4), 
        MUL_5(5), 
        MUL_6(6), 
        MUL_7(7), 
        MUL_8(8), 
        MUL_9(9), 
        MUL_10(10);
        
        private final int value;
        
        private FONTMUL(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public enum FONTTYPE
    {
        FONT_1("1"), 
        FONT_2("2"), 
        FONT_3("3"), 
        FONT_4("4"), 
        FONT_5("5"), 
        FONT_6("6"), 
        FONT_7("7"), 
        FONT_8("8"), 
        FONT_9("9"), 
        FONT_10("10"), 
        SIMPLIFIED_CHINESE("TSS24.BF2"), 
        TRADITIONAL_CHINESE("TST24.BF2"), 
        KOREAN("K");
        
        private final String value;
        
        private FONTTYPE(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public enum ROTATION
    {
        ROTATION_0(0), 
        ROTATION_90(90), 
        ROTATION_180(180), 
        ROTATION_270(270);
        
        private final int value;
        
        private ROTATION(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public enum EEC
    {
        LEVEL_L("L"), 
        LEVEL_M("M"), 
        LEVEL_Q("Q"), 
        LEVEL_H("H");
        
        private final String value;
        
        private EEC(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public enum BARCODETYPE
    {
        CODE128("128"), 
        CODE128M("128M"), 
        EAN128("EAN128"), 
        ITF25("25"), 
        ITF25C("25C"), 
        CODE39("39"), 
        CODE39C("39C"), 
        CODE39S("39S"), 
        CODE93("93"), 
        EAN13("EAN13"), 
        EAN13_2("EAN13+2"), 
        EAN13_5("EAN13+5"), 
        EAN8("EAN8"), 
        EAN8_2("EAN8+2"), 
        EAN8_5("EAN8+5"), 
        CODABAR("CODA"), 
        POST("POST"), 
        UPCA("UPCA"), 
        UPCA_2("UPCA+2"), 
        UPCA_5("UPCA+5"), 
        UPCE("UPCE13"), 
        UPCE_2("UPCE13+2"), 
        UPCE_5("UPCE13+5"), 
        CPOST("CPOST"), 
        MSI("MSI"), 
        MSIC("MSIC"), 
        PLESSEY("PLESSEY"), 
        ITF14("ITF14"), 
        EAN14("EAN14");
        
        private final String value;
        
        private BARCODETYPE(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public enum RESPONSE_MODE
    {
        ON("ON"), 
        OFF("OFF"), 
        BATCH("BATCH");
        
        private final String value;
        
        private RESPONSE_MODE(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
