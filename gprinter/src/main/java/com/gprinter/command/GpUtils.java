// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.command;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.io.UnsupportedEncodingException;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.Canvas;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import android.os.Environment;
import android.graphics.Matrix;
import android.graphics.Bitmap;
import java.util.regex.Pattern;

public class GpUtils
{
    private static Pattern pattern;
    private static int[] p0;
    private static int[] p1;
    private static int[] p2;
    private static int[] p3;
    private static int[] p4;
    private static int[] p5;
    private static int[] p6;
    private static int[][] Floyd16x16;
    private static int[][] Floyd8x8;
    public static final int PAPER_58_WIDTH = 32;
    public static final int PAPER_80_WIDTH = 48;
    private static int sPaperWidth;
    private static Integer[] theSet0;
    private static Integer[][] FormatTable;
    static Integer[] theSet1;
    static Integer[] theSet2;
    public static final int ALGORITHM_DITHER_16x16 = 16;
    public static final int ALGORITHM_DITHER_8x8 = 8;
    public static final int ALGORITHM_TEXTMODE = 2;
    public static final int ALGORITHM_GRAYTEXTMODE = 1;
    public static final int[][] COLOR_PALETTE;
    private static int method;
    public static final int FLOYD_STEINBERG_DITHER = 1;
    public static final int ATKINSON_DITHER = 2;
    
