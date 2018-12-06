package uk.airbyte.fwc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reminder {

    @SerializedName("sent")
    @Expose
    private Boolean sent;

    /**
     * No args constructor for use in serialization
     *
     */
    public Reminder() {
    }

    /**
     *
     * @param sent
     */
    public Reminder(Boolean sent) {
        super();
        this.sent = sent;
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

}
