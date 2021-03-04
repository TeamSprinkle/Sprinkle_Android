package com.example.sprinkle_android.adapter;


public class DataItem {

    private String content;
    private int viewType;

    public DataItem(String content,int viewType) {
        this.content = content;
        this.viewType = viewType;
    }

    public String getContent() {
        return content;
    }

    public int getViewType() {
        return viewType;
    }
    public void setContent(String content)
    {
        this.content = content;
    }
    public void setViewType(int viewType)
    {
        this.viewType = viewType;
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "content='" + content + '\'' +
                ", viewType=" + viewType +
                '}';
    }
}
