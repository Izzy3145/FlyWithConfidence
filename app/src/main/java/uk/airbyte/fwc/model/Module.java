package uk.airbyte.fwc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Module extends RealmObject {

    @PrimaryKey
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
    private String introduction;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("presenter")
    @Expose
    private Presenter presenter;
    @SerializedName("media")
    @Expose
    private Media media;
    @SerializedName("bullets")
    @Expose
    private RealmList<String> bullets = null;
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
     *
     */
    public Module() {
    }

    /**
     *
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
    public Module(String id, String type, String displayOrder, String name, String introduction, String description, String notes, Presenter presenter, Media media, RealmList<String> bullets, Category category, Topic topic, Boolean free, Boolean canView) {
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
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

    public RealmList<String> getBullets() {
        return bullets;
    }

    public void setBullets(RealmList<String> bullets) {
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

}

