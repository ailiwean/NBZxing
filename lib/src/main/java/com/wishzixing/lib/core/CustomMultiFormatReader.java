package com.wishzixing.lib.core;

import com.google.zxing.BarcodeFormat;
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
import com.google.zxing.qrcode.QRCodeReader;
import com.wishzixing.lib.config.CameraConfig;
import com.wishzixing.lib.config.ScanModelConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;


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

        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        @SuppressWarnings("unchecked")
        Collection<BarcodeFormat> formats =
                hints == null ? null : (Collection<BarcodeFormat>) hints.get(DecodeHintType.POSSIBLE_FORMATS);
        Collection<Reader> readers = new ArrayList<>();
        if (formats != null) {
            boolean addOneDReader =
                    formats.contains(BarcodeFormat.UPC_A) ||
                            formats.contains(BarcodeFormat.UPC_E) ||
                            formats.contains(BarcodeFormat.EAN_13) ||
                            formats.contains(BarcodeFormat.EAN_8) ||
                            formats.contains(BarcodeFormat.CODABAR) ||
                            formats.contains(BarcodeFormat.CODE_39) ||
                            formats.contains(BarcodeFormat.CODE_93) ||
                            formats.contains(BarcodeFormat.CODE_128) ||
                            formats.contains(BarcodeFormat.ITF) ||
                            formats.contains(BarcodeFormat.RSS_14) ||
                            formats.contains(BarcodeFormat.RSS_EXPANDED);
            // Put 1D readers upfront in "normal" mode
            if (addOneDReader && !tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }
            if (formats.contains(BarcodeFormat.QR_CODE)) {
                readers.add(new QRCodeReader());
            }
            if (formats.contains(BarcodeFormat.DATA_MATRIX)) {
                readers.add(new DataMatrixReader());
            }
            if (formats.contains(BarcodeFormat.AZTEC)) {
                readers.add(new AztecReader());
            }
            if (formats.contains(BarcodeFormat.PDF_417)) {
                readers.add(new PDF417Reader());
            }
            if (formats.contains(BarcodeFormat.MAXICODE)) {
                readers.add(new MaxiCodeReader());
            }
            // At end in "try harder" mode
            if (addOneDReader && tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }

        }

        if (readers.isEmpty()) {
            if (!tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }

            readers.add(new QRCodeReader());
            readers.add(new DataMatrixReader());
            readers.add(new AztecReader());
            readers.add(new PDF417Reader());
            readers.add(new MaxiCodeReader());

            if (tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }
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
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<>();
        decodeFormats = new Vector<>();

        Vector<BarcodeFormat> PRODUCT_FORMATS = new Vector<>(5);
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_A);
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_E);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
        // PRODUCT_FORMATS.add(BarcodeFormat.RSS14);
        Vector<BarcodeFormat> ONE_D_FORMATS = new Vector<>(PRODUCT_FORMATS.size() + 4);
        ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_39);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_93);
        ONE_D_FORMATS.add(BarcodeFormat.CODE_128);
        ONE_D_FORMATS.add(BarcodeFormat.ITF);
        Vector<BarcodeFormat> QR_CODE_FORMATS = new Vector<>(1);
        QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);
        Vector<BarcodeFormat> DATA_MATRIX_FORMATS = new Vector<>(1);
        DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX);

        int scanModel = CameraConfig.getInstance().getScanModel();

        // 这里设置可扫描的类型，我这里选择了都支持
        if (scanModel == ScanModelConfig.BARCODE)
            decodeFormats.addAll(ONE_D_FORMATS);

        if (scanModel == ScanModelConfig.QRCODE)
            decodeFormats.addAll(QR_CODE_FORMATS);

        if (scanModel == ScanModelConfig.ALL) {
            decodeFormats.addAll(ONE_D_FORMATS);
            decodeFormats.addAll(QR_CODE_FORMATS);
            decodeFormats.addAll(DATA_MATRIX_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        // 设置继续的字符编码格式为UTF8
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        setHints(hints);
    }

    private static class Holder {
        static CustomMultiFormatReader INSTANCE = new CustomMultiFormatReader();
    }
}
