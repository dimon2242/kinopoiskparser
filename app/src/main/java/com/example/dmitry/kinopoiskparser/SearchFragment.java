package com.example.dmitry.kinopoiskparser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class SearchFragment extends Fragment {

    private List<Movie> mMovies;
    private MoviesSingleton mMoviesSingleton;
    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mSearchEditText;
    private Toolbar mToolbar;
    private String mSearchTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mMoviesRecyclerView = (RecyclerView) v.findViewById(R.id.search_list_recycler_view);
        mMoviesRecyclerView.setLayoutManager(mLinearLayoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), mLinearLayoutManager.getOrientation());
        mMoviesRecyclerView.addItemDecoration(divider);
        mSearchEditText = (EditText) ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar_search_edit_text);

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchTitle = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSearchEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch(mSearchTitle);
                    return true;
                }
                return false;
            }
        });

        mMoviesSingleton = MoviesSingleton.get(getActivity());

        return v;
    }

    private void startSearch(String title) {
        mMoviesSingleton.clear();
        SearchInfoDownloader SID = new SearchInfoDownloader();
        SID.execute(urlConstructor(title));
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

    private class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mProducerTextView;
        private ImageView mPicImageView;
        private Movie mMovie;

        public MovieHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_movie_title_text_view);
            mProducerTextView = (TextView) itemView.findViewById(R.id.list_item_movie_producer_text_view);
            mPicImageView = (ImageView) itemView.findViewById(R.id.list_item_movie_thumb_image_view);
        }

        public void bindMovie(Movie movie) {
            mMovie = movie;
            mTitleTextView.setText(mMovie.getTitle());
            mProducerTextView.setText(mMovie.getProducer());
            mPicImageView.setImageBitmap(mMovie.getSmallThumbBitmap());
        }

        @Override
        public void onClick(View v) {
            startActivity(MovieDetailActivity.newIntent(getActivity(), mMovie.getId()));
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
                        m.setSmallThumbBitmap(ImageDownloader.get("https://www.kinopoisk.ru/images/sm_film/" + imgNumber + ".jpg"));
                        m.setProducer(producer);
                        m.setURL(movieUrl);
                        mMoviesSingleton.addMovie(m);
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
        mMovies = mMoviesSingleton.getMovies();
        mAdapter = new MovieAdapter(mMovies);
        mMoviesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            if(mSearchEditText.getVisibility() == View.GONE) {
                mSearchEditText.setVisibility(View.VISIBLE);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            } else {
                mSearchEditText.setVisibility(View.GONE);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mSearchTitle);
                startSearch(mSearchTitle);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
