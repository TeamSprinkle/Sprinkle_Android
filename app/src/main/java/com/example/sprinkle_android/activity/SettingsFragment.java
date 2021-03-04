package com.example.sprinkle_android.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import androidx.preference.PreferenceFragmentCompat;

import com.example.sprinkle_android.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences pref;
    private EditTextPreference nickName_EPref;
    private ListPreference voiceSelect_LPref;
    private SwitchPreference otherSecretaryFired_SPref;
    private SwitchPreference secretarySay_SPref;
    private SwitchPreference userSay_SPref;

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
}