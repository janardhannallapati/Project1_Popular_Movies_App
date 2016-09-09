package android.inoss.com.popularmoviesapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inoss.com.popularmoviesapp.R;
import android.inoss.com.popularmoviesapp.activities.MovieDetailsActivity;
import android.inoss.com.popularmoviesapp.adapters.MovieAdapter;
import android.inoss.com.popularmoviesapp.beans.Movie;
import android.inoss.com.popularmoviesapp.constants.Constants;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by janardhan on 04-Sep-16.
 */

public class MovieListFragment extends Fragment {

    public static final String LOG_TAG = MovieListFragment.class.getSimpleName();


    private MovieAdapter movieAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        List<Movie> emptyData = new ArrayList<Movie>();
        movieAdapter = new MovieAdapter(getActivity(), emptyData);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);
                Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, movie);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesList();
    }

    private void updateMoviesList() {
        String sortBy = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortBy = preferences.getString(getString(R.string.pref_movies_sort_by_key), getString(R.string.pref_movies_sort_by_default));

        if (!isOnline()) {
            Toast.makeText(getActivity(), R.string.network_down_message,Toast.LENGTH_LONG).show();
            return ;
        }

        new FetchMoviesListTask().execute(sortBy);
    }


    private class FetchMoviesListTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            String sortBy = params[0];
            String POPULAR_MOVIES_BASE_URI = "http://api.themoviedb.org/3/movie/" + sortBy + "?";
            String APIKEY = "api_key";
            Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URI).buildUpon()
                    .appendQueryParameter(APIKEY, Constants.API_KEY)
                    .build();
            Log.v(LOG_TAG, builtUri.toString());
            URL url = null;
            BufferedReader reader = null;
            HttpURLConnection connection = null;
            StringBuffer buffer = new StringBuffer();



            try {
                //steps for HTTPRequest for weather data were explained in lecture 2.6
                //1.Make HTTP request ( we use HttpUrlConnection rather than apache HttpClient )
                //2.Read response from input stream
                //3. clean up and log any errors

                url = new URL(builtUri.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //This line will throw NetworkOnMainThreadException if it is used outside this AsyncTask
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");

                }
                if (buffer.length() == 0) {
                    return null;
                }

                //return getMaxTemperatureForDay(buffer.toString(),getActivity(),tag);
                try {
                    return getMoviesDataFromJson(buffer.toString());
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "Error Closing stream", e);
                    }
                }
            }

            return null;

        }

        private Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String MOVIE_ORIGINAL_TITLE = "original_title";
            final String MOVIE_POSTER_IMAGE_THUMBNAIL = "poster_path";
            final String PLOT_SYNOPSIS = "overview";
            final String USER_RATING = "vote_average";
            final String RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            Movie[] movies = new Movie[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieJsonObj = (JSONObject) movieArray.get(i);
                String originalTitle = movieJsonObj.getString(MOVIE_ORIGINAL_TITLE);
                String posterPath = movieJsonObj.getString(MOVIE_POSTER_IMAGE_THUMBNAIL);
                String overview = movieJsonObj.getString(PLOT_SYNOPSIS);
                Double userRating = movieJsonObj.getDouble(USER_RATING);
                String releaseDate = movieJsonObj.getString(RELEASE_DATE);
                Movie movie = new Movie();
                movie.setOriginalTitle(originalTitle);
                movie.setPosterPath(posterPath);
                movie.setOverview(overview);
                movie.setVoteAverage(userRating);
                movie.setReleaseDate(releaseDate);
                movies[i] = movie;
                //	original title
                //	movie poster image thumbnail
                //	A plot synopsis (called overview in the api)
                //	user rating (called vote_average in the api)
                //	release date

            }
            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                movieAdapter.clear();
                for (Movie movie : movies) {
                    Log.d(LOG_TAG, movie.toString());
                    movieAdapter.add(movie);

                }

            }

        }
    }

    public boolean isOnline() {
        if (getActivity() != null) {
            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
            }else{
                return false;
            }
        }

}
