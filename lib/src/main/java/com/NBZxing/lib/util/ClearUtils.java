package com.NBZxing.lib.util;

import com.NBZxing.lib.manager.PixsValuesCusManager;

public class ClearUtils {

    public static void clear() {

        PixsValuesCusManager.getInstance().stop();

    }


}

