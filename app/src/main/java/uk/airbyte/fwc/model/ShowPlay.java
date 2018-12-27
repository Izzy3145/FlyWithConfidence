package uk.airbyte.fwc.model;

public class ShowPlay {

    private String image;
    private String thumbnail;
    private String videoUrl;

    public ShowPlay(){};

    public ShowPlay(String image, String thumbnail, String videoUrl){
        this.image = image;
        this.thumbnail = thumbnail;
        this.videoUrl = videoUrl;
    }

    public String getImage() {
        return image;
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
