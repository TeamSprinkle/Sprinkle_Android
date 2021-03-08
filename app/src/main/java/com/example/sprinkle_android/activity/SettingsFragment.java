package com.example.sprinkle_android.activity;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.example.sprinkle_android.R;

import static android.app.Activity.RESULT_OK;
import static android.app.Activity.RESULT_CANCELED;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences pref;
    private EditTextPreference nickName_EPref;
    private ListPreference voiceSelect_LPref;
    private SwitchPreference otherSecretaryFired_SPref;
    private SwitchPreference secretarySay_SPref;
    private SwitchPreference userSay_SPref;

    private String userEmail = null;
    private String emailType = null;

    private final int USERINFO_REQUEST_CODE = 0;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting, rootKey);
        //별도의 화면 레이아웃파일(layout폴더)을 사용하지 않고
        //설정 xml문서를 통해 화면이 자동 생성
        //res폴더 안에 xml폴더 안에 .xml문서를 만들고
        //<PregerenceScreen>클래스를 통해 화면 설계 시작..
        //addPreferencesFromResource(R.xml.setting);

        //SharedPreference객체를 참조하여 설정상태에 대한 제어 가능..
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }


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