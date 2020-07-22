package com.ailiwean.core;

import android.content.Context;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * @Package: com.ailiwean.core
 * @ClassName: Uitls
 * @Description:
 * @Author: SWY
 * @CreateDate: 2020/4/26 11:10 AM
 */
public class Utils {

    static WeakReference<Context> holder;

    public static void init(Context mContext) {
        holder = new WeakReference<>(mContext);
    }

    public static Context getContext() {
        return holder.get();
    }

    public static int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, Utils.getContext().getResources().getDisplayMetrics());
    }

    public static byte[] readFile(String fileName) {
        int len;
        FileInputStream stream;
        ByteArrayOutputStream stream2 = null;
        try {
            stream = new FileInputStream(fileName);
            stream2 = new ByteArrayOutputStream();
            byte[] buffer = new byte[5];
            //先读后写,循环读写
            while ((len = stream.read(buffer)) != -1) {
                stream2.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (stream2 != null)
            return stream2.toByteArray();
        return null;
    }

}
