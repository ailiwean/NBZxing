package com.ailiwean.module_grayscale;

import android.graphics.Rect;

import java.util.HashSet;
import java.util.Set;

/**
 * @Package: com.ailiwean.module_grayscale
 * @ClassName: InterruptScale
 * @Description: 开闭操作
 * @Author: SWY
 * @CreateDate: 2020/9/12 10:14 PM
 */
class InterruptGrayScale_ implements Dispatch {

    //结构元素步长
    private int stepX = 10;
    private int stepY = 10;

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        byte[] newByte = data.clone();
        for (int step_h = 0; step_h + stepY < height; step_h += stepY) {
            for (int step_w = 0; step_w + stepX < width; step_w += stepX) {
                int count = 0;
                int avage = 0;
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        if ((newByte[y_ * width + x_] & 0xff) < 130)
                            count++;
                        avage += newByte[y_ * width + x_] & 0xff;
                    }
                }
                if (count == 0) {
                    continue;
                }
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        if ((newByte[y_ * width + x_] & 0xff) > avage)
                            newByte[y_ * width + x_] = (byte) ((avage & 0xff));
                        else newByte[y_ * width + x_] = (byte) (newByte[y_ * width + x_] & 0xff);
                    }
                }
            }
        }
        return newByte;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height, Rect rect) {
        byte[] newByte = data.clone();

//        int offset = (int) (Math.random() * 3);
//        for (int i = 0; i < offset; i++) {
//            openOp(newByte, width, rect, i);
//        }
//        for (int i = 0; i < offset; i++) {
//            closeOp(newByte, width, rect, i);
//        }

        byte[] binarizationByte = new byte[rect.width() * rect.height()];

        int linkIndex = 0;
        for (int start_h = rect.top; start_h < rect.bottom; start_h++) {
            for (int start_w = rect.left; start_w < rect.right; start_w++) {
                int index = start_h * width + start_w;
                binarizationByte[linkIndex++] = newByte[index];
            }
        }
        linkIndex = 0;
        toBinarization(binarizationByte, rect.width(), rect.height());

        for (int i = 0; i < 5; i++) {
            openOp(binarizationByte, rect.width(), rect.height(), 0);
            closeOp(binarizationByte, rect.width(), rect.height(), 0);
        }

        for (int start_h = rect.top; start_h < rect.bottom; start_h++) {
            for (int start_w = rect.left; start_w < rect.right; start_w++) {
                int index = start_h * width + start_w;
                newByte[index] = binarizationByte[linkIndex++];
            }
        }


//        stepX = 2;
//        stepY = 2;
//        setA.clear();
//        for (int step_h = offset; step_h + stepY < height; step_h += 1) {
//            for (int step_w = offset; step_w + stepX < width; step_w += 1) {
//                int count = 0;
//                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
//                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
//
//                        if (y_ * width + x_ >= width * height)
//                            continue;
//
//                        if ((newByte[y_ * width + x_] & 0xff) == 255)
//                            count++;
//                    }
//                }
//                if (count == 0) {
//                    continue;
//                }
//
//                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
//                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
//                        setA.add(y_ * width + x_);
//                    }
//                }
//            }
//        }
//
////        for (Integer integer : setA) {
////            newByte[integer] = (byte) 255;
////        }


        return newByte;
    }

    private void openOp(byte[] newByte, int width, int height, int offset) {

        Set<Integer> set = new HashSet<>();

        for (int step_h = offset; step_h + stepY < height; step_h += stepY) {
            for (int step_w = offset; step_w + stepX < width; step_w += stepX) {
                int count = 0;
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {

                        if (y_ * width + x_ >= width * height)
                            return;

                        if ((newByte[y_ * width + x_] & 0xff) == 0)
                            count++;
                    }
                }
                if (count == 0) {
                    continue;
                }
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        set.add(y_ * width + x_);
//                        newByte[y_ * width + x_] = 0;
                    }
                }
            }
        }

        for (int index : set) {
            newByte[index] = 0;
        }

    }

    private void closeOp(byte[] newByte, int width, int height, int offset) {


        Set<Integer> set = new HashSet<>();

        for (int step_h = offset; step_h + stepY < height; step_h += stepY) {
            for (int step_w = offset; step_w + stepX < width; step_w += stepX) {
                int count = 0;
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {


                        if (y_ * width + x_ >= width * height)
                            return;

                        if ((newByte[y_ * width + x_] & 0xff) == 255)
                            count++;
                    }
                }
                if (count == 0) {
                    continue;
                }
                for (int y_ = step_h; y_ < step_h + stepY; y_++) {
                    for (int x_ = step_w; x_ < step_w + stepX; x_++) {
                        set.add(y_ * width + x_);
                    }
                }
            }
        }

        for (int index : set) {
            newByte[index] = (byte) 255;
        }

    }


    // This class uses 5x5 blocks to compute local luminance, where each block is 8x8 pixels.
    // So this is the smallest dimension in each axis we can accept.
    private static final int BLOCK_SIZE_POWER = 2;
    private static final int BLOCK_SIZE = 1 << BLOCK_SIZE_POWER; // ...0100...00
    private static final int BLOCK_SIZE_MASK = BLOCK_SIZE - 1;   // ...0011...11
    private static final int MINIMUM_DIMENSION = BLOCK_SIZE * 5;
    private static final int MIN_DYNAMIC_RANGE = 24;


    private void toBinarization(byte[] luminances, int width, int height) {
        int subWidth = width >> BLOCK_SIZE_POWER;
        if ((width & BLOCK_SIZE_MASK) != 0) {
            subWidth++;
        }
        int subHeight = height >> BLOCK_SIZE_POWER;
        if ((height & BLOCK_SIZE_MASK) != 0) {
            subHeight++;
        }
        int[][] blackPoints = calculateBlackPoints(luminances, subWidth, subHeight, width, height);

        calculateThresholdForBlock(luminances, subWidth, subHeight, width, height, blackPoints);
    }


    private int[][] calculateBlackPoints(byte[] luminances,
                                         int subWidth,
                                         int subHeight,
                                         int width,
                                         int height) {
        int maxYOffset = height - BLOCK_SIZE;
        int maxXOffset = width - BLOCK_SIZE;
        int[][] blackPoints = new int[subHeight][subWidth];
        for (int y = 0; y < subHeight; y++) {
            int yoffset = y << BLOCK_SIZE_POWER;
            if (yoffset > maxYOffset) {
                yoffset = maxYOffset;
            }
            for (int x = 0; x < subWidth; x++) {
                int xoffset = x << BLOCK_SIZE_POWER;
                if (xoffset > maxXOffset) {
                    xoffset = maxXOffset;
                }
                int sum = 0;
                int min = 0xFF;
                int max = 0;
                for (int yy = 0, offset = yoffset * width + xoffset; yy < BLOCK_SIZE; yy++, offset += width) {
                    for (int xx = 0; xx < BLOCK_SIZE; xx++) {
                        int pixel = luminances[offset + xx] & 0xFF;
                        sum += pixel;
                        // still looking for good contrast
                        if (pixel < min) {
                            min = pixel;
                        }
                        if (pixel > max) {
                            max = pixel;
                        }
                    }
                    // short-circuit min/max tests once dynamic range is met
                    if (max - min > MIN_DYNAMIC_RANGE) {
                        // finish the rest of the rows quickly
                        for (yy++, offset += width; yy < BLOCK_SIZE; yy++, offset += width) {
                            for (int xx = 0; xx < BLOCK_SIZE; xx++) {
                                sum += luminances[offset + xx] & 0xFF;
                            }
                        }
                    }
                }

                // The default estimate is the average of the values in the block.
                int average = sum >> (BLOCK_SIZE_POWER * 2);
                if (max - min <= MIN_DYNAMIC_RANGE) {
                    // If variation within the block is low, assume this is a block with only light or only
                    // dark pixels. In that case we do not want to use the average, as it would divide this
                    // low contrast area into black and white pixels, essentially creating data out of noise.
                    //
                    // The default assumption is that the block is light/background. Since no estimate for
                    // the level of dark pixels exists locally, use half the min for the block.
                    average = min / 2;

                    if (y > 0 && x > 0) {
                        // Correct the "white background" assumption for blocks that have neighbors by comparing
                        // the pixels in this block to the previously calculated black points. This is based on
                        // the fact that dark barcode symbology is always surrounded by some amount of light
                        // background for which reasonable black point estimates were made. The bp estimated at
                        // the boundaries is used for the interior.

                        // The (min < bp) is arbitrary but works better than other heuristics that were tried.
                        int averageNeighborBlackPoint =
                                (blackPoints[y - 1][x] + (2 * blackPoints[y][x - 1]) + blackPoints[y - 1][x - 1]) / 4;
                        if (min < averageNeighborBlackPoint) {
                            average = averageNeighborBlackPoint;
                        }
                    }
                }
                blackPoints[y][x] = average;
            }
        }
        return blackPoints;
    }


    private void calculateThresholdForBlock(byte[] luminances,
                                            int subWidth,
                                            int subHeight,
                                            int width,
                                            int height,
                                            int[][] blackPoints) {
        int maxYOffset = height - BLOCK_SIZE;
        int maxXOffset = width - BLOCK_SIZE;
        for (int y = 0; y < subHeight; y++) {
            int yoffset = y << BLOCK_SIZE_POWER;
            if (yoffset > maxYOffset) {
                yoffset = maxYOffset;
            }
            int top = cap(y, subHeight - 3);
            for (int x = 0; x < subWidth; x++) {
                int xoffset = x << BLOCK_SIZE_POWER;
                if (xoffset > maxXOffset) {
                    xoffset = maxXOffset;
                }
                int left = cap(x, subWidth - 3);
                int sum = 0;
                for (int z = -2; z <= 2; z++) {
                    int[] blackRow = blackPoints[top + z];
                    sum += blackRow[left - 2] + blackRow[left - 1] + blackRow[left] + blackRow[left + 1] + blackRow[left + 2];
                }
                int average = sum / 25;
                thresholdBlock(luminances, xoffset, yoffset, average, width);
            }
        }
    }

    private int cap(int value, int max) {
        return value < 2 ? 2 : Math.min(value, max);
    }

    private void thresholdBlock(byte[] luminances,
                                int xoffset,
                                int yoffset,
                                int threshold,
                                int stride) {
        for (int y = 0, offset = yoffset * stride + xoffset; y < BLOCK_SIZE; y++, offset += stride) {
            for (int x = 0; x < BLOCK_SIZE; x++) {
                // Comparison needs to be <= so that black == 0 pixels are black even if the threshold is 0.
                if ((luminances[offset + x] & 0xFF) <= threshold) {
                    //  matrix.set(xoffset + x, yoffset + y);
                    //luminances[(yoffset + y) * ((stride + 31) / 32) + ((xoffset + x) / 32)] = 0;
                    luminances[offset + x] = 0;
                } else luminances[offset + x] = (byte) 255;
            }
        }
    }

}
