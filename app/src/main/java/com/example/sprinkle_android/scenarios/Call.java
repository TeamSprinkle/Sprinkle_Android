package com.example.sprinkle_android.scenarios;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.sprinkle_android.adapter.Code;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Call extends Scenario{

    private String intent;
    private ArrayList<String> entity;
    private ArrayList<String> entityAnswer;

    public Call()
    {
        this.intent = "call";
        this.entity = new ArrayList<String>();
        this.entityAnswer = new ArrayList<String>();
        this.entity.add("target");
        this.entityAnswer.add("누구에게 전화 걸까요?");
    }

    public String getIntent()
    {
        return this.intent;
    }

    public String runScenario(Context context, JSONObject answer)
    {
        String target = "";
        String phoneNumber = "";

        try {
            target = (String)answer.get("target");
            Log.d("Call",target);

            phoneNumber = getAddressBook(context,target);
            // 전화걸기
            if(phoneNumber == "Fail")
            {
                return "Fail";

            }
            else
            {
                Log.d("Call","전화번호 : "+ phoneNumber);
                context.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber)));
                return "Success";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getAddressBook(Context context,String target)
    {
        String name = null;
        String v_id = null;
        String phoneNum = null;

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,Code.ADDRESS_PROJECTION, ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",null, null);
        Cursor phoneCursor;

        while (cursor.moveToNext()){

            v_id = cursor.getString(0);
            System.out.println("id : " + v_id);
            name = cursor.getString(1);
            System.out.println("이름 : " + name);

            phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,Code.ADDRESS_PHONE_PROJECTION,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + v_id,null,null);

            while (phoneCursor.moveToNext()){
                phoneNum = phoneCursor.getString(0);
                System.out.println("번호 : " + phoneNum);
                if(name.equals(target))
                {
                    return phoneNum.replaceAll("[^0-9]","");
                }
            }
        }
        return "Fail";
    }
    public String getRequireAnswer(String entity)
    {
        return "";
    }
}
