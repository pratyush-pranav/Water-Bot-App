package com.example.jerry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler=new Handler();
        handler.postDelayed(new MyThread(),1500);
    }
    class MyThread implements Runnable{
        @Override
        public void run() {
            Intent intent=new Intent(MainActivity.this,FunctionActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
