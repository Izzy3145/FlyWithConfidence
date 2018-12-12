package uk.airbyte.fwc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("email_address")
    @Expose
    private String emailAddress;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("first_name")
    @Expose
    private String firstName;

    /**
     * No args constructor for use in serialization
     *
     */
    public Login() {
    }

    public Login(String emailAddress) {
        super();
        this.emailAddress = emailAddress;
    }

    public Login(String password, String emailAddress) {
        super();
        this.password = password;
        this.emailAddress = emailAddress;
    }

    public Login(String emailAddress, String lastName, String firstName) {
        super();
        this.emailAddress = emailAddress;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public Login(String password, String emailAddress, String lastName, String firstName) {
        super();
        this.password = password;
        this.emailAddress = emailAddress;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

}
