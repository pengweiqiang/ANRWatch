package com.shopin.android.sp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    public void main(View view){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtra("key","哈喽");
        startActivity(intent);
    }
}
