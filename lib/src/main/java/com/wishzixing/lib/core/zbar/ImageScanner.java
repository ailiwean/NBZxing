package com.wishzixing.lib.core.zbar;

/***
 *  Created by SWY
 *  DATE 2019/7/7
 *
 */
public class ImageScanner {
    /**
     * C pointer to a zbar_image_scanner_t.
     */
    private long peer;

    static {
        System.loadLibrary("zbar");
        init();
    }

    private static native void init();

    public ImageScanner() {
        peer = create();
    }

    /**
     * Create an associated peer instance.
     */
    private native long create();

    protected void finalize() {
        destroy();
    }

    /**
     * Clean up native data associated with an instance.
     */
    public synchronized void destroy() {
        if (peer != 0) {
            destroy(peer);
            peer = 0;
        }
    }

    /**
     * Destroy the associated peer instance.
     */
    private native void destroy(long peer);

    /**
     * Set config for indicated symbology (0 for all) to specified value.
     */
    public native void setConfig(int symbology, int config, int value)
            throws IllegalArgumentException;

    /**
     * Parse configuration string and apply to image scanner.
     */
    public native void parseConfig(String config);

    /**
     * Enable or disable the inter-image result cache (default disabled).
     * Mostly useful for scanning video frames, the cache filters duplicate
     * results from consecutive images, while adding some consistency
     * checking and hysteresis to the results.  Invoking this method also
     * clears the cache.
     */
    public native void enableCache(boolean enable);

    /**
     * Retrieve decode results for last scanned image.
     *
     * @returns the SymbolSet result container
     */
    public SymbolSet getResults() {
        return (new SymbolSet(getResults(peer)));
    }

    private native long getResults(long peer);

    /**
     * Scan for symbols in provided Image.
     * The image format must currently be "Y800" or "GRAY".
     *
     * @returns the number of symbols successfully decoded from the image.
     */
    public native int scanImage(Image image);
}