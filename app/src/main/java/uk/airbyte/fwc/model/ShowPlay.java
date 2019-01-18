package uk.airbyte.fwc.model;

import javax.annotation.Nullable;

public class ShowPlay {

    private String moduleID;
    private String image;
    private String thumbnail;
    private String videoUrl;
    private int currentWindow;
    private long playerPosition;
    private boolean isIntro;

    public ShowPlay(){};

    public ShowPlay(@Nullable  String moduleID, @Nullable String image, @Nullable String thumbnail, @Nullable String videoUrl,
                    int currentWindow, long playerPosition, boolean isIntro){
        this.moduleID = moduleID;
        this.image = image;
        this.thumbnail = thumbnail;
        this.videoUrl = videoUrl;
        this.currentWindow = currentWindow;
        this.playerPosition = playerPosition;
        this.isIntro = isIntro;
    }

    public String getImage() {
        return image;
    }

    public boolean isIntro() {
        return isIntro;
    }

    public void setIntro(boolean intro) {
        isIntro = intro;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(@Nullable String moduleID) {
        this.moduleID = moduleID;
    }

    public void setImage(@Nullable String image) {
        this.image = image;
    }

    public int getCurrentWindow() {
        return currentWindow;
    }

    public void setCurrentWindow(int currentWindow) {
        this.currentWindow = currentWindow;
    }

    public long getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(long playerPosition) {
        this.playerPosition = playerPosition;
    }

    public @Nullable String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(@Nullable String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public @Nullable String getVideoUrl() {
        return  videoUrl;
    }

    public void setVideoUrl(@Nullable String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
