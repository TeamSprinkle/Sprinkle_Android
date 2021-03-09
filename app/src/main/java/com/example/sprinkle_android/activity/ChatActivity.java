package com.example.sprinkle_android.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprinkle_android.R;
import com.example.sprinkle_android.adapter.Code;
import com.example.sprinkle_android.adapter.DataItem;
import com.example.sprinkle_android.adapter.MyAdapter;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ArrayList<DataItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 아래의 함수 대신
        // 사용자가 음성 입력을 하면 RIGHT_CONTENT에 메시지를 보내주고,
        // 시스템이 응답할때 TTS와 함께 LEFT_CONTENT에 메시지를 보내준다.
        this.initializeData();

        RecyclerView recyclerView = findViewById(R.id.chat_rView_recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        recyclerView.setAdapter(new MyAdapter(dataList));  // Adapter 등록

        // 여기에 기능 수행하는 STT를 실행시키는 코드를 넣으면 된다...
    }

    public void initializeData()
    {
        dataList = new ArrayList<>();

        dataList.add(new DataItem("여긴 비서가 음성으로 말하면서 출력될 말풍선 입니다.",  Code.ViewType.LEFT_CONTENT));
        dataList.add(new DataItem("여긴 우리가 음성으로 말하면 출력될 말풍선 입니다.",   Code.ViewType.RIGHT_CONTENT));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d("이벤트 확인","Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d("이벤트 확인","Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d("이벤트 확인","Action was UP");
                finish();
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d("이벤트 확인","Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d("이벤트 확인","Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
}