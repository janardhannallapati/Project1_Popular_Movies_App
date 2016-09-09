package android.inoss.com.popularmoviesapp.adapters;

import android.app.Activity;
import android.inoss.com.popularmoviesapp.R;
import android.inoss.com.popularmoviesapp.beans.Movie;
import android.inoss.com.popularmoviesapp.constants.Constants;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by janardhan on 04-Sep-16.
 */
public class MovieAdapter extends ArrayAdapter<Movie>{
    public static final String LOG_TAG=MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie  movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);

        }

        ImageView posterImageView = (ImageView) convertView.findViewById(R.id.movie_poster_image);


        Picasso.with(getContext()).load(Constants.MOVIE_IMAGE_BASE_URL+Constants.MOVIE_IMAGE_LIST_PAGE_SIZE+movie.getPosterPath()).into(posterImageView);

        return convertView;
    }
}
