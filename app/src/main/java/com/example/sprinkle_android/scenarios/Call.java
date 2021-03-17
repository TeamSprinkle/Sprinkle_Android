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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
            target = (String) answer.get("target");
            Log.d("Call",target);
            
            // 전화걸기
            if(getAddressBook(context,"이경원") == -1)
            {
                return "Fail";

            }
            else
            {
                context.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber)));
                return "Success";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public int getAddressBook(Context context,String target)
    {
        String regExp = "^[가-힣]";
        String v_id = null;
        String name = null;
        String phoneNum = null;

        ContentResolver resolver = context.getContentResolver();
        Uri phoneUri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = resolver.query(phoneUri, Code.ADDRESS_PROJECTION, null,null, null);

        while (cursor.moveToNext()){
            try {
                name = cursor.getString(1);
                phoneNum = cursor.getString(2);
                if(name.equals(target))
                {
                    Log.d("이거 번호 뭐니?",phoneNum);
                    return Integer.parseInt(phoneNum);
                }
            }catch(Exception e) {
                System.out.println(e.toString());
            }
        }
        return -1;
    }
    public String getRequireAnswer(String entity)
    {
        return "";
    }
}
