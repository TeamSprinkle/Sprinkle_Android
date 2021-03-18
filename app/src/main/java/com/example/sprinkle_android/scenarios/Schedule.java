package com.example.sprinkle_android.scenarios;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Schedule extends Scenario{

    private String intent;
    private ArrayList<String> entity;
    private ArrayList<String> entityAnswer;

    public Schedule()
    {
        this.intent = "schedule";

        this.entity = new ArrayList<String>();
        this.entityAnswer = new ArrayList<String>();
        this.entity.add("date"); // 기본값 오늘 날짜
        this.entity.add("subject"); // 기본값 일정
        this.entity.add("time"); // 기본값 지금
        this.entity.add("action"); // 기본값 잡다
    }

    public String getIntent()
    {
        return this.intent;
    }

    public String runScenario(Context context, JSONObject data)
    {
        String date = null;
        String time = null;
        String title = null;
        String description = null;
        
        try {
            date = data.getString("date");
            time = data.getString("time");
            title = data.getString("subject");
            description = data.getString("subject"); // 설명이 추가되면 action으로 변경
            eventAdd(context,date,time,title,description);

        }
        catch (Exception e)
        {
        }
        return "";
    }

    public String getRequireAnswer(String entity)
    {
        return "";
    }
    public String eventAdd(Context context, String date, String time, String title, String description)
    {
        long calID = 1; // 캘린더 id값 불러오는 걸로 바꿔야함
        long startMillis = 0;
        long endMillis = 0;

        StringTokenizer stringTokenizer = new StringTokenizer(date,"-");
        int tokenCnt = stringTokenizer.countTokens();
        String[] dateToken = new String[tokenCnt];
        int startYear = Integer.parseInt(dateToken[0].replaceAll("[^0-9]",""));
        int startMonth = Integer.parseInt(dateToken[1].replaceAll("[^0-9]",""));
        int startDay = Integer.parseInt(dateToken[2].replaceAll("[^0-9]",""));

        for(int i = 0 ; i < tokenCnt ; i++)
        {
            dateToken[i] = stringTokenizer.nextToken();
            System.out.println("데이터 확인 : " + dateToken[i]);
        }



        System.out.println("데이터 확인 : " + date);
        System.out.println("데이터 확인 : " + time);
        System.out.println("데이터 확인 : " + title);
        System.out.println("데이터 확인 : " + description);


//        Calendar beginTime = Calendar.getInstance();
//        beginTime.set(2021, 2, 25, 7, 30);
//        startMillis = beginTime.getTimeInMillis();
//        //Log.d("이거 몇시? : ",startMillis);
//        Calendar endTime = Calendar.getInstance();
//        endTime.set(2021, 2, 25, 8, 45);
//        endMillis = endTime.getTimeInMillis();
//
//        ContentResolver cr = context.getContentResolver();
//        ContentValues values = new ContentValues();
//        values.put(CalendarContract.Events.DTSTART, startMillis);
//        values.put(CalendarContract.Events.DTEND, endMillis);
//        values.put(CalendarContract.Events.TITLE, "대체 어디에 추가 되는거니??");
//        values.put(CalendarContract.Events.DESCRIPTION, "이건 어디에 나오는거고??");
//        values.put(CalendarContract.Events.CALENDAR_ID, calID);
//        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Korea");

        return "";
    }
}
