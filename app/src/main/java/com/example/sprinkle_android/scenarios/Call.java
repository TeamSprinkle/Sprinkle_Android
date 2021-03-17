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
            if(getAddressBook(context,target)) {
                context.startActivity(new Intent("android.intent.action.CALL", Uri.parse(phoneNumber)));
                return "성공";
            }
            else
            {
                return "실패";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public boolean getAddressBook(Context context,String target)
    {
        List<String> phoneBooks = new ArrayList<String>();
        String regExp = "^[가-힣]";
        String name = null;
        String v_id = null;
        Set<String> savePhoneBooks = null;
        Iterator<String> iter = phoneBooks.iterator();


        ContentResolver resolver = context.getContentResolver();
        Uri phoneUri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = resolver.query(phoneUri, Code.ADDRESS_PROJECTION, null,null, null);

        while (cursor.moveToNext()){
            try {
                name = cursor.getString(1);
                phoneBooks.add(name);
            }catch(Exception e) {
                System.out.println(e.toString());
            }
        }

        while(iter.hasNext())
        {
            if(iter.next().equals(target))
            {
                return true;
            }
        }
        cursor.close();
        return false;
    }
    public String getRequireAnswer(String entity)
    {
        return "";
    }
}
