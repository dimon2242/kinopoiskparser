package com.example.dmitry.kinopoiskparser;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.UUID;

public class Movie implements Serializable {

    private String mTitle;
    private Bitmap mSmallThumbBitmap;
    private Bitmap mBigThumbBitmap;
    private String mURL;
    private String mProducer;
    private String mFullTitle;
    private UUID mId;
    private String mDescription;
    private float mRating;

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public String getFullTitle() {
        return mFullTitle;
    }

    public void setFullTitle(String fullTitle) {
        mFullTitle = fullTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Movie() {
        mId = UUID.randomUUID();
    }

    public UUID getId() {
        return mId;
    }

    public Bitmap getBigThumbBitmap() {
        return mBigThumbBitmap;
    }

    public void setBigThumbBitmap(Bitmap bigThumbBitmap) {
        mBigThumbBitmap = bigThumbBitmap;
    }

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

    public Bitmap getSmallThumbBitmap() {
        return mSmallThumbBitmap;
    }

    public void setSmallThumbBitmap(Bitmap smallThumbBitmap) {
        mSmallThumbBitmap = smallThumbBitmap;
    }
}
