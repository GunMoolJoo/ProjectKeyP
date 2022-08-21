package com.example.myapplication;

public class StoryData {
    private String title;
    private String mainStory;

    public StoryData(String title, String mainStory){
        this.title = title;
        this.mainStory = mainStory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainStory() {
        return mainStory;
    }

    public void setMainStory(String mainStory) {
        this.mainStory = mainStory;
    }
}

