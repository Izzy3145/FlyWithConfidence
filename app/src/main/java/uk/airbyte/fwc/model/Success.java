package uk.airbyte.fwc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Success {

    //TODO: ASK - is there a better way to do this?

        @SerializedName("success")
        @Expose
        private Boolean success;

        /**
         * No args constructor for use in serialization
         *
         */
        public Success() {
        }

        /**
         *
         * @param success
         */
        public Success(Boolean success) {
            super();
            this.success = success;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

}
