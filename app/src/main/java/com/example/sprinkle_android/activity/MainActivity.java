package com.example.sprinkle_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sprinkle_android.R;
import com.example.sprinkle_android.recognition.SpeakerRecognizer;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent initIntent = new Intent(this,InitActivity.class);
        initIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(initIntent,0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            Log.d("MainActivity","퍼미션 받기 성공");
            startService(new Intent(MainActivity.this, SpeakerRecognizer.class));

            // moveTaskToBack() : 현재의 activity가 속해있는 테스크를 백그라운드로 즉시 이동 시키는 함수.
            // 전달인자로 true : 어떠한 경우라도 무조건 백그라운드로 이동시킨다. false : 현재의 activity가 루트(root)일 경우에만 백그라운드로 이동시킨다.
            // ※루트(root)란 태스크의 가장 첫번째(바닥)의 activity임을 뜻한다.
            moveTaskToBack(true);
        }
        else if(resultCode == RESULT_CANCELED)
        {
            Log.d("MainActivity","퍼미션 거절..");
            finish();
        }
    }
}