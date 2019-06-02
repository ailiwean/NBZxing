package com.wishzixing.lib;

import android.app.Activity;

/***
 *  Created by SWY
 *  DATE 2019/6/1
 *
 */
public interface WishLife {

    void onCreat(Activity activity);

    void onResume();

    void onPause();

    void onStop();

    void onDestory();

}
