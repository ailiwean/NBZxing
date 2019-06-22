package com.wishzixing.lib.listener;

import com.google.zxing.Result;

/***
 *  Created by SWY
 *  DATE 2019/6/22
 *
 */
public interface ResultListener {

    void scanSucceed(Result result);

    void scanImgFail();

}
