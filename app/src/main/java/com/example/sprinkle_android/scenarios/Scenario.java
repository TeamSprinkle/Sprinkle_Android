package com.example.sprinkle_android.scenarios;

import android.content.Context;

import org.json.JSONObject;

public abstract class Scenario {
    private String intent;

    public abstract String getIntent();
    public abstract String runScenario(Context context, JSONObject data);
    public abstract String getRequireAnswer(String entity);
}
