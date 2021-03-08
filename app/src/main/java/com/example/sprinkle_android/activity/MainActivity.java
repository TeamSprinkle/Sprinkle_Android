package com.example.sprinkle_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sprinkle_android.R;



public class MainActivity extends AppCompatActivity {

    private Button setting_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this,InitActivity.class);
        startActivityForResult(intent,0);

        this.setting_btn = (Button)findViewById(R.id.main_btn_setting);

        this.setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            Log.d("MainActivity","퍼미션 받기 성공");
        }
        else if(resultCode == RESULT_CANCELED)
        {
            Log.d("MainActivity","퍼미션 거절..");
            finish();
        }
    }
}