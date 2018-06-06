package com.tieta.tieta

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler

/**
 * Created by zwy on 2018/5/8.
 * email:16681805@qq.com
 */
class StartActivity:Activity(){
    var mHandler=Handler();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Thread(object:Runnable{
            override fun run() {
                Thread.sleep(2000);
                mHandler.post(object :Runnable{
                    override fun run() {
                        var intent:Intent = Intent(this@StartActivity,MainActivity::class.java);
                        startActivity(intent);
                    }

                });
            }
        }).start();
    }
}