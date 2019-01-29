package uk.airbyte.fwc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Receipt {

    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("receipt")
    @Expose
    private String receipt;

    /**
     * No args constructor for use in serialization
     *
     */
    public Receipt() {
    }

    public Receipt(String topic, String receipt) {
        super();
        this.topic = topic;
        this.receipt = receipt;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
}
