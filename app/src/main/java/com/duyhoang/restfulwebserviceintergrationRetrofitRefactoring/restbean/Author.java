package com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rogerh on 7/15/2018.
 */

public class Author {

    @SerializedName("authorId")
    private long authorId;

    @SerializedName("authorName")
    private String authorName;

    @SerializedName("authorEmailId")
    private String authorEmailId;

    @SerializedName("authorPassword")
    private String authorPassword;

    public Author(){}

    public Author(long authorId, String authorName, String authorEmailId, String authorPassword) {
        super();
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorEmailId = authorEmailId;
        this.authorPassword = authorPassword;
    }


    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmailId() {
        return authorEmailId;
    }

    public void setAuthorEmailId(String authorEmailId) {
        this.authorEmailId = authorEmailId;
    }

    public String getAuthorPassword() {
        return authorPassword;
    }

    public void setAuthorPassword(String authorPassword) {
        this.authorPassword = authorPassword;
    }

    public String toString(){
        return "("+this.authorEmailId+", "+this.authorPassword+", "+this.authorId+", "+this.authorName+")";
    }
}
