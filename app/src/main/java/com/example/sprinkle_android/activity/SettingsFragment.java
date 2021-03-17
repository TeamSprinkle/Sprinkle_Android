package com.example.sprinkle_android.activity;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.sprinkle_android.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends PreferenceFragmentCompat{

    private SharedPreferences pref =null;
    private SharedPreferences.Editor editor = null;
    private BaseAdapter baseAdapter =null;
    private EditTextPreference nickName_EPref;
    private ListPreference voiceSelect_LPref;
    private SwitchPreference otherSecretaryFired_SPref;
    private SwitchPreference secretarySay_SPref;
    private SwitchPreference userSay_SPref;

    private String userEmail = null;
    private String emailType = null;
    private final int USERINFO_REQUEST_CODE = 0;
    private String secretaryName = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting, rootKey);
        //별도의 화면 레이아웃파일(layout폴더)을 사용하지 않고
        //설정 xml문서를 통해 화면이 자동 생성
        //res폴더 안에 xml폴더 안에 .xml문서를 만들고
        //<PregerenceScreen>클래스를 통해 화면 설계 시작..
        //addPreferencesFromResource(R.xml.setting);
        init();
    }

    //setting_ep_nickName
    @SuppressLint("CommitPrefEdits")
    public void init()
    {
        //SharedPreference객체를 참조하여 설정상태에 대한 제어 가능..
        //this.pref = getContext().getSharedPreferences("myPref",MODE_PRIVATE);
        this.pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.editor = pref.edit();



        // 설정값 preference 키값 등록
        this.nickName_EPref = findPreference("setting_ep_secretary_name");
        this.voiceSelect_LPref = findPreference("setting_lp_voiceSelect");
        this.otherSecretaryFired_SPref = findPreference("setting_sp_otherSecretaryFired");
        this.secretarySay_SPref = findPreference("setting_sp_secretarySay");
        this.userSay_SPref = findPreference("setting_sp_userSay");
        // 비서이름 초기화
        this.secretaryName = pref.getString("setting_ep_secretary_name","");
        this.nickName_EPref.setSummary(pref.getString("setting_ep_secretary_name",secretaryName));

        //설정값 변경리스너..등록
        this.pref.registerOnSharedPreferenceChangeListener(listener);
    }

    //설정값 변경리스너 객체 맴버변수
    SharedPreferences.OnSharedPreferenceChangeListener listener= new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d("SettingFragment","설정 화면 버튼 이벤트 : " + key);

            if(key.equals("setting_ep_secretary_name"))
            {
                Log.d("settingFragment","비서 이름 설정");
                nickName_EPref.setSummary(nickName_EPref.getText());
                editor.putString("setting_ep_secretary_name",nickName_EPref.getText());
                editor.apply();
                Log.d("settingFragment","비서 이름 변경 : " + nickName_EPref.getText());
            }
            else if(key.equals("setting_lp_voiceSelect"))
            {
            }
            else if(key.equals("setting_sp_otherSecretaryFired"))
            {
            }
            else if(key.equals("setting_sp_secretarySay"))
            {
            }
            else if(key.equals("setting_sp_userSay"))
            {
            }
        }
    };




    /* 사용자 계정정보 얻어오기
        AccountManager를 통해 핸드폰 안의 사용자 계정을 가지고 온다. */
        /* ※ android O(오레오)이상 부터는 manifests의 GET_ACCOUNTS 권한만으로는 앱에서 계정정보를 불러올 수 없다...
         아니 그럼 api를 수정했어야 되는거 아닙니까?
         그래서 사용자에게 권한을 받아야하고 AccountManager.newChooseAccountIntent() 또는 인증자의 특정한 메소드를 사용해야 한다.*/
    public void getUserInfo()
    {
        Intent intent = AccountManager.newChooseAccountIntent(
                null, null, new String[]{"com.google"}, null,
                null, null, null);
        startActivityForResult(intent, USERINFO_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USERINFO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                this.userEmail = extras.getString(AccountManager.KEY_ACCOUNT_NAME);
                this.emailType = extras.getString(AccountManager.KEY_ACCOUNT_TYPE);

                Log.i("SettingFragment","계정 : " + userEmail + "유형 : " + emailType);
            }


        }
        else if(resultCode == RESULT_CANCELED)
        {
            Log.i("SettingFragment","취소 버튼 클릭");
        }
    }
}