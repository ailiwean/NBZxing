package com.android.NBZxing

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_select.*

/**
 * @Package:        com.android.NBZxing
 * @ClassName:      MainActiviy
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/10/18 7:26 PM
 */
class MainActiviy : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
    }

    fun toClick(view: View) {
        if (view == style1)
            ScanActivity.startSelf(this)
        if (view == style2)
            ScanActivity2.startSelf(this)
    }

}