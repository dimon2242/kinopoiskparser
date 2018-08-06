package com.example.dmitry.kinopoiskparser;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class MovieDetailActivity extends CommonAbstractActivityHost {

    private static final String EXTRA_MOVIE_ID = "extra_movie_id";

    public static Intent newIntent(Context packageContext, UUID movieId) {
        Intent intent = new Intent(packageContext, MovieDetailActivity.class);
        intent.putExtra(EXTRA_MOVIE_ID, movieId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID movieId = (UUID) getIntent().getSerializableExtra(EXTRA_MOVIE_ID);
        return MovieDetailFragment.newInstance(movieId);
    }
}
