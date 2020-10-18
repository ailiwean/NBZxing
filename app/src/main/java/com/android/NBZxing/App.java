package com.android.NBZxing;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;


/**
 * @Package: com.android.NBZxing
 * @ClassName: App
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/10/1 9:22 PM
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "99de934384", true);
    }
}
