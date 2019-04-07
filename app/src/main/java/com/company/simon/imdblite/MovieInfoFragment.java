package com.company.simon.imdblite;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import androidx.fragment.app.DialogFragment;


/*
* this class is the DialogFragment that show the additional information
* of the movie it receives
* */
public class MovieInfoFragment extends DialogFragment {

    private Activity activity;
    private Movie movie;
    private ImageView imgFragmentMoviePoster;
    private TextView txtFragmentMovieName;
    private TextView txtFragmentRating;
    private TextView txtFragmentReleaseYear;
    private TextView txtFragmentGenres;


    /*
    * this onCreateView will initialize all the view's
    * variables and set their content.
    * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_info, container, false);
        imgFragmentMoviePoster = view.findViewById(R.id.imgFragmentMoviePoster);
        txtFragmentMovieName = view.findViewById(R.id.txtFragmentMovieName);
        txtFragmentRating = view.findViewById(R.id.txtFragmentRating);
        txtFragmentReleaseYear = view.findViewById(R.id.txtFragmentReleaseYear);
        txtFragmentGenres = view.findViewById(R.id.txtFragmentGenres);

        txtFragmentMovieName.setText(movie.getTitle());
        txtFragmentRating.setText(movie.getRating());
        txtFragmentReleaseYear.setText(movie.getReleaseYear());
        txtFragmentGenres.setText(movie.getGenre());
        Picasso.with(activity).load(movie.getImage()).fit().into(imgFragmentMoviePoster);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(800, 1200);
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

}


