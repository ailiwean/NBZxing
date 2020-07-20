package com.ailiwean.core.able;

import android.os.Handler;

import com.ailiwean.core.WorkThreadServer;

import java.util.ArrayList;
import java.util.List;

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
        ableList.add(new XQRScanFast(handler));
        ableList.add(new XQRScanZoomAble(handler));
        ableList.add(new XQRScanAbleRotate(handler));
//        ableList.add(new CQRScanZoomAble(handler));
//        ableList.add(new LighSolveAble(handler));
        ableList.add(new RevColorSanAble(handler));
    }

    public static AbleManager createInstance(Handler handler) {
        return new AbleManager(handler);
    }

    @Override
    public void cusAction(byte[] data, int dataWidth, int dataHeight) {
        for (PixsValuesAble able : ableList) {
            server.post(() -> able.cusAction(data, dataWidth, dataHeight));
        }
    }

    public void release() {
        ableList.clear();
        server.quit();
    }

}
