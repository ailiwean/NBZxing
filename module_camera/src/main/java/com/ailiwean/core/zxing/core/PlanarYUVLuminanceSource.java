/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ailiwean.core.zxing.core;

import android.text.TextUtils;
import android.util.Log;

import com.ailiwean.core.zxing.core.common.HybridBinarizer;
import com.ailiwean.core.zxing.core.common.HybridBinarizerCrude;

/**
 * This object extends LuminanceSource around an array of YUV data returned from the camera driver,
 * with the option to crop to a rectangle within the full data. This can be used to exclude
 * superfluous pixels around the perimeter and speed up decoding.
 * <p>
 * It works for any pixel format where the Y channel is planar and appears first, including
 * YCbCr_420_SP and YCbCr_422_SP.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class PlanarYUVLuminanceSource extends LuminanceSource {

    private byte[] matrix;
    private int dataWidth;
    private int dataHeight;
    private int left;
    private int top;

    public PlanarYUVLuminanceSource(byte[] yuvData,
                                    int dataWidth,
                                    int dataHeight,
                                    int left,
                                    int top,
                                    int width,
                                    int height) {
        super(width, height);

        if (left + width > dataWidth || top + height > dataHeight) {
            this.dataHeight = 0;
            this.dataWidth = 0;
            this.left = 0;
            this.top = 0;
            return;
        }
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        this.left = left;
        this.top = top;
        matrix = getGlobeMatrix(yuvData);
    }


    public PlanarYUVLuminanceSource(
            byte[] matrix,
            int width,
            int height) {
        super(width, height);
        this.matrix = matrix;
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        if (y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        int width = getWidth();
        if (row == null || row.length < width) {
            row = new byte[width];
        }
        int offset = y * width;
        System.arraycopy(matrix, offset, row, 0, width);
        return row;
    }

    public byte[] getGlobeMatrix(byte[] yuvData) {
        int width = getWidth();
        int height = getHeight();

        // If the caller asks for the entire underlying image, save the copy and give them the
        // original data. The docs specifically warn that result.length must be ignored.
        if (width == dataWidth && height == dataHeight) {
            return yuvData;
        }

        int area = width * height;
        byte[] matrix = new byte[area];
        int inputOffset = top * dataWidth + left;

        // If the width matches the full width of the underlying data, perform a single copy.
        if (width == dataWidth) {
            System.arraycopy(yuvData, inputOffset, matrix, 0, area);
            return matrix;
        }

        // Otherwise copy one cropped row at a time.
        for (int y = 0; y < height; y++) {
            int outputOffset = y * width;
            System.arraycopy(yuvData, inputOffset, matrix, outputOffset, width);
            inputOffset += dataWidth;
        }
        return matrix;
    }

    @Override
    public byte[] getMatrix() {
        return matrix;
    }

    @Override
    public boolean isCropSupported() {
        return true;
    }

    @Override
    public LuminanceSource crop(int left, int top, int width, int height) {
        return this;
    }

    Binarizer hybridBinary;
    Binarizer hybridBinaryCurde;

    public Binarizer getHybridBinary() {
        if (hybridBinary == null) {
            hybridBinary = new HybridBinarizer(this);
        }
        return hybridBinary;
    }

    public Binarizer getHybridBinaryCurde() {
        if (hybridBinaryCurde == null) {
            hybridBinaryCurde = new HybridBinarizerCrude(this);
        }
        return hybridBinaryCurde;
    }

    /***
     * 拷贝所有
     * @return
     */
    public PlanarYUVLuminanceSource copyAll() {
        return new PlanarYUVLuminanceSource(matrix.clone(), getWidth(), getHeight());
    }

    /***
     * 仅拷贝byte[]封装
     * @return
     */
    public PlanarYUVLuminanceSourceRotate onlyCopyWarpRotate() {
        return new PlanarYUVLuminanceSourceRotate(matrix, getWidth(), getHeight());
    }

    String tagId;

    public String getTagId() {
        if (TextUtils.isEmpty(tagId))
            tagId = System.currentTimeMillis() + "";
        return tagId;
    }
}
