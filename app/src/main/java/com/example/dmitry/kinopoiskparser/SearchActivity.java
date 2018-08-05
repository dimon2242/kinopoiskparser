package com.example.dmitry.kinopoiskparser;

import android.support.v4.app.Fragment;

public class SearchActivity extends CommonAbstractActivityHost {

    @Override
    protected Fragment createFragment() {
        return new SearchFragment();
    }
}
