package com.android.NBZXing

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.ailiwean.core.view.ZxingCameraView

/**
 * @Package:        com.android.NBZXing
 * @ClassName:      CusZxing
 * @Description:
 * @Author:         SWY
 * @CreateDate:     2020/4/30 4:06 PM
 */
class CusZxing @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : ZxingCameraView(context, attributeSet, def) {

    override fun resultBack(content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    override fun provideFloorView(): View? {
        return null
    }
}