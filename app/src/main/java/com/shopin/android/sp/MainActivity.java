package com.shopin.android.sp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntent().putExtra("name","anrwatch");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void anr(View view){
        //模拟ANR操作
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void login(View view){

        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.putExtra("name","demo");
        startActivity(intent);
    }
}
