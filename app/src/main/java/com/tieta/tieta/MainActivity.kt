package com.tieta.tieta

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import kotlinx.android.synthetic.main.activity_main.*
import org.devio.takephoto.app.TakePhotoActivity
import org.devio.takephoto.model.TResult
import java.io.File
import java.util.*

class MainActivity : TakePhotoActivity(), View.OnClickListener {
    var mLocationClient: LocationClient? = null;
    val SDK_PERMISSION_REQUEST = 127
    var permissionInfo: String? = null;
    var latitude: Double = 0.0;
    var longitude: Double = 0.0;
    var address: String? = "定位失败";
    var mBitmap: Bitmap? = null;
    var time = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_take_photo.setOnClickListener(this);

        mLocationClient = LocationClient(applicationContext);
        mLocationClient!!.registerLocationListener(object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation?) {
                if (location != null) {
                    latitude = location.latitude;
                    longitude = location.longitude;
                    address = location.addrStr;
                    Log.i("zwy", "" + latitude + " " + longitude + " " + address);
                }
            }
        });

        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy;
        option.setCoorType("bd0911");
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setEnableSimulateGps(false);
        option.setIsNeedAddress(true);
        mLocationClient!!.locOption = option;
        getPersimmions();

        btn_save_photo.setOnClickListener {
            ImageUtils.saveBitmap(this, mBitmap, time);
        }
    }

    override fun onStart() {
        super.onStart()
        mLocationClient!!.start();
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onClick(view: View) {
        val file = File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg")
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        val imageUri = Uri.fromFile(file)
        if (view.id == R.id.btn_take_photo) {
            takePhoto.onPickFromCapture(imageUri)
        }

    }

    override fun takeCancel() {
        super.takeCancel()
    }

    override fun takeFail(result: TResult?, msg: String?) {
        super.takeFail(result, msg)
        Log.i("fail", result.toString());
    }

    override fun takeSuccess(result: TResult?) {
        super.takeSuccess(result)
        Log.i("success", result.toString());
        val path: String = result!!.image.originalPath;
        val bitmap: Bitmap = BitmapFactory.decodeFile(path);

        //打logo水印
        val logo = BitmapFactory.decodeResource(resources, R.mipmap.logo);
        var rs = ImageUtils.createWaterMaskRightTop(this, bitmap, logo, 20, 20);
        //打地址水印
        rs = ImageUtils.drawTextToLeftBottom(this, rs, "地址：" + address, 24, Color.WHITE, 10, 10);
        //打纬度水印
        rs = ImageUtils.drawTextToLeftBottom(this, rs, "纬度：" + latitude.toString(), 24, Color.WHITE, 10, 39);
        //经度
        rs = ImageUtils.drawTextToLeftBottom(this, rs, "经度：" + longitude.toString(), 24, Color.WHITE, 10, 68);
        var time = TimeUitls.getNowTime();
        rs = ImageUtils.drawTextToLeftBottom(this, rs, "时间：" + TimeUitls.getNowTime(), 24, Color.WHITE, 10, 97);
        iv_show_photo.setImageBitmap(rs);
        mBitmap = rs;
    }

    @TargetApi(23)
    private fun getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = ArrayList<String>()
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            /*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n"
            }


            if (permissions.size > 0) {
                requestPermissions(permissions.toTypedArray(), SDK_PERMISSION_REQUEST)
            }
        }
    }

    @TargetApi(23)
    private fun addPermission(permissionsList: ArrayList<String>, permission: String): Boolean {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true
            } else {
                permissionsList.add(permission)
                return false
            }

        } else {
            return true
        }
    }

    @TargetApi(23)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    fun setWaterMark() {

    }


}
