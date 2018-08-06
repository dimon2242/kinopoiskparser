package com.example.dmitry.kinopoiskparser;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.UUID;

public class MovieDetailFragment extends Fragment {

    private static final String TAG = "movie_detail_frag_tag";
    private static final String ARG_MOVIE_ID = "arg_movie_id";
    private Movie mMovie;
    private ImageView mImageView;
    private TextView mDescriptionTextView;
    private TextView mFullTitleTextView;
    private TextView mRatingTextView;
    private MovieDetailInfoDownloader MDID;

    public static Fragment newInstance(UUID movieId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE_ID, movieId);
        Fragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        mImageView = (ImageView) v.findViewById(R.id.fragment_detail_thumb_pic);
        mDescriptionTextView = (TextView) v.findViewById(R.id.fragment_detail_description);
        mFullTitleTextView = (TextView) v.findViewById(R.id.fragment_detail_title);
        mRatingTextView = (TextView) v.findViewById(R.id.fragment_detail_rating);
        UUID movieId = (UUID) getArguments().getSerializable(ARG_MOVIE_ID);
        mMovie = MoviesSingleton.get(getActivity()).getMovie(movieId);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mMovie.getTitle());
        Log.d("MovieDetailF", mMovie.getURL());
        MDID = new MovieDetailInfoDownloader();
        MDID.execute(mMovie);
        return v;
    }

    private String findPicUrl(Movie movie) throws IOException {
        Document doc;
        String picUrl = null;
        doc = Jsoup.connect(movie.getURL()).get();
        Elements linkTags = doc.getElementsByTag("link");
        for(Element linkTag : linkTags) {
            String rel = linkTag.attr("rel");
            if("image_src".equals(rel)) {
                picUrl = linkTag.attr("href");
                Log.d(TAG, picUrl);
                movie.setBigThumbBitmap(ImageDownloader.get(picUrl));
                break;
            }
        }
        return picUrl;
    }

    private float findRating(Movie movie) throws IOException {
        Document doc;
        float rating = 0;
        doc = Jsoup.connect(movie.getURL()).get();
        Elements metas = doc.select("meta[itemprop=ratingValue]");
        for (Element meta : metas) {
            String itemprop = meta.attr("itemprop");
            if("ratingValue".equals(itemprop)) {
                rating = Float.valueOf(meta.attr("content"));
                break;
            }
        }
        return rating;
    }

    private String findDescription(Movie movie) throws IOException {
        Document doc;
        String description = null;
        doc = Jsoup.connect(movie.getURL()).get();
        Elements tags = doc.select("div[class=brand_words film-synopsys]");
        for(Element tag : tags) {
            String itemprop = tag.attr("itemprop");
            if("description".equals(itemprop)) {
                description = tag.text();
                break;
            }
        }
        return description;
    }

    private String findTitle(Movie movie) throws IOException {
        Document doc;
        String title = null;
        doc = Jsoup.connect(movie.getURL()).get();
        Elements metaTags = doc.getElementsByTag("meta");
        for (Element metaTag : metaTags) {
            String property = metaTag.attr("property");
            String content = metaTag.attr("content");
            if ("title".equals(property)) {
                title = content;
                break;
            }
        }
        return title;
    }

    private class MovieDetailInfoDownloader extends AsyncTask<Movie, Void, Void> {

        @Override
        protected Void doInBackground(Movie... movies) {
            try {
                for (Movie movie : movies) {
                    movie.setBigThumbBitmap(ImageDownloader.get(findPicUrl(movie)));
                    movie.setDescription(findDescription(movie));
                    movie.setFullTitle(findTitle(movie));
                    movie.setRating(findRating(movie));
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updateUI();
        }
    }

    private void updateUI() {
        mImageView.setImageBitmap(mMovie.getBigThumbBitmap());
        mDescriptionTextView.setText(mMovie.getDescription());
        mFullTitleTextView.setText(mMovie.getFullTitle());
        mRatingTextView.setText(getString(R.string.rating_title) + " " + String.valueOf(mMovie.getRating()) + "/10");
    }
}
