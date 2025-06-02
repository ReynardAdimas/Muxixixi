package com.example.muxixixi.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Song {
//    private String name;
//    public Song(String name){
//        this.name = name;
//    }
//
//    public String getName(){
//        return name;
//    }
//
//    public StringProperty nameProperty(){
//        return name;
//    }
    private final SimpleStringProperty name;
    public Song(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getName(){
        return name.get();
    }

    public void setName(String name){
        this.name.set(name);
    }

    public StringProperty nameProperty(){
        return name;
    }
}
