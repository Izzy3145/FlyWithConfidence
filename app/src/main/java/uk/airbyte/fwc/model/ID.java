package uk.airbyte.fwc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ID {

    @SerializedName("id")
    @Expose
    private String id;

    /**
     * No args constructor for use in serialization
     */
    public ID() {
    }

    /**
     * @param id
     */
    public ID(String id) {
        super();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

