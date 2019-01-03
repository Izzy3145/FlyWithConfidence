package uk.airbyte.fwc.model;

import javax.annotation.Nullable;

public class ShowPlay {

    private String moduleID;
    private String image;
    private String thumbnail;
    private String videoUrl;

    public ShowPlay(){};

    public ShowPlay(@Nullable  String moduleID, String image, String thumbnail, String videoUrl){
        this.moduleID = moduleID;
        this.image = image;
        this.thumbnail = thumbnail;
        this.videoUrl = videoUrl;
    }

    public String getImage() {
        return image;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(@Nullable String moduleID) {
        this.moduleID = moduleID;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
