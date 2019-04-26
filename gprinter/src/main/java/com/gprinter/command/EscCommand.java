// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.command;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EscCommand
{
    private static final String DEBUG_TAG = "EscCommand";
    Vector<Byte> Command;
    
    public EscCommand() {
        this.Command = null;
        this.Command = new Vector<Byte>(4096, 1024);
    }
    
    private void addArrayToCommand(final byte[] array) {
        for (int i = 0; i < array.length; ++i) {
            this.Command.add(array[i]);
        }
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
    
    private void addStrToCommand(final String str, final String charset) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes(charset);
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < bs.length; ++i) {
                this.Command.add(bs[i]);
            }
        }
    }
    
    private void addStrToCommandUTF8Encoding(final String str, int length) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("EscCommand", "bs.length" + bs.length);
            if (length > bs.length) {
                length = bs.length;
            }
            Log.d("EscCommand", "length" + length);
            for (int i = 0; i < length; ++i) {
                this.Command.add(bs[i]);
            }
        }
    }
    
    private void addStrToCommand(final String str, int length) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("EscCommand", "bs.length" + bs.length);
            if (length > bs.length) {
                length = bs.length;
            }
            Log.d("EscCommand", "length" + length);
            for (int i = 0; i < length; ++i) {
                this.Command.add(bs[i]);
            }
        }
    }

    /**
     * 加入跳格符
     */
    public void addHorTab() {
        final byte[] command = { 9 };
        this.addArrayToCommand(command);
    }
    
    public void addText(final String text) {
        this.addStrToCommand(text);
    }
    
    public void addText(final String text, final String charsetName) {
        this.addStrToCommand(text, charsetName);
    }
    
    public void addArabicText(String text) {
        text = GpUtils.reverseLetterAndNumber(text);
        text = GpUtils.splitArabic(text);
        final String[] split;
        final String[] fooInput = split = text.split("\\n");
        for (final String in : split) {
            final byte[] output = GpUtils.string2Cp864(in);
            for (int i = 0; i < output.length; ++i) {
                if (output[i] == -16) {
                    this.addArrayToCommand(new byte[] { 27, 116, 29, -124, 27, 116, 22 });
                }
                else if (output[i] == 127) {
                    this.Command.add((byte)(-41));
                }
                else {
                    this.Command.add(output[i]);
                }
            }
        }
    }
    
    public void addPrintAndLineFeed() {
        final byte[] command = { 10 };
        this.addArrayToCommand(command);
    }
    
    public void RealtimeStatusTransmission(final STATUS status) {
        final byte[] command = { 16, 4, 0 };
        command[2] = status.getValue();
        this.addArrayToCommand(command);
    }
    
    public void addGeneratePluseAtRealtime(final LabelCommand.FOOT foot, byte t) {
        final byte[] command = { 16, 20, 1, 0, 0 };
        command[3] = (byte)foot.getValue();
        if (t > 8) {
            t = 8;
        }
        command[4] = t;
        this.addArrayToCommand(command);
    }
    
    public void addSound(byte n, byte t) {
        final byte[] command = { 27, 66, 0, 0 };
        if (n < 0) {
            n = 1;
        }
        else if (n > 9) {
            n = 9;
        }
        if (t < 0) {
            t = 1;
        }
        else if (t > 9) {
            t = 9;
        }
        command[2] = n;
        command[3] = t;
        this.addArrayToCommand(command);
    }

    /**
     * 设置字符右间距
     * @param n = n * unit
     */
    public void addSetRightSideCharacterSpacing(final byte n) {
        final byte[] command = { 27, 32, 0 };
        command[2] = n;
        this.addArrayToCommand(command);
    }
    
    public Vector<Byte> getCommand() {
        return this.Command;
    }

    public void addSelectPrintModes(final FONT font, final ENABLE emphasized, final ENABLE doubleheight, final ENABLE doublewidth, final ENABLE underline) {
        byte temp = 0;
        if (font == FONT.FONTB) {
            temp = 1;
        }
        if (emphasized == ENABLE.ON) {
            temp |= 0x8;
        }
        if (doubleheight == ENABLE.ON) {
            temp |= 0x10;
        }
        if (doublewidth == ENABLE.ON) {
            temp |= 0x20;
        }
        if (underline == ENABLE.ON) {
            temp |= (byte)128;
        }
        final byte[] command = { 27, 33, 0 };
        command[2] = temp;
        this.addArrayToCommand(command);
    }

    /**
     * 设置绝对打印位置
     * @param n
     */
    public void addSetAbsolutePrintPosition(final short n) {
        final byte[] command = { 27, 36, 0, 0 };
        final byte nl = (byte)(n % 256);
        final byte nh = (byte)(n / 256);
        command[2] = nl;
        command[3] = nh;
        this.addArrayToCommand(command);
    }
    
    public void addSelectOrCancelUserDefineCharacter(final ENABLE enable) {
        final byte[] command = { 27, 37, 0 };
        if (enable == ENABLE.ON) {
            command[2] = 1;
        }
        else {
            command[2] = 0;
        }
        this.addArrayToCommand(command);
    }
    
    public void addTurnUnderlineModeOnOrOff(final UNDERLINE_MODE underline) {
        final byte[] command = { 27, 45, 0 };
        command[2] = underline.getValue();
        this.addArrayToCommand(command);
    }

    /**
     * 设置为默认行间距,默认行间距为 3.75 mm 约 30 点
     */
    public void addSelectDefualtLineSpacing() {
        final byte[] command = { 27, 50 };
        this.addArrayToCommand(command);
    }

    /**
     * 设置行间距
     * @param n:行间距为 n* ver_motion_unit 点
     */
    public void addSetLineSpacing(final byte n) {
        final byte[] command = { 27, 51, 0 };
        command[2] = n;
        this.addArrayToCommand(command);
    }
    
    public void addCancelUserDefinedCharacters(final byte n) {
        final byte[] command = { 27, 63, 0 };
        if (n >= 32 && n <= 126) {
            command[2] = n;
        }
        else {
            command[2] = 32;
        }
        this.addArrayToCommand(command);
    }
    
    public void addInitializePrinter() {
        final byte[] command = { 27, 64 };
        this.addArrayToCommand(command);
    }
    
    public void addTurnEmphasizedModeOnOrOff(final ENABLE enabel) {
        final byte[] command = { 27, 69, 0 };
        command[2] = enabel.getValue();
        this.addArrayToCommand(command);
    }
    
    public void addTurnDoubleStrikeOnOrOff(final ENABLE enabel) {
        final byte[] command = { 27, 71, 0 };
        command[2] = enabel.getValue();
        this.addArrayToCommand(command);
    }

    /**
     * 打印并走纸
     * @param n:走纸距离为 n* ver_motion_unit 点
     */
    public void addPrintAndFeedPaper(final byte n) {
        final byte[] command = { 27, 74, 0 };
        command[2] = n;
        this.addArrayToCommand(command);
    }
    
    public void addSelectCharacterFont(final FONT font) {
        final byte[] command = { 27, 77, 0 };
        command[2] = font.getValue();
        this.addArrayToCommand(command);
    }
    
    public void addSelectInternationalCharacterSet(final CHARACTER_SET set) {
        final byte[] command = { 27, 82, 0 };
        command[2] = set.getValue();
        this.addArrayToCommand(command);
    }
    
    public void addTurn90ClockWiseRotatin(final ENABLE enabel) {
        final byte[] command = { 27, 86, 0 };
        command[2] = enabel.getValue();
        this.addArrayToCommand(command);
    }

    /**
     * 将打印位置设置到距当前位置 n 点处
     * @param n
     */
    public void addSetRelativePrintPositon(final short n) {
        final byte[] command = { 27, 92, 0, 0 };
        final byte nl = (byte)(n % 256);
        final byte nh = (byte)(n / 256);
        command[2] = nl;
        command[3] = nh;
        this.addArrayToCommand(command);
    }
    
    public void addSelectJustification(final JUSTIFICATION just) {
        final byte[] command = { 27, 97, 0 };
        command[2] = just.getValue();
        this.addArrayToCommand(command);
    }

    /**
     * 打印并走纸 n 行
     * @param n
     */
    public void addPrintAndFeedLines(final byte n) {
        final byte[] command = { 27, 100, 0 };
        command[2] = n;
        this.addArrayToCommand(command);
    }
    
    public void addGeneratePlus(final LabelCommand.FOOT foot, final byte t1, final byte t2) {
        final byte[] command = { 27, 112, 0, 0, 0 };
        command[2] = (byte)foot.getValue();
        command[3] = t1;
        command[4] = t2;
        this.addArrayToCommand(command);
    }
    
    public void addSelectCodePage(final CODEPAGE page) {
        final byte[] command = { 27, 116, 0 };
        command[2] = page.getValue();
        this.addArrayToCommand(command);
    }
    
    public void addTurnUpsideDownModeOnOrOff(final ENABLE enable) {
        final byte[] command = { 27, 123, 0 };
        command[2] = enable.getValue();
        this.addArrayToCommand(command);
    }

    /**
     * 选择字符横向和纵向放大倍数
     * @param width 1-8 倍
     * @param height
     */
    public void addSetCharcterSize(final WIDTH_ZOOM width, final HEIGHT_ZOOM height) {
        final byte[] command = { 29, 33, 0 };
        byte temp = 0;
        temp |= width.getValue();
        temp |= height.getValue();
        command[2] = temp;
        this.addArrayToCommand(command);
    }
    
    public void addTurnReverseModeOnOrOff(final ENABLE enable) {
        final byte[] command = { 29, 66, 0 };
        command[2] = enable.getValue();
        this.addArrayToCommand(command);
    }
    
    public void addSelectPrintingPositionForHRICharacters(final HRI_POSITION position) {
        final byte[] command = { 29, 72, 0 };
        command[2] = position.getValue();
        this.addArrayToCommand(command);
    }

    /**
     * 设置左边距
     * @param n: 左边距为 n *hor_motion_unit 点
     */
    public void addSetLeftMargin(final short n) {
        final byte[] command = { 29, 76, 0, 0 };
        final byte nl = (byte)(n % 256);
        final byte nh = (byte)(n / 256);
        command[2] = nl;
        command[3] = nh;
        this.addArrayToCommand(command);
    }

    /**
     * 横纵向移动单位
     * Gp58 系列打印机均为203dpi，1mm 约为8点，实际打印宽度为48mm，约384点
     * 横向移动单位 hor_motion_unit 默认为1点，纵向移动单位 ver_motion_unit 默认为0.5点
     * 汉字为 24*24 点阵
     * @param x
     * @param y
     */
    public void addSetHorAndVerMotionUnits(final byte x, final byte y) {
        final byte[] command = { 29, 80, 0, 0 };
        command[2] = x;
        command[3] = y;
        this.addArrayToCommand(command);
    }
    
    public void addCutAndFeedPaper(final byte length) {
        final byte[] command = { 29, 86, 66, 0 };
        command[3] = length;
        this.addArrayToCommand(command);
    }
    
    public void addCutPaper() {
        final byte[] command = { 29, 86, 1 };
        this.addArrayToCommand(command);
    }
    
    public void addSetPrintingAreaWidth(final short width) {
        final byte nl = (byte)(width % 256);
        final byte nh = (byte)(width / 256);
        final byte[] command = { 29, 87, 0, 0 };
        command[2] = nl;
        command[3] = nh;
        this.addArrayToCommand(command);
    }
    
    public void addSetAutoSatusBack(final ENABLE enable) {
        final byte[] command = { 29, 97, 0 };
        if (enable == ENABLE.OFF) {
            command[2] = 0;
        }
        else {
            command[2] = -1;
        }
        this.addArrayToCommand(command);
    }
    
    public void addSetFontForHRICharacter(final FONT font) {
        final byte[] command = { 29, 102, 0 };
        command[2] = font.getValue();
        this.addArrayToCommand(command);
    }
    
    public void addSetBarcodeHeight(final byte height) {
        final byte[] command = { 29, 104, 0 };
        command[2] = height;
        this.addArrayToCommand(command);
    }
    
    public void addSetBarcodeWidth(byte width) {
        final byte[] command = { 29, 119, 0 };
        if (width > 6) {
            width = 6;
        }
        if (width < 2) {
            width = 1;
        }
        command[2] = width;
        this.addArrayToCommand(command);
    }
    
    public void addSetKanjiFontMode(final ENABLE DoubleWidth, final ENABLE DoubleHeight, final ENABLE Underline) {
        final byte[] command = { 28, 33, 0 };
        byte temp = 0;
        if (DoubleWidth == ENABLE.ON) {
            temp |= 0x4;
        }
        if (DoubleHeight == ENABLE.ON) {
            temp |= 0x8;
        }
        if (Underline == ENABLE.ON) {
            temp |= (byte)128;
        }
        command[2] = temp;
        this.addArrayToCommand(command);
    }
    
    public void addSelectKanjiMode() {
        final byte[] command = { 28, 38 };
        this.addArrayToCommand(command);
    }
    
    public void addSetKanjiUnderLine(final UNDERLINE_MODE underline) {
        final byte[] command = { 28, 45, 0 };
        command[3] = underline.getValue();
        this.addArrayToCommand(command);
    }
    
    public void addCancelKanjiMode() {
        final byte[] command = { 28, 46 };
        this.addArrayToCommand(command);
    }
    
    public void addSetKanjiLefttandRightSpace(final byte left, final byte right) {
        final byte[] command = { 28, 83, 0, 0 };
        command[2] = left;
        command[3] = right;
        this.addArrayToCommand(command);
    }
    
    public void addSetQuadrupleModeForKanji(final ENABLE enable) {
        final byte[] command = { 28, 87, 0 };
        command[2] = enable.getValue();
        this.addArrayToCommand(command);
    }
    
    public void addRastBitImage(final Bitmap bitmap, final int nWidth, final int nMode) {
        if (bitmap != null) {
            final int width = (nWidth + 7) / 8 * 8;
            int height = bitmap.getHeight() * width / bitmap.getWidth();
            final Bitmap grayBitmap = GpUtils.toGrayscale(bitmap);
            final Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
            final byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
            final byte[] command = new byte[8];
            height = src.length / width;
            command[0] = 29;
            command[1] = 118;
            command[2] = 48;
            command[3] = (byte)(nMode & 0x1);
            command[4] = (byte)(width / 8 % 256);
            command[5] = (byte)(width / 8 / 256);
            command[6] = (byte)(height % 256);
            command[7] = (byte)(height / 256);
            this.addArrayToCommand(command);
            final byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);
            for (int k = 0; k < codecontent.length; ++k) {
                this.Command.add(codecontent[k]);
            }
        }
        else {
            Log.d("BMP", "bmp.  null ");
        }
    }
    
    public void addOriginRastBitImage(final Bitmap bitmap, final int nWidth, final int nMode) {
        if (bitmap != null) {
            final int width = (nWidth + 7) / 8 * 8;
            final int height = bitmap.getHeight() * width / bitmap.getWidth();
            final Bitmap rszBitmap = GpUtils.resizeImage(bitmap, width, height);
            final byte[] data = GpUtils.printEscDraw(rszBitmap);
            this.addArrayToCommand(data);
        }
        else {
            Log.d("BMP", "bmp.  null ");
        }
    }
    
    public void addRastBitImageWithMethod(final Bitmap bitmap, final int nWidth, final int nMode, final int method) {
        if (bitmap != null) {
            final int width = (nWidth + 7) / 8 * 8;
            int height = bitmap.getHeight() * width / bitmap.getWidth();
            final Bitmap resizeImage = GpUtils.resizeImage(bitmap, width, height);
            final Bitmap rszBitmap = GpUtils.filter(resizeImage, resizeImage.getWidth(), resizeImage.getHeight());
            final byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
            final byte[] command = new byte[8];
            height = src.length / width;
            command[0] = 29;
            command[1] = 118;
            command[2] = 48;
            command[3] = (byte)(nMode & 0x1);
            command[4] = (byte)(width / 8 % 256);
            command[5] = (byte)(width / 8 / 256);
            command[6] = (byte)(height % 256);
            command[7] = (byte)(height / 256);
            this.addArrayToCommand(command);
            final byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);
            for (int k = 0; k < codecontent.length; ++k) {
                this.Command.add(codecontent[k]);
            }
        }
        else {
            Log.d("BMP", "bmp.  null ");
        }
    }
    
    public void addDownloadNvBitImage(final Bitmap[] bitmap) {
        if (bitmap != null) {
            Log.d("BMP", "bitmap.length " + bitmap.length);
            final int n = bitmap.length;
            if (n > 0) {
                final byte[] command = { 28, 113, (byte)n };
                this.addArrayToCommand(command);
                for (int i = 0; i < n; ++i) {
                    int height = (bitmap[i].getHeight() + 7) / 8 * 8;
                    final int width = bitmap[i].getWidth() * height / bitmap[i].getHeight();
                    final Bitmap grayBitmap = GpUtils.toGrayscale(bitmap[i]);
                    final Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
                    final byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
                    height = src.length / width;
                    Log.d("BMP", "bmp  Width " + width);
                    Log.d("BMP", "bmp  height " + height);
                    final byte[] codecontent = GpUtils.pixToEscNvBitImageCmd(src, width, height);
                    for (int k = 0; k < codecontent.length; ++k) {
                        this.Command.add(codecontent[k]);
                    }
                }
            }
            return;
        }
        Log.d("BMP", "bmp.  null ");
    }
    
    public void addPrintNvBitmap(final byte n, final byte mode) {
        final byte[] command = { 28, 112, 0, 0 };
        command[2] = n;
        command[3] = mode;
        this.addArrayToCommand(command);
    }
    
    public void addUPCA(final String content) {
        final byte[] command = { 29, 107, 65, 11 };
        if (content.length() < command[3]) {
            return;
        }
        this.addArrayToCommand(command);
        this.addStrToCommand(content, 11);
    }
    
    public void addUPCE(final String content) {
        final byte[] command = { 29, 107, 66, 11 };
        if (content.length() < command[3]) {
            return;
        }
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }
    
    public void addEAN13(final String content) {
        final byte[] command = { 29, 107, 67, 12 };
        if (content.length() < command[3]) {
            return;
        }
        this.addArrayToCommand(command);
        Log.d("EscCommand", "content.length" + content.length());
        this.addStrToCommand(content, command[3]);
    }
    
    public void addEAN8(final String content) {
        final byte[] command = { 29, 107, 68, 7 };
        if (content.length() < command[3]) {
            return;
        }
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }
    
    @SuppressLint({ "DefaultLocale" })
    public void addCODE39(String content) {
        final byte[] command = { 29, 107, 69, (byte)content.length() };
        content = content.toUpperCase();
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }
    
    public void addITF(final String content) {
        final byte[] command = { 29, 107, 70, (byte)content.length() };
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }
    
    public void addCODABAR(final String content) {
        final byte[] command = { 29, 107, 71, (byte)content.length() };
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }
    
    public void addCODE93(final String content) {
        final byte[] command = { 29, 107, 72, (byte)content.length() };
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }
    
    public void addCODE128(final String content) {
        final byte[] command = { 29, 107, 73, (byte)content.length() };
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }
    
    public String genCodeC(final String content) {
        final List<Byte> bytes = new ArrayList<Byte>(20);
        final int len = content.length();
        bytes.add((byte)123);
        bytes.add((byte)67);
        for (int i = 0; i < len; i += 2) {
            final int ken = (content.charAt(i) - '0') * '\n';
            final int bits = content.charAt(i + 1) - '0';
            final int current = ken + bits;
            bytes.add((byte)current);
        }
        final byte[] bb = new byte[bytes.size()];
        for (int j = 0; j < bb.length; ++j) {
            bb[j] = bytes.get(j);
        }
        return new String(bb, 0, bb.length);
    }
    
    public String genCodeB(final String content) {
        return String.format("{B%s", content);
    }
    
    public String genCode128(final String content) {
        final String regex = "([^0-9])";
        final String[] str = content.split(regex);
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(content);
        String splitString = null;
        final int strlen = str.length;
        if (strlen > 0 && matcher.find()) {
            splitString = matcher.group(0);
        }
        final StringBuilder sb = new StringBuilder();
        for (final String first : str) {
            final int len = first.length();
            final int result = len % 2;
            if (result == 0) {
                final String codeC = this.genCodeC(first);
                sb.append(codeC);
            }
            else {
                sb.append(this.genCodeB(String.valueOf(first.charAt(0))));
                sb.append(this.genCodeC(first.substring(1, first.length())));
            }
            if (splitString != null) {
                sb.append(this.genCodeB(splitString));
                splitString = null;
            }
        }
        return sb.toString();
    }
    
    public void addSelectSizeOfModuleForQRCode(final byte n) {
        final byte[] command = { 29, 40, 107, 3, 0, 49, 67, 3 };
        command[7] = n;
        this.addArrayToCommand(command);
    }
    
    public void addSelectErrorCorrectionLevelForQRCode(final byte n) {
        final byte[] command = { 29, 40, 107, 3, 0, 49, 69, 0 };
        command[7] = n;
        this.addArrayToCommand(command);
    }
    
    public void addStoreQRCodeData(final String content) {
        final byte[] command = { 29, 40, 107, 0, 0, 49, 80, 48 };
        command[3] = (byte)((content.getBytes().length + 3) % 256);
        command[4] = (byte)((content.getBytes().length + 3) / 256);
        this.addArrayToCommand(command);
        byte[] bs = null;
        if (!content.equals("")) {
            try {
                bs = content.getBytes("utf-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < bs.length; ++i) {
                this.Command.add(bs[i]);
            }
        }
    }
    
    public void addPrintQRCode() {
        final byte[] command = { 29, 40, 107, 3, 0, 49, 81, 48 };
        this.addArrayToCommand(command);
    }
    
    public void addQueryPrinterStatus() {
        final byte[] command = { 29, 114, 1 };
        this.addArrayToCommand(command);
    }
    
    public void addUserCommand(final byte[] command) {
        this.addArrayToCommand(command);
    }
    
    public enum STATUS
    {
        PRINTER_STATUS(1), 
        PRINTER_OFFLINE(2), 
        PRINTER_ERROR(3), 
        PRINTER_PAPER(4);
        
        private final int value;
        
        private STATUS(final int value) {
            this.value = value;
        }
        
        public byte getValue() {
            return (byte)this.value;
        }
    }
    
    public enum ENABLE
    {
        OFF(0), 
        ON(1);
        
        private final int value;
        
        private ENABLE(final int value) {
            this.value = value;
        }
        
        public byte getValue() {
            return (byte)this.value;
        }
    }
    
    public enum UNDERLINE_MODE
    {
        OFF(0), 
        UNDERLINE_1DOT(1), 
        UNDERLINE_2DOT(2);
        
        private final int value;
        
        private UNDERLINE_MODE(final int value) {
            this.value = value;
        }
        
        public byte getValue() {
            return (byte)this.value;
        }
    }
    
    public enum FONT
    {
        FONTA(0), 
        FONTB(1);
        
        private final int value;
        
        private FONT(final int value) {
            this.value = value;
        }
        
        public byte getValue() {
            return (byte)this.value;
        }
    }
    
    public enum CHARACTER_SET
    {
        USA(0), 
        FRANCE(1), 
        GERMANY(2), 
        UK(3), 
        DENMARK_I(4), 
        SWEDEN(5), 
        ITALY(6), 
        SPAIN_I(7), 
        JAPAN(8), 
        NORWAY(9), 
        DENMARK_II(10), 
        SPAIN_II(11), 
        LATIN_AMERCIA(12), 
        KOREAN(13), 
        SLOVENIA(14), 
        CHINA(15);
        
        private final int value;
        
        private CHARACTER_SET(final int value) {
            this.value = value;
        }
        
        public byte getValue() {
            return (byte)this.value;
        }
    }
    
    public enum JUSTIFICATION
    {
        LEFT(0), 
        CENTER(1), 
        RIGHT(2);
        
        private final int value;
        
        private JUSTIFICATION(final int value) {
            this.value = value;
        }
        
        public byte getValue() {
            return (byte)this.value;
        }
    }
    
    public enum CODEPAGE
    {
        PC437(0), 
        KATAKANA(1), 
        PC850(2), 
        PC860(3), 
        PC863(4), 
        PC865(5), 
        WEST_EUROPE(6), 
        GREEK(7), 
        HEBREW(8), 
        EAST_EUROPE(9), 
        IRAN(10), 
        WPC1252(16), 
        PC866(17), 
        PC852(18), 
        PC858(19), 
        IRANII(20), 
        LATVIAN(21), 
        ARABIC(22), 
        PT151(23), 
        PC747(24), 
        WPC1257(25), 
        VIETNAM(27), 
        PC864(28), 
        PC1001(29), 
        UYGUR(30), 
        THAI(255);
        
        private final int value;
        
        private CODEPAGE(final int value) {
            this.value = value;
        }
        
        public byte getValue() {
            return (byte)this.value;
        }
    }
    
    public enum WIDTH_ZOOM
    {
        MUL_1(0), 
        MUL_2(16), 
        MUL_3(32), 
        MUL_4(48), 
        MUL_5(64), 
        MUL_6(80), 
        MUL_7(96), 
        MUL_8(112);
        
        private final int value;
        
        private WIDTH_ZOOM(final int value) {
            this.value = value;
        }
        
        public byte getValue() {
            return (byte)this.value;
        }
    }
    
    public enum HEIGHT_ZOOM
    {
        MUL_1(0), 
        MUL_2(1), 
        MUL_3(2), 
        MUL_4(3), 
        MUL_5(4), 
        MUL_6(5), 
        MUL_7(6), 
        MUL_8(7);
        
        private final int value;
        
        private HEIGHT_ZOOM(final int value) {
            this.value = value;
        }
        
        public byte getValue() {
            return (byte)this.value;
        }
    }
    
    public enum HRI_POSITION
    {
        NO_PRINT(0), 
        ABOVE(1), 
        BELOW(2), 
        ABOVE_AND_BELOW(3);
        
        private final int value;
        
        private HRI_POSITION(final int value) {
            this.value = value;
        }
        
        public byte getValue() {
            return (byte)this.value;
        }
    }
}