    public static Bitmap resizeImage(final Bitmap bitmap, final int w, final int h) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final float scaleWidth = w / width;
        final float scaleHeight = h / height;
        final Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        final Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }
    
    public static void saveMyBitmap(final Bitmap mBitmap) {
        final File f = new File(Environment.getExternalStorageDirectory().getPath(), "Btatotest.jpeg");
        try {
            f.createNewFile();
        }
        catch (IOException ex) {}
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, (OutputStream)fOut);
            fOut.flush();
            fOut.close();
        }
        catch (FileNotFoundException ex2) {}
        catch (IOException ex3) {}
    }
    
    public static Bitmap toGrayscale(final Bitmap bmpOriginal) {
        final int height = bmpOriginal.getHeight();
        final int width = bmpOriginal.getWidth();
        final Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        final Canvas c = new Canvas(bmpGrayscale);
        final Paint paint = new Paint();
        final ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0f);
        final ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter((ColorFilter)f);
        c.drawBitmap(bmpOriginal, 0.0f, 0.0f, paint);
        return bmpGrayscale;
    }
    
    static byte[] pixToEscRastBitImageCmd(final byte[] src, final int nWidth, final int nMode) {
        final int nHeight = src.length / nWidth;
        final byte[] data = new byte[8 + src.length / 8];
        data[0] = 29;
        data[1] = 118;
        data[2] = 48;
        data[3] = (byte)(nMode & 0x1);
        data[4] = (byte)(nWidth / 8 % 256);
        data[5] = (byte)(nWidth / 8 / 256);
        data[6] = (byte)(nHeight % 256);
        data[7] = (byte)(nHeight / 256);
        int i = 8;
        int k = 0;
        while (i < data.length) {
            data[i] = (byte)(GpUtils.p0[src[k]] + GpUtils.p1[src[k + 1]] + GpUtils.p2[src[k + 2]] + GpUtils.p3[src[k + 3]] + GpUtils.p4[src[k + 4]] + GpUtils.p5[src[k + 5]] + GpUtils.p6[src[k + 6]] + src[k + 7]);
            k += 8;
            ++i;
        }
        return data;
    }
    
    public static byte[] pixToEscRastBitImageCmd(final byte[] src) {
        final byte[] data = new byte[src.length / 8];
        int i = 0;
        int k = 0;
        while (i < data.length) {
            data[i] = (byte)(GpUtils.p0[src[k]] + GpUtils.p1[src[k + 1]] + GpUtils.p2[src[k + 2]] + GpUtils.p3[src[k + 3]] + GpUtils.p4[src[k + 4]] + GpUtils.p5[src[k + 5]] + GpUtils.p6[src[k + 6]] + src[k + 7]);
            k += 8;
            ++i;
        }
        return data;
    }
    
    static byte[] pixToEscNvBitImageCmd(final byte[] src, final int width, final int height) {
        final byte[] data = new byte[src.length / 8 + 4];
        data[0] = (byte)(width / 8 % 256);
        data[1] = (byte)(width / 8 / 256);
        data[2] = (byte)(height / 8 % 256);
        data[3] = (byte)(height / 8 / 256);
        int k = 0;
        for (int i = 0; i < width; ++i) {
            k = 0;
            for (int j = 0; j < height / 8; ++j) {
                data[4 + j + i * height / 8] = (byte)(GpUtils.p0[src[i + k]] + GpUtils.p1[src[i + k + 1 * width]] + GpUtils.p2[src[i + k + 2 * width]] + GpUtils.p3[src[i + k + 3 * width]] + GpUtils.p4[src[i + k + 4 * width]] + GpUtils.p5[src[i + k + 5 * width]] + GpUtils.p6[src[i + k + 6 * width]] + src[i + k + 7 * width]);
                k += 8 * width;
            }
        }
        return data;
    }
    
    public static byte[] pixToLabelCmd(final byte[] src) {
        final byte[] data = new byte[src.length / 8];
        int k = 0;
        int j = 0;
        while (k < data.length) {
            final byte temp = (byte)(GpUtils.p0[src[j]] + GpUtils.p1[src[j + 1]] + GpUtils.p2[src[j + 2]] + GpUtils.p3[src[j + 3]] + GpUtils.p4[src[j + 4]] + GpUtils.p5[src[j + 5]] + GpUtils.p6[src[j + 6]] + src[j + 7]);
            data[k] = (byte)~temp;
            j += 8;
            ++k;
        }
        return data;
    }
    
    public static byte[] pixToTscCmd(final int x, final int y, final int mode, final byte[] src, final int nWidth) {
        final int height = src.length / nWidth;
        final int width = nWidth / 8;
        final String str = "BITMAP " + x + "," + y + "," + width + "," + height + "," + mode + ",";
        byte[] bitmap = null;
        try {
            bitmap = str.getBytes("GB2312");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final byte[] arrayOfByte = new byte[src.length / 8];
        int k = 0;
        int j = 0;
        while (k < arrayOfByte.length) {
            final byte temp = (byte)(GpUtils.p0[src[j]] + GpUtils.p1[src[j + 1]] + GpUtils.p2[src[j + 2]] + GpUtils.p3[src[j + 3]] + GpUtils.p4[src[j + 4]] + GpUtils.p5[src[j + 5]] + GpUtils.p6[src[j + 6]] + src[j + 7]);
            arrayOfByte[k] = (byte)~temp;
            j += 8;
            ++k;
        }
        final byte[] data = new byte[bitmap.length + arrayOfByte.length];
        System.arraycopy(bitmap, 0, data, 0, bitmap.length);
        System.arraycopy(arrayOfByte, 0, data, bitmap.length, arrayOfByte.length);
        return data;
    }
    
    private static void format_K_dither16x16(final int[] orgpixels, final int xsize, final int ysize, final byte[] despixels) {
        int k = 0;
        for (int y = 0; y < ysize; ++y) {
            for (int x = 0; x < xsize; ++x) {
                if ((orgpixels[k] & 0xFF) > GpUtils.Floyd16x16[x & 0xF][y & 0xF]) {
                    despixels[k] = 0;
                }
                else {
                    despixels[k] = 1;
                }
                ++k;
            }
        }
    }
    
    public static byte[] bitmapToBWPix(final Bitmap mBitmap) {
        final int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        final byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];
        final Bitmap grayBitmap = toGrayscale(mBitmap);
        grayBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        format_K_dither16x16(pixels, grayBitmap.getWidth(), grayBitmap.getHeight(), data);
        return data;
    }
    
    private static void format_K_dither16x16_int(final int[] orgpixels, final int xsize, final int ysize, final int[] despixels) {
        int k = 0;
        for (int y = 0; y < ysize; ++y) {
            for (int x = 0; x < xsize; ++x) {
                if ((orgpixels[k] & 0xFF) > GpUtils.Floyd16x16[x & 0xF][y & 0xF]) {
                    despixels[k] = -1;
                }
                else {
                    despixels[k] = -16777216;
                }
                ++k;
            }
        }
    }
    
    private static void format_K_dither8x8_int(final int[] orgpixels, final int xsize, final int ysize, final int[] despixels) {
        int k = 0;
        for (int y = 0; y < ysize; ++y) {
            for (int x = 0; x < xsize; ++x) {
                if ((orgpixels[k] & 0xFF) >> 2 > GpUtils.Floyd8x8[x & 0x7][y & 0x7]) {
                    despixels[k] = -1;
                }
                else {
                    despixels[k] = -16777216;
                }
                ++k;
            }
        }
    }
    
    public static int[] bitmapToBWPix_int(final Bitmap mBitmap, final int algorithm) {
        int[] pixels = new int[0];
        switch (algorithm) {
            case 8: {
                final Bitmap grayBitmap = toGrayscale(mBitmap);
                pixels = new int[grayBitmap.getWidth() * grayBitmap.getHeight()];
                grayBitmap.getPixels(pixels, 0, grayBitmap.getWidth(), 0, 0, grayBitmap.getWidth(), grayBitmap.getHeight());
                format_K_dither8x8_int(pixels, grayBitmap.getWidth(), grayBitmap.getHeight(), pixels);
                break;
            }
            case 2: {
                break;
            }
            default: {
                final Bitmap grayBitmap = toGrayscale(mBitmap);
                pixels = new int[grayBitmap.getWidth() * grayBitmap.getHeight()];
                grayBitmap.getPixels(pixels, 0, grayBitmap.getWidth(), 0, 0, grayBitmap.getWidth(), grayBitmap.getHeight());
                format_K_dither16x16_int(pixels, grayBitmap.getWidth(), grayBitmap.getHeight(), pixels);
                break;
            }
        }
        return pixels;
    }
    
    public static Bitmap toBinaryImage(final Bitmap mBitmap, final int nWidth, final int algorithm) {
        final int width = (nWidth + 7) / 8 * 8;
        final int height = mBitmap.getHeight() * width / mBitmap.getWidth();
        final Bitmap rszBitmap = resizeImage(mBitmap, width, height);
        final int[] pixels = bitmapToBWPix_int(rszBitmap, algorithm);
        rszBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return rszBitmap;
    }
    
    private static int getCloseColor(final int tr, final int tg, final int tb) {
        int minDistanceSquared = 195076;
        int bestIndex = 0;
        for (int i = 0; i < GpUtils.COLOR_PALETTE.length; ++i) {
            final int rdiff = tr - GpUtils.COLOR_PALETTE[i][0];
            final int gdiff = tg - GpUtils.COLOR_PALETTE[i][1];
            final int bdiff = tb - GpUtils.COLOR_PALETTE[i][2];
            final int distanceSquared = rdiff * rdiff + gdiff * gdiff + bdiff * bdiff;
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                bestIndex = i;
            }
        }
        return bestIndex;
    }
    
    private static void setPixel(final int[] input, final int width, final int height, int col, int row, final int[] p) {
        if (col < 0 || col >= width) {
            col = 0;
        }
        if (row < 0 || row >= height) {
            row = 0;
        }
        final int index = row * width + col;
        input[index] = (0xFF000000 | clamp(p[0]) << 16 | clamp(p[1]) << 8 | clamp(p[2]));
    }
    
    private static int[] getPixel(final int[] input, final int width, final int height, int col, int row, final float error, final int[] ergb) {
        if (col < 0 || col >= width) {
            col = 0;
        }
        if (row < 0 || row >= height) {
            row = 0;
        }
        final int index = row * width + col;
        int tr = input[index] >> 16 & 0xFF;
        int tg = input[index] >> 8 & 0xFF;
        int tb = input[index] & 0xFF;
        tr += (int)(error * ergb[0]);
        tg += (int)(error * ergb[1]);
        tb += (int)(error * ergb[2]);
        return new int[] { tr, tg, tb };
    }
    
    public static int clamp(final int value) {
        return (value > 255) ? 255 : ((value < 0) ? 0 : value);
    }
    
    public static Bitmap filter(final Bitmap nbm, final int width, final int height) {
        final int[] inPixels = new int[width * height];
        nbm.getPixels(inPixels, 0, width, 0, 0, width, height);
        final int[] outPixels = new int[inPixels.length];
        int index = 0;
        for (int row = 0; row < height; ++row) {
            for (int col = 0; col < width; ++col) {
                index = row * width + col;
                final int r1 = inPixels[index] >> 16 & 0xFF;
                final int g1 = inPixels[index] >> 8 & 0xFF;
                final int b1 = inPixels[index] & 0xFF;
                final int cIndex = getCloseColor(r1, g1, b1);
                outPixels[index] = (0xFF000000 | GpUtils.COLOR_PALETTE[cIndex][0] << 16 | GpUtils.COLOR_PALETTE[cIndex][1] << 8 | GpUtils.COLOR_PALETTE[cIndex][2]);
                final int[] ergb = { r1 - GpUtils.COLOR_PALETTE[cIndex][0], g1 - GpUtils.COLOR_PALETTE[cIndex][1], b1 - GpUtils.COLOR_PALETTE[cIndex][2] };
                if (GpUtils.method == 1) {
                    final float e1 = 0.4375f;
                    final float e2 = 0.3125f;
                    final float e3 = 0.1875f;
                    final float e4 = 0.0625f;
                    final int[] rgb1 = getPixel(inPixels, width, height, col + 1, row, e1, ergb);
                    final int[] rgb2 = getPixel(inPixels, width, height, col, row + 1, e2, ergb);
                    final int[] rgb3 = getPixel(inPixels, width, height, col - 1, row + 1, e3, ergb);
                    final int[] rgb4 = getPixel(inPixels, width, height, col + 1, row + 1, e4, ergb);
                    setPixel(inPixels, width, height, col + 1, row, rgb1);
                    setPixel(inPixels, width, height, col, row + 1, rgb2);
                    setPixel(inPixels, width, height, col - 1, row + 1, rgb3);
                    setPixel(inPixels, width, height, col + 1, row + 1, rgb4);
                }
                else {
                    if (GpUtils.method != 2) {
                        throw new IllegalArgumentException("Not Supported Dither Mothed!!");
                    }
                    final float e1 = 0.125f;
                    final int[] rgb5 = getPixel(inPixels, width, height, col + 1, row, e1, ergb);
                    final int[] rgb6 = getPixel(inPixels, width, height, col + 2, row, e1, ergb);
                    final int[] rgb7 = getPixel(inPixels, width, height, col - 1, row + 1, e1, ergb);
                    final int[] rgb8 = getPixel(inPixels, width, height, col, row + 1, e1, ergb);
                    final int[] rgb9 = getPixel(inPixels, width, height, col + 1, row + 1, e1, ergb);
                    final int[] rgb10 = getPixel(inPixels, width, height, col, row + 2, e1, ergb);
                    setPixel(inPixels, width, height, col + 1, row, rgb5);
                    setPixel(inPixels, width, height, col + 2, row, rgb6);
                    setPixel(inPixels, width, height, col - 1, row + 1, rgb7);
                    setPixel(inPixels, width, height, col, row + 1, rgb8);
                    setPixel(inPixels, width, height, col + 1, row + 1, rgb9);
                    setPixel(inPixels, width, height, col, row + 2, rgb10);
                }
            }
        }
        final Bitmap bitmap = Bitmap.createBitmap(outPixels, 0, width, width, height, Bitmap.Config.RGB_565);
        return bitmap;
    }
    
    public static byte[] printEscDraw(final Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final byte[] bitbuf = new byte[width / 8];
        final byte[] imgbuf = new byte[width / 8 * height + 8];
        imgbuf[0] = 29;
        imgbuf[1] = 118;
        imgbuf[2] = 48;
        imgbuf[3] = 0;
        imgbuf[4] = (byte)(width / 8);
        imgbuf[5] = 0;
        imgbuf[6] = (byte)(height % 256);
        imgbuf[7] = (byte)(height / 256);
        int s = 7;
        for (int i = 0; i < height; ++i) {
            for (int k = 0; k < width / 8; ++k) {
                final int c0 = bitmap.getPixel(k * 8, i);
                int p0;
                if (c0 == -1) {
                    p0 = 0;
                }
                else {
                    p0 = 1;
                }
                final int c2 = bitmap.getPixel(k * 8 + 1, i);
                int p2;
                if (c2 == -1) {
                    p2 = 0;
                }
                else {
                    p2 = 1;
                }
                final int c3 = bitmap.getPixel(k * 8 + 2, i);
                int p3;
                if (c3 == -1) {
                    p3 = 0;
                }
                else {
                    p3 = 1;
                }
                final int c4 = bitmap.getPixel(k * 8 + 3, i);
                int p4;
                if (c4 == -1) {
                    p4 = 0;
                }
                else {
                    p4 = 1;
                }
                final int c5 = bitmap.getPixel(k * 8 + 4, i);
                int p5;
                if (c5 == -1) {
                    p5 = 0;
                }
                else {
                    p5 = 1;
                }
                final int c6 = bitmap.getPixel(k * 8 + 5, i);
                int p6;
                if (c6 == -1) {
                    p6 = 0;
                }
                else {
                    p6 = 1;
                }
                final int c7 = bitmap.getPixel(k * 8 + 6, i);
                int p7;
                if (c7 == -1) {
                    p7 = 0;
                }
                else {
                    p7 = 1;
                }
                final int c8 = bitmap.getPixel(k * 8 + 7, i);
                int p8;
                if (c8 == -1) {
                    p8 = 0;
                }
                else {
                    p8 = 1;
                }
                final int value = p0 * 128 + p2 * 64 + p3 * 32 + p4 * 16 + p5 * 8 + p6 * 4 + p7 * 2 + p8;
                bitbuf[k] = (byte)value;
            }
            for (int t = 0; t < width / 8; ++t) {
                ++s;
                imgbuf[s] = bitbuf[t];
            }
        }
        return imgbuf;
    }
    
    public static byte[] printTscDraw(final int x, final int y, final LabelCommand.BITMAP_MODE mode, final Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final byte[] bitbuf = new byte[width / 8];
        final String str = "BITMAP " + x + "," + y + "," + width / 8 + "," + height + "," + mode.getValue() + ",";
        byte[] strPrint = null;
        try {
            strPrint = str.getBytes("GB2312");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final byte[] imgbuf = new byte[width / 8 * height + strPrint.length + 8];
        for (int d = 0; d < strPrint.length; ++d) {
            imgbuf[d] = strPrint[d];
        }
        int s = strPrint.length - 1;
        for (int i = 0; i < height; ++i) {
            for (int k = 0; k < width / 8; ++k) {
                final int c0 = bitmap.getPixel(k * 8, i);
                int p0;
                if (c0 == -1) {
                    p0 = 1;
                }
                else {
                    p0 = 0;
                }
                final int c2 = bitmap.getPixel(k * 8 + 1, i);
                int p2;
                if (c2 == -1) {
                    p2 = 1;
                }
                else {
                    p2 = 0;
                }
                final int c3 = bitmap.getPixel(k * 8 + 2, i);
                int p3;
                if (c3 == -1) {
                    p3 = 1;
                }
                else {
                    p3 = 0;
                }
                final int c4 = bitmap.getPixel(k * 8 + 3, i);
                int p4;
                if (c4 == -1) {
                    p4 = 1;
                }
                else {
                    p4 = 0;
                }
                final int c5 = bitmap.getPixel(k * 8 + 4, i);
                int p5;
                if (c5 == -1) {
                    p5 = 1;
                }
                else {
                    p5 = 0;
                }
                final int c6 = bitmap.getPixel(k * 8 + 5, i);
                int p6;
                if (c6 == -1) {
                    p6 = 1;
                }
                else {
                    p6 = 0;
                }
                final int c7 = bitmap.getPixel(k * 8 + 6, i);
                int p7;
                if (c7 == -1) {
                    p7 = 1;
                }
                else {
                    p7 = 0;
                }
                final int c8 = bitmap.getPixel(k * 8 + 7, i);
                int p8;
                if (c8 == -1) {
                    p8 = 1;
                }
                else {
                    p8 = 0;
                }
                final int value = p0 * 128 + p2 * 64 + p3 * 32 + p4 * 16 + p5 * 8 + p6 * 4 + p7 * 2 + p8;
                bitbuf[k] = (byte)value;
            }
            for (int t = 0; t < width / 8; ++t) {
                ++s;
                imgbuf[s] = bitbuf[t];
            }
        }
        return imgbuf;
    }
    
    static String splitArabic(final String input) {
        final StringBuilder sb = new StringBuilder(256);
        final String[] arabics = input.split("\\n");
        if (arabics.length == 1 && arabics[0].length() > GpUtils.sPaperWidth) {
            final int insertWrapNumber = arabics[0].length() / GpUtils.sPaperWidth;
            int i = 1;
            int j = 0;
            while (i <= insertWrapNumber) {
                sb.append(arabics[0].substring(j, GpUtils.sPaperWidth * i));
                j += GpUtils.sPaperWidth;
                ++i;
            }
            if (sb.length() >= 0) {
                sb.append('\n');
            }
            final int lastArabic = arabics[0].length() % GpUtils.sPaperWidth;
            sb.append(arabics[0].substring(arabics[0].length() - lastArabic, arabics[0].length()));
            return splitArabic(sb.toString());
        }
        for (int k = 0; k < arabics.length; ++k) {
            final int childStringLength = arabics[k].length();
            if (childStringLength > GpUtils.sPaperWidth) {
                sb.append(splitArabic(arabics[k]));
            }
            else {
                sb.append(addSpaceAfterArabicString(arabics[k], GpUtils.sPaperWidth - childStringLength));
            }
        }
        return sb.toString();
    }
    
    static String addSpaceAfterArabicString(final String arabic, final int number) {
        final StringBuilder sb = new StringBuilder(65);
        sb.append(arabic);
        for (int i = 0; i < number; ++i) {
            sb.append(' ');
        }
        sb.append('\n');
        return sb.toString();
    }
    
    static String reverseLetterAndNumber(final String input) {
        final StringBuilder sb = new StringBuilder(input);
        final Matcher matcher = GpUtils.pattern.matcher(input);
        while (matcher.find()) {
            final String matcherString = matcher.group();
            final int matcherStart = matcher.start();
            final int matcherEnd = matcher.end();
            sb.replace(matcherStart, matcherEnd, new StringBuilder(matcherString).reverse().toString());
        }
        return sb.toString();
    }
    
    static byte[] string2Cp864(final String arabicString) {
        final Integer[] originUnicode = new Integer[arabicString.length()];
        final Integer[] outputUnicode = new Integer[arabicString.length()];
        final Integer[] outputChars = new Integer[originUnicode.length];
        copy(arabicString.toCharArray(), originUnicode, arabicString.length());
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(originUnicode));
        list = Hyphen(list);
        list = Deformation(list);
        Collections.reverse(list);
        list.toArray(outputUnicode);
        final char[] chs = integer2Character(outputUnicode);
        byte[] cp864bytes = new byte[0];
        try {
            cp864bytes = new String(chs).getBytes("cp864");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cp864bytes;
    }
    
    static char[] integer2Character(final Integer[] integers) {
        final char[] chs = new char[integers.length];
        for (int i = 0; i < integers.length; ++i) {
            if (integers[i] != null) {
                chs[i] = (char)(int)integers[i];
            }
            else {
                chs[i] = ' ';
            }
        }
        return chs;
    }
    
    static void copy(final char[] array, final Integer[] originUnicode, final int length) {
        for (int i = 0; i < length; ++i) {
            originUnicode[i] = (int)array[i];
        }
    }
    
    static List<Integer> Hyphen(final List<Integer> list) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) == 1604) {
                switch (list.get(i + 1)) {
                    case 1570: {
                        list.set(i, 17442);
                        list.remove(i + 1);
                        break;
                    }
                    case 1571: {
                        list.set(i, 17443);
                        list.remove(i + 1);
                        break;
                    }
                    case 1573: {
                        list.set(i, 17445);
                        list.remove(i + 1);
                        break;
                    }
                    case 1575: {
                        list.set(i, 17447);
                        list.remove(i + 1);
                        break;
                    }
                }
            }
        }
        return list;
    }
    
    static List<Integer> Deformation(final List<Integer> inputlist) {
        int flag = 0;
        final List<Integer> outputlist = new ArrayList<Integer>();
        final Map<Integer, Integer[]> formHashTable = new HashMap<Integer, Integer[]>(40);
        for (int i = 0; i < 40; ++i) {
            formHashTable.put(GpUtils.theSet0[i], GpUtils.FormatTable[i]);
        }
        for (int i = 0; i < inputlist.size(); ++i) {
            if (compare(inputlist.get(i), 0)) {
                if (i == 0) {
                    final boolean inSet1 = false;
                    final boolean inSet2 = compare(inputlist.get(i + 1), 2);
                    flag = Flag(inSet1, inSet2);
                }
                else if (i == inputlist.size() - 1) {
                    final boolean inSet1 = compare(inputlist.get(i - 1), 1);
                    final boolean inSet2 = false;
                    flag = Flag(inSet1, inSet2);
                }
                else {
                    final boolean inSet1 = compare(inputlist.get(i - 1), 1);
                    final boolean inSet2 = compare(inputlist.get(i + 1), 2);
                    flag = Flag(inSet1, inSet2);
                }
                final Integer[] a = formHashTable.get(inputlist.get(i));
                outputlist.add(a[flag]);
            }
            else {
                outputlist.add(inputlist.get(i));
            }
        }
        return outputlist;
    }
    
    static boolean compare(final Integer input, final int i) {
        final List<Integer[]> list = new ArrayList<Integer[]>(3);
        list.add(GpUtils.theSet0);
        list.add(GpUtils.theSet1);
        list.add(GpUtils.theSet2);
        return findInArray(list.get(i), input);
    }
    
    static boolean findInArray(final Integer[] integer, final int input) {
        for (int j = 0; j < integer.length; ++j) {
            if (integer[j] == input) {
                return true;
            }
        }
        return false;
    }
    
    static int Flag(final boolean set1, final boolean set2) {
        if (set1 && set2) {
            return 3;
        }
        if (!set1 && set2) {
            return 2;
        }
        if (set1 && !set2) {
            return 1;
        }
        return 0;
    }
    
    public static void setPaperWidth(final int paperWidth) {
        GpUtils.sPaperWidth = paperWidth;
    }
    
    public static byte[] ByteTo_byte(final Vector<Byte> vector) {
        final int len = vector.size();
        final byte[] data = new byte[len];
        for (int i = 0; i < len; ++i) {
            data[i] = vector.get(i);
        }
        return data;
    }
    
    public int getMethod() {
        return GpUtils.method;
    }
    
    public static void setMethod(final int method) {
        GpUtils.method = method;
    }
    
    static {
        GpUtils.pattern = Pattern.compile("([a-zA-Z0-9!@#$^&*\\(\\)~\\{\\}:\",\\.<>/]+)");
        GpUtils.p0 = new int[] { 0, 128 };
        GpUtils.p1 = new int[] { 0, 64 };
        GpUtils.p2 = new int[] { 0, 32 };
        GpUtils.p3 = new int[] { 0, 16 };
        GpUtils.p4 = new int[] { 0, 8 };
        GpUtils.p5 = new int[] { 0, 4 };
        GpUtils.p6 = new int[] { 0, 2 };
        GpUtils.Floyd16x16 = new int[][] { { 0, 128, 32, 160, 8, 136, 40, 168, 2, 130, 34, 162, 10, 138, 42, 170 }, { 192, 64, 224, 96, 200, 72, 232, 104, 194, 66, 226, 98, 202, 74, 234, 106 }, { 48, 176, 16, 144, 56, 184, 24, 152, 50, 178, 18, 146, 58, 186, 26, 154 }, { 240, 112, 208, 80, 248, 120, 216, 88, 242, 114, 210, 82, 250, 122, 218, 90 }, { 12, 140, 44, 172, 4, 132, 36, 164, 14, 142, 46, 174, 6, 134, 38, 166 }, { 204, 76, 236, 108, 196, 68, 228, 100, 206, 78, 238, 110, 198, 70, 230, 102 }, { 60, 188, 28, 156, 52, 180, 20, 148, 62, 190, 30, 158, 54, 182, 22, 150 }, { 252, 124, 220, 92, 244, 116, 212, 84, 254, 126, 222, 94, 246, 118, 214, 86 }, { 3, 131, 35, 163, 11, 139, 43, 171, 1, 129, 33, 161, 9, 137, 41, 169 }, { 195, 67, 227, 99, 203, 75, 235, 107, 193, 65, 225, 97, 201, 73, 233, 105 }, { 51, 179, 19, 147, 59, 187, 27, 155, 49, 177, 17, 145, 57, 185, 25, 153 }, { 243, 115, 211, 83, 251, 123, 219, 91, 241, 113, 209, 81, 249, 121, 217, 89 }, { 15, 143, 47, 175, 7, 135, 39, 167, 13, 141, 45, 173, 5, 133, 37, 165 }, { 207, 79, 239, 111, 199, 71, 231, 103, 205, 77, 237, 109, 197, 69, 229, 101 }, { 63, 191, 31, 159, 55, 183, 23, 151, 61, 189, 29, 157, 53, 181, 21, 149 }, { 254, 127, 223, 95, 247, 119, 215, 87, 253, 125, 221, 93, 245, 117, 213, 85 } };
        GpUtils.Floyd8x8 = new int[][] { { 0, 32, 8, 40, 2, 34, 10, 42 }, { 48, 16, 56, 24, 50, 18, 58, 26 }, { 12, 44, 4, 36, 14, 46, 6, 38 }, { 60, 28, 52, 20, 62, 30, 54, 22 }, { 3, 35, 11, 43, 1, 33, 9, 41 }, { 51, 19, 59, 27, 49, 17, 57, 25 }, { 15, 47, 7, 39, 13, 45, 5, 37 }, { 63, 31, 55, 23, 61, 29, 53, 21 } };
        GpUtils.sPaperWidth = 48;
        GpUtils.theSet0 = new Integer[] { 1569, 1570, 1571, 1572, 1573, 1574, 1575, 1576, 1577, 1578, 1579, 1580, 1581, 1582, 1583, 1584, 1585, 1586, 1587, 1588, 1589, 1590, 1591, 1592, 1593, 1594, 1601, 1602, 1603, 1604, 1605, 1606, 1607, 1608, 1609, 1610, 17442, 17443, 17445, 17447 };
        GpUtils.FormatTable = new Integer[][] { { 65152, 65152, 65152, 65152 }, { 65153, 65154, 65153, 65154 }, { 65155, 65156, 65155, 65156 }, { 65157, 65157, 65157, 65157 }, { 65149, 65149, 65149, 65149 }, { 65163, 65163, 65163, 65163 }, { 65165, 65166, 65165, 65166 }, { 65167, 65167, 65169, 65169 }, { 65171, 65171, 65171, 65171 }, { 65173, 65173, 65175, 65175 }, { 65177, 65177, 65179, 65179 }, { 65181, 65181, 65183, 65183 }, { 65185, 65185, 65187, 65187 }, { 65189, 65189, 65191, 65191 }, { 65193, 65193, 65193, 65193 }, { 65195, 65195, 65195, 65195 }, { 65197, 65197, 65197, 65197 }, { 65199, 65199, 65199, 65199 }, { 65201, 65201, 65203, 65203 }, { 65205, 65205, 65207, 65207 }, { 65209, 65209, 65211, 65211 }, { 65213, 65213, 65215, 65215 }, { 65217, 65217, 65217, 65217 }, { 65221, 65221, 65221, 65221 }, { 65225, 65226, 65227, 65228 }, { 65229, 65230, 65231, 65232 }, { 65233, 65233, 65235, 65235 }, { 65237, 65237, 65239, 65239 }, { 65241, 65241, 65243, 65243 }, { 65245, 65245, 65247, 65247 }, { 65249, 65249, 65251, 65251 }, { 65253, 65253, 65255, 65255 }, { 65257, 65257, 65259, 65259 }, { 65261, 65261, 65261, 65261 }, { 65263, 65264, 65263, 65264 }, { 65265, 65266, 65267, 65267 }, { 65269, 65270, 65269, 65270 }, { 65271, 65272, 65271, 65272 }, { 65273, 65274, 65273, 65274 }, { 65275, 65276, 65275, 65276 } };
        GpUtils.theSet1 = new Integer[] { 1574, 1576, 1578, 1579, 1580, 1581, 1582, 1587, 1588, 1589, 1590, 1591, 1592, 1593, 1594, 1600, 1601, 1602, 1603, 1604, 1605, 1606, 1607, 1610 };
        GpUtils.theSet2 = new Integer[] { 1570, 1571, 1572, 1573, 1574, 1575, 1576, 1577, 1578, 1579, 1580, 1581, 1582, 1583, 1584, 1585, 1586, 1587, 1588, 1589, 1590, 1591, 1592, 1593, 1594, 1600, 1601, 1602, 1603, 1604, 1605, 1606, 1607, 1608, 1609, 1610 };
        COLOR_PALETTE = new int[][] { { 0, 0, 0 }, { 255, 255, 255 } };
        GpUtils.method = 1;
    }
}
