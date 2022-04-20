package cn.tosee.rtch264demo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DecimalFormat;

/**
 * @author : create by yanggaosheng$
 * @time : 2019-12-06$
 * @des :  网络连接相关工具类$
 **/
public class NetworkUtils {

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager == null) {
                return false;
            }
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    private static DecimalFormat DF = new DecimalFormat("0.00");
    public static String convertNetSpeed(int speed){
        if(speed > 1024){
            return (DF.format((float)speed/1024)) + "kb";
        }else {
            return (DF.format((float)speed))+"b";
        }
    }
}
