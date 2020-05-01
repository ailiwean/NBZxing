package com.ailiwean.core;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.HashMap;
import java.util.Map;

/**
 * @Package: com.ailiwean.core
 * @ClassName: WorkThreadServer
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/19 5:46 PM
 */
public class WorkThreadServer {

    private static String DEFAULT = "CommonThread";

    private HashMap<String, HandlerThread> handlerThreadHashMap = new HashMap<>();
    private HashMap<String, Handler> handlerHashMap = new HashMap<>();

    private WorkThreadServer() {
    }

    private static class Holder {
        static WorkThreadServer INSTANCE = new WorkThreadServer();
    }

    public static WorkThreadServer getInstance() {
        return Holder.INSTANCE;
    }

    public Handler getBgHandle() {
        return getBgHandle(DEFAULT);
    }

    public Handler getBgHandle(Object o) {
        if (handlerHashMap.get(o.toString()) == null) {
            HandlerThread handlerThread = new HandlerThread(o.toString());
            handlerThread.start();
            handlerThreadHashMap.put(o.toString(), handlerThread);
            Handler handler = new Handler(handlerThread.getLooper());
            handlerHashMap.put(o.toString(), handler);
            return handler;
        }
        return handlerHashMap.get(o.toString());
    }

    public void quit() {
        for (Map.Entry<String, HandlerThread> stringHandlerThreadEntry : handlerThreadHashMap.entrySet()) {
            stringHandlerThreadEntry.getValue().quit();
        }
        handlerThreadHashMap.clear();
        handlerHashMap.clear();
    }
}
