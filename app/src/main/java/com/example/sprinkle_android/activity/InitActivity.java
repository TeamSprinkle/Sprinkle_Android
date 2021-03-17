package com.example.sprinkle_android.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.sprinkle_android.R;
import com.example.sprinkle_android.adapter.Code;
import com.example.sprinkle_android.connection.SprinkleHttpURLConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class InitActivity extends AppCompatActivity {

    private List<String> phoneBooks = new ArrayList<String>();
    private String androidId = null;
    private String phoneNumber = null;
    private boolean resCheck = false;
    private String userEmail = null;
    private String emailType = null;
    private final int USERINFO_REQUEST_CODE = 0;
    private static final String url = "/users/init";
    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        checkPermission();
    }


    private void initUserInfo()
    {
        try {
            // POST 요청을한다.
            SprinkleHttpURLConnection conn = new SprinkleHttpURLConnection(this);

            // R.string.url_1은 https://www.naver.com과 같은 특정사이트다.
            // sID -> key, id -> value, sPWD -> key, password -> value
            conn.execute(this.url, "POST", "deviceId", this.androidId, "phoneNum", this.phoneNumber, "phonebooks", this.phoneBooks.toString());

            // 동기로 진행된다. task가 성공하면 값을 return 받는다.
            // 만약 error가 발생하면 callBackValue에 Error : 가 포함된다.
            // return을 받을생각이 없다면 해당 코드줄은 주석처리해도된다. 단 작업의 성공여부는 알수없음
            String callBackValue = conn.get();

            // fail
            if(callBackValue.isEmpty() || callBackValue.equals("") || callBackValue == null || callBackValue.contains("Error")) {
                Log.d("MainActivity 결과값", "서버 요청 실패다");
            }
            // success
            else {
                Log.d("MainActivity 결과값", callBackValue);
                // TODO : callBackValue를 이용해서 코드기술
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d("MainActivity", "ExecutionException");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("MainActivity", "InterruptedException");
        }
    }

    private void checkPermission()
    {
        //if(ContextCompat.checkSelfPermission("컨텍스트 정보의 자리","요청할 권한") != PackageManager.PERMISSION_GRANTED)
        // checkSelfPermission()의 리턴값은 요청한 권한이 수락되었을때 PERMISSION_GRANTED를 리턴한다.
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED // Manifest.permission.WRITE_CONTACTS 승인되지 않았을 떄
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED // Manifest.permission.READ_CONTACTS 승인되지 않았을 떄
                || ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED // Manifest.permission.READ_PHONE_STATE 승인되지 않았을 떄
                || ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED // Manifest.permission.CALL_PHONE 승인되지 않았을 떄
                || ContextCompat.checkSelfPermission(this,Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED // Manifest.permission.GET_ACCOUNTS 승인되지 않았을 떄
                || ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED // Manifest.permission.INTERNET 승인되지 않았을 떄
                || ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED // Manifest.permission.RECORD_AUDIO 승인되지 않았을 떄
                || ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED // Manifest.permission.WRITE_CALENDAR 승인되지 않았을 떄
                || ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {// Manifest.permission.READ_CALENDAR 승인되지 않았을 떄
            ActivityCompat.requestPermissions(this, Code.PERMISSION_PROJECTION, 0);
        }
        else{
            //이 영역은 권한이 거부되었을 때
            //권한이 거부되었을 때에 사용자에게 권한 설정을 요구하는 다이얼로그를 띄우기 위해서
            //requestPermission을 쓴다. requestPermission(액티비티의 정보,String[]의 권한요청명령 ,request 코드)
            //여기서 리퀘스트 코드는 후에 onRequestPermissionResult() 메서드에 결과물이 전달될 시, 결과물들을 구분 짓는 인덱스
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_CONTACTS)){ // 이곳에 퍼미션을 더 추가해야 한다.
                //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
                // 이 경우 계속해서 권한은 묻는건 사용자의 입장이 곤란할 수 있지만 그렇다고 사용에 꼭 필요한 권한일 경우 묻지 않을 수 없다.
                // 그렇기 때문에 한번 취소했던 이력이 있는 경우 이것에 대해 설명해주는 Notice를 주는 등, 여러가지 작업을 생각해서 작성해보자.

            }else{
                //사용자가 다시 보지 않기에 체크하고, 권한 설정을 거절한 이력이 있는 경우

            }
            //사용자에게 권한 요청 다이얼로그를 띄우는데 만약 사용자가 다시 보지 않기에 체크를  했을 경우
            //곧바로 onRequestPermissionResult가 실행된다.
            ActivityCompat.requestPermissions(this, Code.PERMISSION_PROJECTION,0);
        }
    }


    @SuppressLint("CommitPrefEdits")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Intent resIntent = new Intent();
        int grantedCnt = 0;

        for(int i = 0 ; i < permissions.length ; i++)
        {
            //System.out.println("퍼미션 확인 : " + permissions[i]);
            //System.out.println("이건 뭐냐? : " + grantResults[i]);
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
            {
                grantedCnt++;
            }
        }
        switch (requestCode)
        {
            case 0:
                if(grantResults.length > 0 && grantedCnt < 10) // AVD로 확인중인데 전화관련 퍼미션이 허용되지 않아서 퍼미션 12개중 10개만 오는 상황.. 핸드폰으로 할때 12로 하고 AVD일때는 10으로 맞춰 놓고 하자...
                {
                    // 권한 하나라도 거부
                    resCheck = false;
                    setResult(RESULT_CANCELED,resIntent);
                }
                else
                {
                    // 권한 모두 허가
                    resCheck = true;
                    getDeviceInfo();
                    getAddressBook();
                    getUserInfo();

                    // SharedPreferences 초기화
                    this.pref = PreferenceManager.getDefaultSharedPreferences(this);
                    this.editor = pref.edit();
                    // 비서 이름 초기화
                    this.editor.putString("secretaryName","시리야");
                    this.editor.apply();
                    
                    //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
                    //initStatus라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
                    String initStatus = pref.getString("initStatus","");
                    if(!initStatus.equals("true"))
                    {
//                        //init 메소드 실행.
//                        initUserInfo();
//
////                      initUserInfo() 메소드가 성공적으로 수행되면 initStatus를 true로 변경.
//
//                        // Activity가 종료되기 전에 저장한다.
//                        //SharedPreferences를 sFile이름, 기본모드로 설정
//                        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
//
//                        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        String text = editText.getText().toString(); // 사용자가 입력한 저장할 데이터
//                        editor.putString("text",text); // key, value를 이용하여 저장하는 형태
//                        //다양한 형태의 변수값을 저장할 수 있다.
//                        //editor.putString();
//                        //editor.putBoolean();
//                        //editor.putFloat();
//                        //editor.putLong();
//                        //editor.putInt();
//                        //editor.putStringSet();

                        //최종 커밋
//                        editor.commit();
                    }

                    setResult(RESULT_OK,resIntent);
                }
                finish();
        }
    }
    public void getAddressBook()
    {
        String regExp = "^[가-힣]";
        String name = null;
        String v_id = null;
        Set<String> savePhoneBooks = null;

        ContentResolver resolver = getApplication().getContentResolver();
        Uri phoneUri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = resolver.query(phoneUri,Code.ADDRESS_PROJECTION, null,null, null);

        while (cursor.moveToNext()){
            try {
                v_id = cursor.getString(0);
                name = cursor.getString(1);
                this.phoneBooks.add(name);
            }catch(Exception e) {
                System.out.println(e.toString());
            }
        }
        Iterator<String> iter = this.phoneBooks.iterator();
        while(iter.hasNext())
        {
            System.out.println(iter.next());
        }
        cursor.close();
    }
    @SuppressLint({"HardwareIds","ServiceCast"})
    public void getDeviceInfo()
    {
        // ANDROID_ID는 기기에서 초기화를 수행하거나 APK 서명 키가 변경되면 값이 변경 될 수 있습니다. 우리는 개인별로 훈련 모델이 있기 때문에 아얘 값이 바뀌지 않는 고유값이 필요하다. 일단 이걸로하고 나중에 ㄱㄱ
        this.androidId = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        System.out.println("androidId :" + this.androidId);
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        try {
            this.phoneNumber = tm.getLine1Number();
            System.out.println("getDevice() : " + this.phoneNumber);
        }
        catch (NullPointerException e)
        {
            Toast.makeText(this,"핸드폰 번호가 없습니다.",Toast.LENGTH_LONG).show();
        }

    }
    /* 사용자 계정정보 얻어오기
            AccountManager를 통해 핸드폰 안의 사용자 계정을 가지고 온다. */
        /* ※ android O(오레오)이상 부터는 manifests의 GET_ACCOUNTS 권한만으로는 앱에서 계정정보를 불러올 수 없다...
         아니 그럼 api를 수정했어야 되는거 아닙니까?
         그래서 사용자에게 권한을 받아야하고 AccountManager.newChooseAccountIntent() 또는 인증자의 특정한 메소드를 사용해야 한다.*/

    public void getUserInfo()
    {
        AccountManager acctMgr = AccountManager.get(this); // 사용자계정 전부를 불러온다.
        Account[] acctArray = acctMgr.getAccounts(); // 사용자 계정들을 배열에 입력
        int acctCnt = acctArray.length;
        int row = 0; // 반복문에 사용할 변수
        Account acct; // 필요한 사용자 계정을 입력할 때 쓸 String 변수

                /* AccountManager를 이용해 사용자가 핸드폰에 쓰고 있는 모든 계정, 앱과 연동 되어있는
                계정들을 전부 불러오고 그걸 Account의 배열에 입력한다. 이를 통해서 필요한 계정정보를 찾을 준비가 됨. */

        // 핸드폰에 어떤 계정이 어떠한 유형으로 있는지 알아보는 코드
        while( row < acctCnt)
        {
            acct = acctArray[row];
            if(acct.type.equals("com.google"))
            {
                this.userEmail = acct.name;
                this.emailType = acct.type;
                break;
            }
            row ++;
        }
        System.out.println("이메일 : " + this.userEmail +"\n타 입 : " + this.emailType);
    }
}