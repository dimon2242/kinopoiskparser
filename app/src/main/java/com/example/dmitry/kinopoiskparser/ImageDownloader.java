package com.example.dmitry.kinopoiskparser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageDownloader {

    private static InputStream mInputStream;
    private static Bitmap mBitmap;

    public static Bitmap get(String url) {
        mBitmap = null;
        try {
            mInputStream = new URL(url).openStream();
            mBitmap = BitmapFactory.decodeStream(mInputStream);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return mBitmap;
    }
}
