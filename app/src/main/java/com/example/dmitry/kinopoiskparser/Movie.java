package com.example.dmitry.kinopoiskparser;

import android.graphics.Bitmap;

public class Movie {

    private String mTitle;
    private Bitmap mBitmap;
    private String mURL;
    private String mProducer;

    public String getProducer() {
        return mProducer;
    }

    public void setProducer(String producer) {
        mProducer = producer;
    }

    public String getURL() {
        return mURL;
    }

    public void setURL(String URL) {
        mURL = URL;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
