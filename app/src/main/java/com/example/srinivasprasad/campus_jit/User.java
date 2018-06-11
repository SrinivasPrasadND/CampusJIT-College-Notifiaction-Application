package com.example.srinivasprasad.campus_jit;

public class User {

    String image;
    String name;
    String thumb_url;

    public User(){}

    public User(String image, String name,String thumb_url) {
        this.image = image;
        this.name = name;
        this.thumb_url = thumb_url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }


}
