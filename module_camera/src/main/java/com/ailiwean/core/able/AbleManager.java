package com.ailiwean.core.able;

import android.graphics.Rect;
import android.os.Handler;

import com.ailiwean.core.WorkThreadServer;
import com.ailiwean.core.zxing.core.BinaryBitmap;
import com.ailiwean.core.zxing.core.PlanarYUVLuminanceSource;

import java.util.ArrayList;
import java.util.List;

import static com.ailiwean.core.helper.ScanHelper.buildLuminanceSource;
import static com.ailiwean.core.helper.ScanHelper.getScanByteRect;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: AbleManager
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/23 2:32 PM
 */
public class AbleManager extends PixsValuesAble {

    private List<PixsValuesAble> ableList = new ArrayList<>();

    WorkThreadServer server;

    private AbleManager(Handler handler) {
        super(handler);
        loadAble();
        server = WorkThreadServer.createInstance();
    }

    public void loadAble() {
        ableList.clear();
//        ableList.add(new XQRScanAble(handler));
        ableList.add(new XQRScanZoomAble(handler));
        ableList.add(new XQRScanFastAble(handler));
        ableList.add(new XQRScanAbleRotate(handler));
        ableList.add(new LighSolveAble(handler));
        ableList.add(new RevColorSanAble(handler));
    }

    public static AbleManager createInstance(Handler handler) {
        return new AbleManager(handler);
    }

    @Override
    public void cusAction(byte[] data, int dataWidth, int dataHeight) {
        PlanarYUVLuminanceSource source = generateGlobeYUVLuminanceSource(data, dataWidth, dataHeight);
        for (PixsValuesAble able : ableList) {
            server.post(() -> {
                able.cusAction(data, dataWidth, dataHeight);
                able.needParseDeploy(source);
            });
        }
    }

    protected PlanarYUVLuminanceSource generateGlobeYUVLuminanceSource(byte[] data, int dataWidth, int dataHeight) {
        return buildLuminanceSource(data, dataWidth, dataHeight, getScanByteRect(dataWidth, dataHeight));
    }

    public void release() {
        ableList.clear();
        server.quit();
    }

}
