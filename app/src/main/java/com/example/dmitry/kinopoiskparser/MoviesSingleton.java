package com.example.dmitry.kinopoiskparser;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MoviesSingleton {

    private static MoviesSingleton sMoviesSingleton;
    private List<Movie> mMovies;

    public static MoviesSingleton get(Context context) {
        if(sMoviesSingleton == null) {
            sMoviesSingleton = new MoviesSingleton(context);
        }

        return sMoviesSingleton;
    }

    private MoviesSingleton(Context context) {
        mMovies = new ArrayList<>();
    }

    public List<Movie> getMovies() {
        return mMovies;
    }

    public Movie getMovie(UUID id) {
        for (Movie movie : mMovies) {
            if (movie.getId().equals(id)) {
                return movie;
            }
        }
        return null;
    }

    public void clear() {
        mMovies.clear();
    }

    public void addMovie(Movie movie) {
        mMovies.add(movie);
    }
}
