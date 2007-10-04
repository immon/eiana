package org.iana.rzm.web.common;

import java.io.*;

public class RequestMetaParameters implements Serializable {

    private String email;
    private String comment;


    public RequestMetaParameters(String email, String comment) {
        this.email = email;
        this.comment = comment;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RequestMetaParameters that = (RequestMetaParameters) o;

        if (comment != null ? !comment.equals(that.comment) : that.comment != null) {
            return false;
        }
        if (email != null ? !email.equals(that.email) : that.email != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (email != null ? email.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }
}
