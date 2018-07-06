package com.shopin.android.anr;

import android.os.Looper;

/**
 * @author will on 2018/7/6 14:54
 * @email pengweiqiang64@163.com
 * @description
 * @Version
 */

public class ANRException extends RuntimeException {
    public ANRException() {
        super("应用程序无响应，快来改BUG啊！！");
        Thread mainThread = Looper.getMainLooper().getThread();
        setStackTrace(mainThread.getStackTrace());

    }
}
