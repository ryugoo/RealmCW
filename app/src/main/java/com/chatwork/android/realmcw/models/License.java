package com.chatwork.android.realmcw.models;

public class License {
    private String name;
    private String author;
    private String year;
    private Type type;

    public License(String name, String author, String year, Type type) {
        this.name = name;
        this.author = author;
        this.year = year;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getYear() {
        return year;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        APACHE_V2
    }
}
