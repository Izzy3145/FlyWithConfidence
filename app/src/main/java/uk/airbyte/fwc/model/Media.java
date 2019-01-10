package uk.airbyte.fwc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

   @RealmClass
    public class Media extends RealmObject {

       //TODO: change PrimaryKey?

       @PrimaryKey
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("captain_placeholder")
    @Expose
    private String placeholder;
    @SerializedName("video_1080")
    @Expose
    private String video1080;
    @SerializedName("video_720")
    @Expose
    private String video720;
    @SerializedName("video_540")
    @Expose
    private String video540;
    @SerializedName("video_360")
    @Expose
    private String video360;

    /**
     * No args constructor for use in serialization
     *
     */
    public Media() {
    }

    /**
     *
     * @param video540
     * @param duration
     * @param thumbnail
     * @param placeholder
     * @param video360
     * @param video720
     * @param video1080
     */
    public Media(Integer duration, String thumbnail, String placeholder, String video1080, String video720, String video540, String video360) {
        super();
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.placeholder = placeholder;
        this.video1080 = video1080;
        this.video720 = video720;
        this.video540 = video540;
        this.video360 = video360;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getVideo1080() {
        return video1080;
    }

    public void setVideo1080(String video1080) {
        this.video1080 = video1080;
    }

    public String getVideo720() {
        return video720;
    }

    public void setVideo720(String video720) {
        this.video720 = video720;
    }

    public String getVideo540() {
        return video540;
    }

    public void setVideo540(String video540) {
        this.video540 = video540;
    }

    public String getVideo360() {
        return video360;
    }

    public void setVideo360(String video360) {
        this.video360 = video360;
    }

}
