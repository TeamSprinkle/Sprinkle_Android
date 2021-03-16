package com.example.sprinkle_android.scenarios;

import android.content.Context;

public abstract class Scenario {
    private String intent;

    public abstract String getIntent();
    public abstract void runScenario(Context context, String data);
    public abstract String getRequireAnswer(String entity);
}
