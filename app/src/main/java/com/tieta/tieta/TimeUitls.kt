package com.tieta.tieta

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zwy on 2018/5/11.
 * email:16681805@qq.com
 */
class TimeUitls {
    companion object {

        fun getNowTime(): String? {
            val time = System.currentTimeMillis()//long now = android.os.SystemClock.uptimeMillis();
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val d1 = Date(time)
            val t1 = format.format(d1)
            return t1;
        }
    }
}