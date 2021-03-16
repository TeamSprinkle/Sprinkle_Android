package com.example.sprinkle_android.scenarios;

public abstract class Scenario {
    private String intent;

    public abstract String getIntent();
    public abstract void runScenario(String data);
}
