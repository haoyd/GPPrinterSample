// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.io.utils;

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

public class GpUtils
{
    private static int[] p0;
    private static int[] p1;
    private static int[] p2;
    private static int[] p3;
    private static int[] p4;
    private static int[] p5;
    private static int[] p6;
    private static int[][] Floyd16x16;
    private static int[][] Floyd8x8;
    public static final int ALGORITHM_DITHER_16x16 = 16;
    public static final int ALGORITHM_DITHER_8x8 = 8;
    public static final int ALGORITHM_TEXTMODE = 2;
    public static final int ALGORITHM_GRAYTEXTMODE = 1;
    
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
    
    public static byte[] pixToTscCmd(final byte[] src) {
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
    
    public static byte[] ByteTo_byte(final Byte[] bytes) {
        final byte[] data = new byte[bytes.length];
        for (int len = bytes.length, i = 0; i < len; ++i) {
            data[i] = bytes[i];
        }
        return data;
    }
    
    static {
        GpUtils.p0 = new int[] { 0, 128 };
        GpUtils.p1 = new int[] { 0, 64 };
        GpUtils.p2 = new int[] { 0, 32 };
        GpUtils.p3 = new int[] { 0, 16 };
        GpUtils.p4 = new int[] { 0, 8 };
        GpUtils.p5 = new int[] { 0, 4 };
        GpUtils.p6 = new int[] { 0, 2 };
        GpUtils.Floyd16x16 = new int[][] { { 0, 128, 32, 160, 8, 136, 40, 168, 2, 130, 34, 162, 10, 138, 42, 170 }, { 192, 64, 224, 96, 200, 72, 232, 104, 194, 66, 226, 98, 202, 74, 234, 106 }, { 48, 176, 16, 144, 56, 184, 24, 152, 50, 178, 18, 146, 58, 186, 26, 154 }, { 240, 112, 208, 80, 248, 120, 216, 88, 242, 114, 210, 82, 250, 122, 218, 90 }, { 12, 140, 44, 172, 4, 132, 36, 164, 14, 142, 46, 174, 6, 134, 38, 166 }, { 204, 76, 236, 108, 196, 68, 228, 100, 206, 78, 238, 110, 198, 70, 230, 102 }, { 60, 188, 28, 156, 52, 180, 20, 148, 62, 190, 30, 158, 54, 182, 22, 150 }, { 252, 124, 220, 92, 244, 116, 212, 84, 254, 126, 222, 94, 246, 118, 214, 86 }, { 3, 131, 35, 163, 11, 139, 43, 171, 1, 129, 33, 161, 9, 137, 41, 169 }, { 195, 67, 227, 99, 203, 75, 235, 107, 193, 65, 225, 97, 201, 73, 233, 105 }, { 51, 179, 19, 147, 59, 187, 27, 155, 49, 177, 17, 145, 57, 185, 25, 153 }, { 243, 115, 211, 83, 251, 123, 219, 91, 241, 113, 209, 81, 249, 121, 217, 89 }, { 15, 143, 47, 175, 7, 135, 39, 167, 13, 141, 45, 173, 5, 133, 37, 165 }, { 207, 79, 239, 111, 199, 71, 231, 103, 205, 77, 237, 109, 197, 69, 229, 101 }, { 63, 191, 31, 159, 55, 183, 23, 151, 61, 189, 29, 157, 53, 181, 21, 149 }, { 254, 127, 223, 95, 247, 119, 215, 87, 253, 125, 221, 93, 245, 117, 213, 85 } };
        GpUtils.Floyd8x8 = new int[][] { { 0, 32, 8, 40, 2, 34, 10, 42 }, { 48, 16, 56, 24, 50, 18, 58, 26 }, { 12, 44, 4, 36, 14, 46, 6, 38 }, { 60, 28, 52, 20, 62, 30, 54, 22 }, { 3, 35, 11, 43, 1, 33, 9, 41 }, { 51, 19, 59, 27, 49, 17, 57, 25 }, { 15, 47, 7, 39, 13, 45, 5, 37 }, { 63, 31, 55, 23, 61, 29, 53, 21 } };
    }
}
