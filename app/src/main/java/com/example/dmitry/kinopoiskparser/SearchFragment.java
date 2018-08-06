package com.example.dmitry.kinopoiskparser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private List<Movie> mMovies;
    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mMoviesRecyclerView = (RecyclerView) v.findViewById(R.id.search_list_recycler_view);
        mMoviesRecyclerView.setLayoutManager(mLinearLayoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), mLinearLayoutManager.getOrientation());
        mMoviesRecyclerView.addItemDecoration(divider);

        mMovies = new ArrayList<>();
        SearchInfoDownloader SID = new SearchInfoDownloader();
        SID.execute(urlConstructor("мстители"));
        //updateUI();
        return v;
    }

    private String urlConstructor(String request) {
        String url = "https://www.kinopoisk.ru/index.php?kp_query=";
        try {
             url += URLEncoder.encode(request, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    private Bitmap thumbDownloader(String url) {
        Bitmap thumb = null;
        try {
            InputStream is = new URL(url).openStream();
            thumb = BitmapFactory.decodeStream(is);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return thumb;
    }

    private class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mProducerTextView;
        private ImageView mPicImageView;
        private Movie mMovie;

        public MovieHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_movie_title_text_view);
            mProducerTextView = (TextView) itemView.findViewById(R.id.list_item_movie_producer_text_view);
            mPicImageView = (ImageView) itemView.findViewById(R.id.list_item_movie_thumb_image_view);
        }

        public void bindMovie(Movie movie) {
            mMovie = movie;
            mTitleTextView.setText(mMovie.getTitle());
            mProducerTextView.setText(mMovie.getProducer());
            mPicImageView.setImageBitmap(mMovie.getBitmap());
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

        private List<Movie> mMovies;

        public MovieAdapter(List<Movie> movies) {
            mMovies = movies;
        }

        @Override
        public MovieHolder onCreateViewHolder(ViewGroup parent, int typeView) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View v = layoutInflater.inflate(R.layout.list_item_movie, parent, false);

            return new MovieHolder(v);
        }

        @Override
        public void onBindViewHolder(MovieHolder holder, int position) {
            Movie movie = mMovies.get(position);
            holder.bindMovie(movie);
        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }
    }

    private class SearchInfoDownloader extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            Document doc;
            try {
                for (String url : urls) {
                    doc = Jsoup.connect(url).get();
                    Elements movies = doc.select(".info");
                    for(Element movie : movies) {
                        String title = movie.select(".name a").text() + " / " + movie.selectFirst(".gray").text() + " " + movie.select(".name .year").text();
                        String imgNumber = movie.selectFirst(".name a").attr("data-id");
                        String producer = movie.select(".gray i a").text();
                        String movieUrl = "https://www.kinopoisk.ru" + movie.selectFirst("a").attr("data-url");
                        Log.d("ASYNC", title + " " + movie.select(".gray i a").text() + " " + movieUrl);
                        Movie m = new Movie();
                        m.setTitle(title);
                        m.setBitmap(thumbDownloader("https://www.kinopoisk.ru/images/sm_film/" + imgNumber + ".jpg"));
                        m.setProducer(producer);
                        m.setURL(movieUrl);
                        mMovies.add(m);
                    }
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
        mAdapter = new MovieAdapter(mMovies);
        mMoviesRecyclerView.setAdapter(mAdapter);
    }
}
