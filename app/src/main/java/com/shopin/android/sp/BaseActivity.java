package com.shopin.android.sp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @author will on 2018/7/6 11:58
 * @email pengweiqiang64@163.com
 * @description
 * @Version
 */

public class BaseActivity extends AppCompatActivity {

    private  String TAG = getClass().getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Log.i(TAG,"onCreate "+"   intent:"+intent.hashCode() + "\n  name:"+intent.getStringExtra("name")+"  key:"+intent.getStringExtra("key"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Intent intentOld = getIntent();
        Log.i(TAG,"onNewIntent "+"   intent:"+intent.hashCode() + "\n  name:"+intent.getStringExtra("name")+"  key:"+intent.getStringExtra("key"));
        Log.i(TAG,"intentOld :"+intentOld.hashCode()+ "\n  name:"+intentOld.getStringExtra("name")+"  key:"+intentOld.getStringExtra("key"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume ");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"onRestart ");
    }

    @Override
    public void finish() {
        super.finish();
    }
}
