package com.ailiwean.core.zxing;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;

import com.ailiwean.core.zxing.core.InvertedLuminanceSource;
import com.ailiwean.core.zxing.core.LuminanceSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Package: com.ailiwean.core.zxing
 * @ClassName: GammeStrengSource
 * @Description: 浅灰色处理
 * @Author: SWY
 * @CreateDate: 2020/8/7 6:07 PM
 */
public class LightGreySource extends LuminanceSource {

    private final LuminanceSource delegate;

    public LightGreySource(LuminanceSource delegate) {
        super(delegate.getWidth(), delegate.getHeight());
        this.delegate = delegate;
    }

    static long startTime = System.currentTimeMillis();

    public static int stepX = 2;
    public static int stepY = 2;

    public synchronized void toPut(byte[] nv21, int w, int h) {

        if (System.currentTimeMillis() - startTime < 5000) {
            return;
        }

        startTime = System.currentTimeMillis();


        File file = new File(Environment.getExternalStorageDirectory(), "YUV_处理前.jpg");
        File file0 = new File(Environment.getExternalStorageDirectory(), "YUV_处理后.jpg");

        if (file.exists()) {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (file0.exists()) {
            file.delete();
            try {
                file0.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] cropNv21 = new byte[nv21.length / 2 * 3];

        for (int i = 0; i < w * h; i++) {
            cropNv21[i] = nv21[i];
        }
        for (int i = 0; i < w * h / 2; i++) {
            if (nv21.length + i < cropNv21.length)
                cropNv21[nv21.length + i] = (byte) 0b10000001;
        }

        System.arraycopy(cropNv21, 0, cropNv21, 0, w * h);

        YuvImage yuvImage = new YuvImage(cropNv21, ImageFormat.NV21, w, h, null);

        try {
            yuvImage.compressToJpeg(new Rect(0, 0, w, h), 100, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//            int offset = (int) (Math.random() * 100);
//            for (int i = 0; i < w; i++) {
//                for (int j = 0; j < w; j++) {
//                    if (i + offset < w && j + offset < w)
//                        cropNv21[i * w + j] = cropNv21[(i + offset) * w + j + offset];
//                    else cropNv21[i * w + j] = (byte) 255;
//                }
//            }

        for (int step_h = 0; step_h + stepX < h; step_h += stepY) {
            for (int step_w = 0; step_w + stepX < w; step_w += stepX) {
                int count = 0;
                int avage = 0;
                int min = Integer.MAX_VALUE;
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        if ((cropNv21[y_ * w + x_] & 0xff) < 130)
                            count++;
                        avage += cropNv21[y_ * w + x_] & 0xff;

                        if (min > (cropNv21[y_ * w + x_] & 0xff))
                            min = cropNv21[y_ * w + x_] & 0xff;
                    }
                }
                if (count == 0) {
                    continue;
                }
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        cropNv21[y_ * w + x_] = (byte) (min / 3 * 2);
                    }
                }
            }
        }

//            for (int i = 0; i < w * h; i++)
//                cropNv21[i] = (byte) (byte) (255 * Math.pow((cropNv21[i] & 0xff) / 255f, 0.8f));


//            for (int step_h = 0; step_h + stepX < h; step_h += stepY) {
//                for (int step_w = 0; step_w + stepX < w; step_w += stepX) {
//                    int avage = 0;
//                    for (int y = step_h; y < step_h + stepY; y++) {
//                        for (int x = step_w; x < step_w + stepX; x++) {
//                            avage += cropNv21[y * w + x] & 0xff;
//                        }
//                    }
//                    avage /= stepY * stepX;
//                    for (int y = step_h; y < step_h + stepY; y++) {
//                        for (int x = step_w; x < step_w + stepX; x++) {
//
//                            if ((cropNv21[y * w + x] & 0xff) > avage * 0.95f) {
//                                cropNv21[y * w + x] = (byte) 250;
//                            } else cropNv21[y * w + x] = (byte) 5;
//                        }
//                    }
//                }
//            }

//            for (int step_h = 0; step_h + stepX < h; step_h += stepY) {
//                for (int step_w = 0; step_w + stepX < w; step_w += stepX) {
//                    int avage = 0;
//                    for (int y = step_h; y < step_h + stepY; y++) {
//                        for (int x = step_w; x < step_w + stepX; x++) {
//                            avage += cropNv21[y * w + x] & 0xff;
//                        }
//                    }
//                    avage /= stepY * stepX;
//                    for (int y = step_h; y < step_h + stepY; y++) {
//                        for (int x = step_w; x < step_w + stepX; x++) {
//
//                            if ((cropNv21[y * w + x] & 0xff) > avage * 0.95f) {
//                                cropNv21[y * w + x] = (byte) 250;
//                            } else cropNv21[y * w + x] = (byte) 5;
//                        }
//                    }
//                }
//            }

        YuvImage yuvImage2 = new YuvImage(cropNv21, ImageFormat.NV21, w, h, null);
        try {
            yuvImage2.compressToJpeg(new Rect(0, 0, w, h), 100, new FileOutputStream(file0));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        row = delegate.getRow(y, row).clone();
        int width = getWidth();
        for (int i = 0; i < width; i++) {
            row[i] = (byte) (row[i] * 2f);
        }
        return row;
    }

    @Override
    public byte[] getMatrix() {
        int length = getWidth() * getHeight();
        toPut(delegate.getMatrix(), getWidth(), getHeight());
        byte[] lightGrey = delegate.getMatrix().clone();
        for (int i = 0; i < length; i++) {
            lightGrey[i] = (byte) (lightGrey[i] * 2f);
        }
        return lightGrey;
    }

    @Override
    public boolean isCropSupported() {
        return delegate.isCropSupported();
    }

    @Override
    public LuminanceSource crop(int left, int top, int width, int height) {
        return new InvertedLuminanceSource(delegate.crop(left, top, width, height));
    }

    @Override
    public boolean isRotateSupported() {
        return delegate.isRotateSupported();
    }

    /**
     * @return original delegate {@link LuminanceSource} since invert undoes itself
     */
    @Override
    public LuminanceSource invert() {
        return delegate;
    }

    @Override
    public LuminanceSource rotateCounterClockwise() {
        return new InvertedLuminanceSource(delegate.rotateCounterClockwise());
    }

    @Override
    public LuminanceSource rotateCounterClockwise45() {
        return new InvertedLuminanceSource(delegate.rotateCounterClockwise45());
    }


}
