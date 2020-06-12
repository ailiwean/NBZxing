package com.ailiwean.core.able;

import android.os.Handler;

import com.ailiwean.core.WorkThreadServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Package: com.ailiwean.core.able
 * @ClassName: AbleManager
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/23 2:32 PM
 */
public class AbleManager extends PixsValuesAble {

    private List<PixsValuesAble> ableList = new ArrayList<>();

    private AbleManager(Handler handler) {
        super(handler);
        //ableList.add(new XQRScanAble(handler));
        ableList.add(new XQRScanZoomAble(handler));
        ableList.add(new XQRScanAbleRotate(handler));
        ableList.add(new LighSolveAble(handler));
    }

    public static AbleManager getInstance(Handler handler) {
        return new AbleManager(handler);
    }

    @Override
    public void cusAction(byte[] data, int dataWidth, int dataHeight) {
        for (PixsValuesAble able : ableList) {
            WorkThreadServer.getInstance()
                    .post(() -> able.cusAction(data, dataWidth, dataHeight));
        }
    }

    public void release() {
        ableList.clear();
        WorkThreadServer.getInstance().quit();
    }

}
