package com.example.sprinkle_android.scenarios;

import android.content.Context;

import java.util.ArrayList;

public class Schedule extends Scenario{

    private String intent;
    private ArrayList<String> entity;
    private ArrayList<String> entityAnswer;

    public Schedule()
    {
        this.intent = "schedule";

        this.entity.add("Date");
        this.entity.add("Subject");
        this.entity.add("Time");
        this.entity.add("Action");
    }

    public String getIntent()
    {
        return this.intent;
    }

    public void runScenario(Context context, String data)
    {
        ;
    }

    public String getRequireAnswer(String entity)
    {
        return "";
    }
}
