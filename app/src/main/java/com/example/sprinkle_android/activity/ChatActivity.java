package com.example.sprinkle_android.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprinkle_android.R;
import com.example.sprinkle_android.adapter.Code;
import com.example.sprinkle_android.adapter.DataItem;
import com.example.sprinkle_android.adapter.MyAdapter;

import java.util.ArrayList;
import java.util.Locale;

import static android.os.SystemClock.sleep;

public class ChatActivity extends AppCompatActivity{

    private ArrayList<DataItem> dataList;
    private Intent receiveDataIntent;
    private Intent speechRecognitionIntent;
    private SpeechRecognizer mRecognizer;
    protected AudioManager mAudioManager;
    private LinearLayoutManager manager;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private TextToSpeech tts;
    private boolean ttsInitRes;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.initializeData();
        sleep(1000);
        // 여기에 기능 수행하는 STT를 실행시키는 코드를 넣으면 된다...
        userText(receiveDataIntent.getStringExtra("input_userVoice")); // 사용자가 하는 말을 텍스트화 시켜서 리스트에 추가
        System.out.println(receiveDataIntent.getStringExtra("input_secretaryVoice"));
        secretaryText(receiveDataIntent.getStringExtra("input_secretaryVoice"));
        //startListening();
    }

    public void initializeData()
    {
        dataList = new ArrayList<>();
        // RecognizerIntent 객체 생성
        speechRecognitionIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognitionIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        speechRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시맬로우 버전 이상인 경우
            if (mAudioManager.isStreamMute(AudioManager.STREAM_NOTIFICATION)) { // 디바이스가 음소거인 경우
                System.out.println("알림 음소거 해제");
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0); // 알림 언뮤트
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0); // 미디어 언뮤트
                //mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0); // 알람 언뮤트(알림x)
                //mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0); // 벨소리 언뮤트
                //mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0); // 시스템 언뮤트
            }
        } else { // 마시맬로우 버전 이하인 경우
            mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        }

        receiveDataIntent = getIntent();

        recyclerView = findViewById(R.id.chat_rView_recyclerView);

        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        myAdapter = new MyAdapter(dataList); // Adapter 생성
        recyclerView.setAdapter(myAdapter);  // Adapter 등록

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS)
                {
                    int result = tts.setLanguage(Locale.KOREA);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.e("TTS","This Language is not supported");
                    }
                    else
                    {
                        Log.d("TTS","Initialization Success");
                        //secretaryText("테스트 해보는거야");
                    }
                }
                else
                {
                    Log.e("TTS", "Initialization Failed!");
                }
            }
        });
    }
    public void secretaryText(String voiceData)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시맬로우 버전 이상인 경우
            if (mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC)) { // 미디어가 뮤트라면
                System.out.println("미디어 음소거 해제");
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0); // 미디어 언뮤트
            }
        }
        speakOut(voiceData);
        dataList.add(new DataItem(voiceData,  Code.ViewType.LEFT_CONTENT)); // 시스템의 말 데이터 추가
        myAdapter.notifyDataSetChanged(); // 리스트의 데이터가 변경되면 갱신 시켜주는 함수
    }

    private void speakOut(String voiceData)
    {
        tts.setPitch((float) 1.0);
        tts.setSpeechRate((float) 1.0);
        tts.speak(voiceData, TextToSpeech.QUEUE_FLUSH,null,"id1");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
    }
    public void userText(String voiceData)
    {
        dataList.add(new DataItem(voiceData,  Code.ViewType.RIGHT_CONTENT)); // 사용자의 말 데이터 추가
        myAdapter.notifyDataSetChanged(); // 리스트의 데이터가 변경되면 갱신 시켜주는 함수
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

    public void startListening()
    {
        mRecognizer= SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(speechRecognitionIntent);
    }

    // RecognizerIntent 객체에 할당할 listener 생성 test
    private RecognitionListener listener = new RecognitionListener()
    {
        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error)
        {
            String message;
            switch (error)
            {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override public void onResults(Bundle results)
        {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어준다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String commandData = null;
            for(int i = 0; i < matches.size() ; i++)
            {
                commandData = matches.get(i);
            }
            userText(commandData);
            System.out.println(commandData);

            // 설정 테스트
            if(commandData.equals("설정"))
            {
                Intent settingIntent = new Intent(ChatActivity.this,SettingActivity.class);
                startActivity(settingIntent);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };

}