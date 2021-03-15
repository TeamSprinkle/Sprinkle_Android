package com.example.sprinkle_android.recognition;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.sprinkle_android.R;
import com.example.sprinkle_android.activity.ChatActivity;
import com.example.sprinkle_android.activity.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.speech.SpeechRecognizer.ERROR_AUDIO;
import static android.speech.SpeechRecognizer.ERROR_CLIENT;
import static android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS;
import static android.speech.SpeechRecognizer.ERROR_NETWORK;
import static android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT;
import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;
import static android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
import static android.speech.SpeechRecognizer.ERROR_SERVER;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
import static com.example.sprinkle_android.recognition.App.CHANEL_ID;

public class SpeakerRecognizer extends RecognitionService {

    private static final int MSG_VOICE_RECO_READY = 0;
    private static final int MSG_VOICE_RECO_END = 1;
    private static final int MSG_VOICE_RECO_RESTART = 2;
    private SpeechRecognizer mSrRecognizer;
    private boolean mBoolVoiceRecognitionStarted;
    protected AudioManager mAudioManager;
    private Intent itIntent;//음성인식 Intent
    private boolean end = false;
    private String resInputVoice = null;
    private String secretaryName = "시리야";


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate()함수 호출/ 순서 : 1");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) { //시스템에서 음성인식 서비스 실행이 가능하다면
            itIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            itIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            itIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN.toString());
            itIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
            itIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
            itIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            startListening();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHdrVoiceRecognitionState = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("handleMessage()함수 호출/ 순서 : 2");
            switch (msg.what) {
                case MSG_VOICE_RECO_READY:
                    break;
                case MSG_VOICE_RECO_END: {
                    stopListening();
                    sendEmptyMessageDelayed(MSG_VOICE_RECO_RESTART, 1000);
                    break;
                }
                case MSG_VOICE_RECO_RESTART:
                    startListening();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand()함수 호출/ 순서 : 4");
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this
                , 0, notificationIntent, 0); //알람을 눌렀을 때 해당 엑티비티로

        Notification notification = new NotificationCompat.Builder(this, CHANEL_ID)
                .setContentTitle("STTModule")
                .setContentText("음성인식중...")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();

        // 아래 코드가 있으면 일단 오류... 근데 이 함수가 호출되면 알림바?에 표시됨.. 원래라면 서비스 호출후 5초이내에 이 메소드가 호출되지 않으면 종료된다고 하는데...왜 종료되지 않는 걸까?
        //startForeground(1, notification);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy()함수 호출/ 순서 : 10");
        end = true;
        mSrRecognizer.destroy();
        mHdrVoiceRecognitionState.sendEmptyMessage(MSG_VOICE_RECO_READY); //음성인식 서비스 다시 시작
    }

    @Override
    protected void onStartListening(Intent recognizerIntent, RecognitionService.Callback listener) {
        System.out.println("onStartListening()함수 호출/ 순서 : 11");
    }

    public void startListening() {
        System.out.println("startListening()함수 호출/ 순서 : 3");
        if(!end){
            // 음성인식 시작할때 띠링~ 소리 제거하는 코드
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시맬로우 버전 이상인 경우
                if (!mAudioManager.isStreamMute(AudioManager.STREAM_NOTIFICATION)) { // 알림소리가 음소거가 아닌경우
                    System.out.println("알림 음소거");
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0); // 알림 뮤트
                    //mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0); // 미디어 뮤트
                    //mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0); // 알람 뮤트(알림x)
                    //mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0); // 벨소리 뮤트
                    //mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0); // 시스템 뮤트
                }
            } else { // 마시맬로우 버전 이하인 경우
                mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            }
            if (!mBoolVoiceRecognitionStarted) { // 최초의 실행이거나 인식이 종료된 후에 다시 인식을 시작하려 할 때
                if (mSrRecognizer == null) {
                    mSrRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    mSrRecognizer.setRecognitionListener(mClsRecognitionListener);
                }
                mSrRecognizer.startListening(itIntent);
            }
            mBoolVoiceRecognitionStarted = true;  //음성인식 서비스 실행 중
        }
        // 이 코드는 음성인식이 성공적으로 입력이 됬을 때 음성인식을 다시 시작하는 코드이다.
        // 스노우 보이가 되면 이 코드는 필요 없다.
        mSrRecognizer.startListening(itIntent);
    }

    public void stopListening() //Override 함수가 아닌 한번만 호출되는 함수 음성인식이 중단될 때
    {
        System.out.println("stopListening()함수 호출/ 순서 : 7");
        try {
            if (mSrRecognizer != null && mBoolVoiceRecognitionStarted) {
                mSrRecognizer.stopListening(); //음성인식 Override 중단을 호출
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mBoolVoiceRecognitionStarted = false;  //음성인식 종료
    }


    @Override
    protected void onCancel(RecognitionService.Callback listener) {
        System.out.println("onCancel()함수 호출/ 순서 : 8");
        mSrRecognizer.cancel();
    }

    @Override
    protected void onStopListening(RecognitionService.Callback listener) { //음성인식 Override 함수의 종료부분
        System.out.println("onStopListening()함수 호출/ 순서 : 9");
        mHdrVoiceRecognitionState.sendEmptyMessage(MSG_VOICE_RECO_RESTART); //음성인식 서비스 다시 시작
    }

    private final RecognitionListener mClsRecognitionListener = new RecognitionListener() {
        @Override
        public void onRmsChanged(float rmsdB) {
            // db이 증가하거나 낮아질때 호출 됨
            System.out.println("onRmsChanged()함수 호출/ 순서 : 6");
        }

        @Override
        public void onResults(Bundle results) {
            System.out.println("onResults()함수 호출/ 순서 : 12");
            //Recognizer KEY를 사용하여 인식한 결과값을 가져오는 코드
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            final String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            StringTokenizer strTokenizer = new StringTokenizer(Arrays.toString(rs),"[]");
            resInputVoice = strTokenizer.nextToken();
            if(resInputVoice.equals(secretaryName)) {
                Intent chatIntent = new Intent(getApplicationContext(),ChatActivity.class);
                // Intent Flag 정리 관련 글 https://kylblog.tistory.com/21
                // 아래 플래그 값을 써야 ChatActivity 아래에 MainActivity 가 깔리는걸 방지할 수 있다.
                chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                chatIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                chatIntent.putExtra("input_userVoice",resInputVoice);
                chatIntent.putExtra("input_secretaryVoice","네");
                startActivity(chatIntent);
            }
            else {
                startListening();
            }
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            System.out.println("onReadyForSpeech()함수 호출/ 순서 : 5");
        }

        @Override
        public void onEndOfSpeech() {
            System.out.println("onEndOfSpeech()함수 호출/ 순서 : 13");
        }

        @Override
        public void onError(int intError) {
            String message = "";
            switch (intError) {

                case ERROR_NETWORK_TIMEOUT:
                    //네트워크 타임아웃
                    message = "네트워크 타임아웃";
                    break;
                case ERROR_NETWORK:
                    message ="네트워크 에러";
                    break;
                case ERROR_AUDIO:
                    //녹음 에러
                    message ="녹음 에러";
                    break;
                case ERROR_SERVER:
                    //서버에서 에러를 보냄
                    message = "서버에서 에러를 보냄";
                    break;
                case ERROR_CLIENT:
                    //클라이언트 에러
                    message = "클라이언트 에러";
                    break;
                case ERROR_SPEECH_TIMEOUT:
                    //아무 음성도 듣지 못했을 때
                    message = "아무 음성도 듣지 못함";
                    mHdrVoiceRecognitionState.sendEmptyMessage(MSG_VOICE_RECO_END);
                    break;
                case ERROR_NO_MATCH:
                    //적당한 결과를 찾지 못했을 때
                    message = "적당한 결과를 찾이 못함";
                    mHdrVoiceRecognitionState.sendEmptyMessage(MSG_VOICE_RECO_END);
                    break;
                case ERROR_RECOGNIZER_BUSY:
                    //RecognitionService가 바쁠 때
                    message = "RecognitionService가 바쁨";
                    break;
                case ERROR_INSUFFICIENT_PERMISSIONS:
                    //uses - permission(즉 RECORD_AUDIO) 이 없을 때
                    message = "permission이 없음";
                    break;
            }
            Log.e("에러가 발상했습니다 : ", message);
        }
        @Override
        public void onBeginningOfSpeech() {
            System.out.println("onBeginningOfSpeech()함수 호출/ 순서 : 14");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            System.out.println("onBufferReceived()함수 호출/ 순서 : 15");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            System.out.println("onEvent()함수 호출/ 순서 : 16");
        }

        @Override
        public void onPartialResults(Bundle partialResults) { //부분 인식을 성공 했을 때
            System.out.println("onPartialResults()함수 호출/ 순서 : 17");
        }
    };
}
