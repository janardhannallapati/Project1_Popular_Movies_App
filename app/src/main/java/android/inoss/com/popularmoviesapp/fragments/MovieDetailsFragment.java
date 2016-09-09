package android.inoss.com.popularmoviesapp.fragments;

import android.content.Intent;
import android.inoss.com.popularmoviesapp.R;
import android.inoss.com.popularmoviesapp.beans.Movie;
import android.inoss.com.popularmoviesapp.constants.Constants;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailsFragment extends Fragment {

    public static final String LOG_TAG=MovieDetailsFragment.class.getSimpleName();

    public MovieDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_movie_details, container, false);
        TextView overviewView= (TextView) rootView.findViewById(R.id.movie_overview);
        TextView releaseDateView= (TextView) rootView.findViewById(R.id.movie_release_date);
        TextView titleView= (TextView) rootView.findViewById(R.id.movie_title);
        TextView ratingView= (TextView) rootView.findViewById(R.id.movie_user_rating);
        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.movie_poster_image);
        Intent intent=getActivity().getIntent();
        if(intent!=null&&intent.hasExtra(Intent.EXTRA_TEXT)){
            Movie movie=intent.getParcelableExtra(Intent.EXTRA_TEXT);
            Log.d(LOG_TAG,"movie obj received:"+movie.toString());
            titleView.setText(movie.getOriginalTitle());
            releaseDateView.setText(movie.getReleaseDate());
            overviewView.setText(movie.getOverview());
            Double voteAverage=movie.getVoteAverage();
            if(voteAverage!=null) {
                ratingView.setText(voteAverage.doubleValue()+"");
            }else{
                voteAverage=0.0d;
            }
            Log.d(LOG_TAG,"title:"+movie.getOriginalTitle()+" releaseDate:"+movie.getReleaseDate()+" rating:"+voteAverage+" overview:"+movie.getOverview());
            Log.d(LOG_TAG, " URL:"+Constants.MOVIE_IMAGE_BASE_URL + Constants.MOVIE_IMAGE_LIST_PAGE_SIZE + movie.getPosterPath());
            if(movie.getPosterPath()!=null){
                Picasso.with(getContext()).load(Constants.MOVIE_IMAGE_BASE_URL+Constants.MOVIE_IMAGE_LIST_PAGE_SIZE+movie.getPosterPath()).into(posterImageView);
            }
        }

        return rootView;
    }


}
