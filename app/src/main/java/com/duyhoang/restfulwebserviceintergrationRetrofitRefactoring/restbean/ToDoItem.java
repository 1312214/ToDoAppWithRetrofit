package com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rogerh on 7/15/2018.
 */

public class ToDoItem{

    @SerializedName("authorEmailId")
    private String authorEmailId;

    @SerializedName("date")
    private long date;

    @SerializedName("id")
    private int id;

    @SerializedName("place")
    private String place;

    @SerializedName("todoString")
    private String todoString;




    public ToDoItem(int id, String todoString, String place, String authorMail){
        this.id = id;
        this.todoString = todoString;
        this.date = new Date().getTime();
        this.place = place;
        this.authorEmailId = authorMail;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTodoString() {
        return todoString;
    }

    public void setTodoString(String todoString) {
        this.todoString = todoString;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAuthorMail() {
        return authorEmailId;
    }

    public void setAuthorMail(String authorMail) {
        this.authorEmailId = authorMail;
    }
}
