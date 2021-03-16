package com.example.sprinkle_android.scenarios;

import android.content.Context;

public class Schedule extends Scenario{

    private String intent;

    public Schedule()
    {
        this.intent = "schedule";
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
