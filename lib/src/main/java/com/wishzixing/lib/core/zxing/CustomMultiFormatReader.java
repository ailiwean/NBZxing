package com.wishzixing.lib.core.zxing;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.aztec.AztecReader;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.maxicode.MaxiCodeReader;
import com.google.zxing.oned.MultiFormatOneDReader;
import com.google.zxing.pdf417.PDF417Reader;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.config.ScanConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;


/**
 * @Description: ZXing 扫码的核心类，内部可以嵌入多种扫描模式
 * @Author: SWY
 * @Date: 2019/4/9 15:53
 */
public class CustomMultiFormatReader implements Reader {

    private Map<DecodeHintType, ?> hints;
    private Reader[] readers;

    /**
     * This version of decode honors the intent of Reader.decode(BinaryBitmap) in that it
     * passes null as a hint to the decoders. However, that makes it inefficient to call repeatedly.
     * Use setHints() followed by decodeWithState() for continuous scan applications.
     *
     * @param image The pixel data to decode
     * @return The contents of the image
     * @throws NotFoundException Any errors which occurred
     */
    @Override
    public Result decode(BinaryBitmap image) throws NotFoundException {
        setHints(null);
        return decodeInternal(image);
    }

    /**
     * Decode an image using the hints provided. Does not honor existing state.
     *
     * @param image The pixel data to decode
     * @param hints The hints to use, clearing the previous state.
     * @return The contents of the image
     * @throws NotFoundException Any errors which occurred
     */
    @Override
    public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
        setHints(hints);
        return decodeInternal(image);
    }

    /**
     * Decode an image using the state set up by calling setHints() previously. Continuous scan
     * clients will get a <b>large</b> speed increase by using this instead of decode().
     *
     * @param image The pixel data to decode
     * @return The contents of the image
     * @throws NotFoundException Any errors which occurred
     */
    public Result decodeWithState(BinaryBitmap image) throws NotFoundException {
        // Make sure to set up the default state so we don't crash
        if (readers == null) {
            setHints(null);
        }
        return decodeInternal(image);
    }

    /**
     * This method adds state to the MultiFormatReader. By setting the hints once, subsequent calls
     * to decodeWithState(image) can reuse the same set of readers without reallocating memory. This
     * is important for performance in continuous scan clients.
     *
     * @param hints The set of hints to use for subsequent calls to decode(image)
     */
    public void setHints(Map<DecodeHintType, ?> hints) {
        this.hints = hints;
        Collection<Reader> readers = new ArrayList<>();

        if (CameraConfig.getInstance().getScanModel() == ScanConfig.ALL) {
            //一维码
            readers.add(new MultiFormatOneDReader(hints));
            //二维码
            readers.add(new QRCodeCore());
            readers.add(new DataMatrixReader());
            readers.add(new AztecReader());
            readers.add(new PDF417Reader());
            readers.add(new MaxiCodeReader());
        }

        if (CameraConfig.getInstance().getScanModel() == ScanConfig.BARCODE) {
            readers.add(new MultiFormatOneDReader(hints));
        }

        if (CameraConfig.getInstance().getScanModel() == ScanConfig.QRCODE) {
            readers.add(new QRCodeCore());
            readers.add(new DataMatrixReader());
            readers.add(new AztecReader());
            readers.add(new PDF417Reader());
            readers.add(new MaxiCodeReader());
        }

        this.readers = readers.toArray(new Reader[0]);
    }

    @Override
    public void reset() {
        if (readers != null) {
            for (Reader reader : readers) {
                reader.reset();
            }
        }
    }

    private Result decodeInternal(BinaryBitmap image) throws NotFoundException {
        if (readers != null) {
            for (Reader reader : readers) {
                try {
                    return reader.decode(image, hints);
                } catch (Exception ignored) {
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    //获取解析的核心类
    public static CustomMultiFormatReader getInstance() {
        return Holder.INSTANCE;
    }

    private CustomMultiFormatReader() {
        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
        // 设置继续的字符编码格式为UTF8
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        setHints(hints);
    }

    private static class Holder {
        static CustomMultiFormatReader INSTANCE = new CustomMultiFormatReader();
    }
}
