package com.example.sprinkle_android.scenarios;

import android.content.Context;

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
        this.entity.add("target");

        this.entityAnswer.add("누구에게 전화 걸까요?");
    }

    public String getIntent()
    {
        return this.intent;
    }

    public void runScenario(Context context, String answer)
    {
        JSONObject data = null;
        String target = "";

        try {
            data = new JSONObject(answer);
            target = (String) data.get("target");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getRequireAnswer(String entity)
    {
        return "";
    }
}
