package com.wishzixing.lib.util;

import com.wishzixing.lib.manager.PixsValuesCusManager;

public class ClearUtils {

    public static void clear() {

        PixsValuesCusManager.getInstance().stop();

    }


}

