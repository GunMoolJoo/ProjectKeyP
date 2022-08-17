package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class IntroActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout); //xml , java 소스 연결
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                // 인트로 화면을 보여준 후 intent를 사용해 메인화면인 MainActivity.class로 넘어가도록 함
                Intent intent = new Intent (IntroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },2000); //2초 뒤에 Runner객체 실행하도록 함
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}

