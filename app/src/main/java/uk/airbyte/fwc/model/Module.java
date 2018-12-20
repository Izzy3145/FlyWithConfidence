package uk.airbyte.fwc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Module extends RealmObject  {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("display_order")
    @Expose
    private String displayOrder;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("introduction")
    @Expose
    private Object introduction;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("notes")
    @Expose
    private Object notes;
    @SerializedName("presenter")
    @Expose
    private Presenter presenter;
    @SerializedName("media")
    @Expose
    private Media media;
    @SerializedName("bullets")
    @Expose
    private List<String> bullets = null;
    @SerializedName("category")
    @Expose
    private Category category;
    @SerializedName("topic")
    @Expose
    private Topic topic;
    @SerializedName("free")
    @Expose
    private Boolean free;
    @SerializedName("can_view")
    @Expose
    private Boolean canView;

    /**
     * No args constructor for use in serialization
     */
    public Module() {
    }

    /**
     * @param topic
     * @param type
     * @param presenter
     * @param id
     * @param free
     * @param category
     * @param description
     * @param name
     * @param canView
     * @param displayOrder
     * @param notes
     * @param media
     * @param introduction
     * @param bullets
     */
    public Module(String id, String type, String displayOrder, String name, Object introduction, Object description, Object notes, Presenter presenter, Media media, List<String> bullets, Category category, Topic topic, Boolean free, Boolean canView) {
        super();
        this.id = id;
        this.type = type;
        this.displayOrder = displayOrder;
        this.name = name;
        this.introduction = introduction;
        this.description = description;
        this.notes = notes;
        this.presenter = presenter;
        this.media = media;
        this.bullets = bullets;
        this.category = category;
        this.topic = topic;
        this.free = free;
        this.canView = canView;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getIntroduction() {
        return introduction;
    }

    public void setIntroduction(Object introduction) {
        this.introduction = introduction;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public Object getNotes() {
        return notes;
    }

    public void setNotes(Object notes) {
        this.notes = notes;
    }

    public Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public List<String> getBullets() {
        return bullets;
    }

    public void setBullets(List<String> bullets) {
        this.bullets = bullets;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public Boolean getCanView() {
        return canView;
    }

    public void setCanView(Boolean canView) {
        this.canView = canView;
    }

    @RealmClass
    class Category extends RealmObject {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;

        /**
         * No args constructor for use in serialization
         */
        public Category() {
        }

        /**
         * @param id
         * @param name
         */
        public Category(String id, String name) {
            super();
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    @RealmClass
    class Media extends RealmObject {

        @SerializedName("duration")
        @Expose
        private Integer duration;
        @SerializedName("thumbnail")
        @Expose
        private Object thumbnail;
        @SerializedName("placeholder")
        @Expose
        private Object placeholder;
        @SerializedName("video_1080")
        @Expose
        private Object video1080;
        @SerializedName("video_720")
        @Expose
        private Object video720;
        @SerializedName("video_540")
        @Expose
        private Object video540;
        @SerializedName("video_360")
        @Expose
        private Object video360;

        /**
         * No args constructor for use in serialization
         */
        public Media() {
        }

        /**
         * @param video540
         * @param duration
         * @param thumbnail
         * @param placeholder
         * @param video360
         * @param video720
         * @param video1080
         */
        public Media(Integer duration, Object thumbnail, Object placeholder, Object video1080, Object video720, Object video540, Object video360) {
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

        public Object getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(Object thumbnail) {
            this.thumbnail = thumbnail;
        }

        public Object getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(Object placeholder) {
            this.placeholder = placeholder;
        }

        public Object getVideo1080() {
            return video1080;
        }

        public void setVideo1080(Object video1080) {
            this.video1080 = video1080;
        }

        public Object getVideo720() {
            return video720;
        }

        public void setVideo720(Object video720) {
            this.video720 = video720;
        }

        public Object getVideo540() {
            return video540;
        }

        public void setVideo540(Object video540) {
            this.video540 = video540;
        }

        public Object getVideo360() {
            return video360;
        }

        public void setVideo360(Object video360) {
            this.video360 = video360;
        }

    }

    @RealmClass
    class Presenter extends RealmObject {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("description")
        @Expose
        private String description;

        /**
         * No args constructor for use in serialization
         */
        public Presenter() {
        }

        /**
         * @param id
         * @param description
         * @param name
         */
        public Presenter(String id, String name, String description) {
            super();
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    @RealmClass
    class Topic extends RealmObject{

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("price")
        @Expose
        private Integer price;

        /**
         * No args constructor for use in serialization
         */
        public Topic() {
        }

        /**
         * @param id
         * @param price
         * @param name
         */
        public Topic(String id, String name, Integer price) {
            super();
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }
    }
}
