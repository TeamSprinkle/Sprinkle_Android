package com.example.sprinkle_android.activity;

import android.app.ActivityManager;
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
import com.example.sprinkle_android.connection.SprinkleHttpURLConnection;
import com.example.sprinkle_android.recognition.SpeakerRecognizer;
import com.example.sprinkle_android.scenarios.Call;
import com.example.sprinkle_android.scenarios.Scenario;
import com.example.sprinkle_android.scenarios.Schedule;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

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
    private static final String url = "/command/and_cmd";
    private String serviceName = null;  // isServiceRunning 메소드에 들어가는 파라미터 값
    private ArrayList<Scenario> scenarios;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.scenarios.add(new Call());
        this.scenarios.add(new Schedule());

        sleep(1000);
        this.initializeData();
        // 여기에 기능 수행하는 STT를 실행시키는 코드를 넣으면 된다...
        userText(receiveDataIntent.getStringExtra("input_userVoice")); // 사용자가 하는 말을 텍스트화 시켜서 리스트에 추가
        System.out.println(receiveDataIntent.getStringExtra("input_secretaryVoice"));
        secretaryText(receiveDataIntent.getStringExtra("input_secretaryVoice"));
        startListening();
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
            if (mAudioManager.isStreamMute(AudioManager.STREAM_NOTIFICATION)) { // 알람이 음소거인 경우
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
                isServiceRunning(SpeakerRecognizer.class.getName()); // 비서인식 서비스 상태 확인
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

    // 스레드 돌려야함
    private void requestCommand(String command)
    {
        try {
            // POST 요청을한다.
            SprinkleHttpURLConnection conn = new SprinkleHttpURLConnection(this);

            // R.string.url_1은 https://www.naver.com과 같은 특정사이트다.
            // sID -> key, id -> value, sPWD -> key, password -> value
            conn.execute(this.url, "POST", "command", command);

            // 동기로 진행된다. task가 성공하면 값을 return 받는다.
            // 만약 error가 발생하면 callBackValue에 Error : 가 포함된다.
            // return을 받을생각이 없다면 해당 코드줄은 주석처리해도된다. 단 작업의 성공여부는 알수없음
            String callBackValue = conn.get();

            // fail
            if(callBackValue.isEmpty() || callBackValue.equals("") || callBackValue == null || callBackValue.contains("Error")) {
                Log.d("ChatActivity 실패 결과값", "서버 요청 실패다");
            }
            // success
            else {
                Log.d("ChatActivity 성공 결과값", callBackValue);
                // TODO : callBackValue를 이용해서 코드기술
                JSONObject res = new JSONObject(callBackValue);
                String state = (String) res.get("state");

                if(state.equals("SUCCESS"))
                {
                    for(Scenario scenario : this.scenarios)
                    {
                        if(scenario.getIntent().equals(res.get("intent")))
                        {
                            String answer = (String) res.get("answer");
                            scenario.runScenario(this, answer);
                        }
                    }
                }
//                else if(state.equals("FALLBACK"))
//                {
//
//                }
                else
                {
                    // FALLBACK 상황인 경우에
                    // TTS에 다시 한번 말씀해 주세요 라고 말해
                    // 다시 받으면 서버에 요청

                    // require 정보가 있는 경우
                    // 각 scenario에서 require 관련 처리해서 TTS에 말하고
                    // 필요한 정보 받아와서 runScenario 실행
                }

            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d("ChatActivity", "ExecutionException");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("ChatActivity", "InterruptedException");
        } catch (Exception e){
            e.printStackTrace();
        }
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
            requestCommand(commandData);
            userText(commandData);
            System.out.println(commandData);
            // 설정 테스트
            if(commandData.equals("설정"))
            {
                Intent settingIntent = new Intent(ChatActivity.this,SettingActivity.class);
                startActivity(settingIntent);
            }
            else
            {
                requestCommand(commandData);
                userText(commandData);
            }
        }



        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };

    public Boolean isServiceRunning(String serviceName){

        // 시스템 내부의 액티비티 상태를 파악하는 ActivityManager객체를 생성한다.
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //  manager.getRunningServices(가져올 서비스 목록 개수) - 현재 시스템에서 동작 중인 모든 서비스 목록을 얻을 수 있다.
        // 리턴값은 List<ActivityManager.RunningServiceInfo>이다. (ActivityManager.RunningServiceInfo의 객체를 담은 List)
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            // ActivityManager.RunningServiceInfo의 객체를 통해 현재 실행중인 서비스의 정보를 가져올 수 있다.
            if (serviceName.equals(service.service.getClassName())) {
                Log.d("ChatActivity",service.service.getClassName() + "실행중");
                return true;
            }

        }
        Log.d("ChatActivity",serviceName + "실행중이지 않음");
        return  false;

    }

}