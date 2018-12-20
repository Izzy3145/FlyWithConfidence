package uk.airbyte.fwc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Topic {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("purchased")
    @Expose
    private Boolean purchased;
    @SerializedName("category")
    @Expose
    private Category category;

    /**
     * No args constructor for use in serialization
     */
    public Topic() {
    }

    /**
     * @param purchased
     * @param id
     * @param category
     * @param price
     * @param description
     * @param name
     */
    public Topic(String id, String name, String description, Integer price, Boolean purchased, Category category) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.purchased = purchased;
        this.category = category;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    class Category {

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

}


