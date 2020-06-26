package com.ailiwean.core.czxing;

public class BarcodeReader {

    private long _nativePtr;
    private static BarcodeReader instance;

    public static BarcodeReader getInstance() {
        if (instance == null) {
            synchronized (BarcodeReader.class) {
                if (instance == null) {
                    instance = new BarcodeReader();
                }
            }
        }
        return instance;
    }

    private BarcodeReader() {
        BarcodeFormat[] formats = new BarcodeFormat[]{BarcodeFormat.QR_CODE,
                BarcodeFormat.CODABAR,
                BarcodeFormat.CODE_128,
                BarcodeFormat.EAN_13,
                BarcodeFormat.UPC_A};
        _nativePtr = NativeSdk.getInstance().createInstance(getNativeFormats(formats));
    }

    public void setBarcodeFormat(BarcodeFormat... formats) {
        NativeSdk.getInstance().setFormat(_nativePtr, getNativeFormats(formats));
    }

    private int[] getNativeFormats(BarcodeFormat... formats) {
        int[] nativeFormats = new int[formats.length];
        for (int i = 0; i < formats.length; ++i) {
            nativeFormats[i] = formats[i].ordinal();
        }
        return nativeFormats;
    }

    public CodeResult read(byte[] data, int cropLeft, int cropTop, int cropWidth, int cropHeight, int rowWidth, int rowHeight) {
        try {
            NativeSdk.getInstance().readBarcodeByte(_nativePtr, data, cropLeft, cropTop, cropWidth, cropHeight, rowWidth, rowHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void prepareRead() {
        NativeSdk.getInstance().prepareRead(_nativePtr);
    }

    public void stopRead() {
        NativeSdk.getInstance().stopRead(_nativePtr);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (_nativePtr != 0) {
                NativeSdk.getInstance().destroyInstance(_nativePtr);
                _nativePtr = 0;
            }
        } finally {
            super.finalize();
        }
    }

    public void setReadCodeListener(ReadCodeListener readCodeListener) {
        NativeSdk.getInstance().setReadCodeListener(readCodeListener);
    }

    public interface ReadCodeListener {
        void onReadCodeResult(CodeResult result);
    }
}