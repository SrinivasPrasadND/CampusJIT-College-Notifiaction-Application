package com.example.srinivasprasad.campus_jit;

import android.support.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

public class BlogPostId {

    @Exclude
    public String BlogPostId;

    public <T extends BlogPostId> T withId(@Nullable final String id){
        this.BlogPostId=id;
        return (T) this;
    }
}
