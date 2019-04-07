package com.company.simon.imdblite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.util.List;
import androidx.fragment.app.FragmentActivity;


/*
* this class is the adapter that loads movies to the recyclerView
* for client viewing
* */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private Activity activity;
    private List<Movie> movies;
    private FragmentActivity fragmentActivity;

    /*
    * constructor
    * */
    public MovieAdapter(Activity activity, List<Movie> movies, FragmentActivity fragmentActivity) {
        super(activity, R.layout.movie_card, movies);
        this.activity = activity;
        this.movies = movies;
        this.fragmentActivity = fragmentActivity;
    }

    /*
    * static nested class ViewContainer that holds the variables
    * for the view's id's
    * */
    static class ViewContainer{
        ImageView imgMoviePoster;
        TextView txtMovieName;
        LinearLayout linearLayoutMovieCard;
        ImageView imgFragmentMoviePoster;
    }


    /*
    * this method is in charge of loading the movies to the listView.
    * it initializes the view variables in static nested class ViewContainer,
    * it handles the onClick for each movie and on click will bring up a DialogFragment from  class
    * MovieInfoFragment and display more information,
    * it sets all the information in the view from the movies it receives.
    * */
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        final ViewContainer viewContainer;
        View view = convertView;
        if(view == null){
            viewContainer = new ViewContainer();
            view = activity.getLayoutInflater().inflate(R.layout.movie_card,parent,false);
            viewContainer.imgMoviePoster = view.findViewById(R.id.imgMoviePoster);
            viewContainer.txtMovieName = view.findViewById(R.id.txtMovieName);
            viewContainer.linearLayoutMovieCard = view.findViewById(R.id.linearLayoutMovieCard);

            viewContainer.imgFragmentMoviePoster = view.findViewById(R.id.imgFragmentMoviePoster);
            view.setTag(viewContainer);
            viewContainer.linearLayoutMovieCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();
                    Movie movie = movies.get(position);
                    Log.d(Variables.ADMIN, "clicked on " + movie.getTitle());
                    Toast.makeText(activity, "" + movie.getTitle(), Toast.LENGTH_SHORT).show();
                    MovieInfoFragment movieInfoFragment = new MovieInfoFragment();
                    movieInfoFragment.setActivity(activity);
                    movieInfoFragment.setMovie(movie);
                    movieInfoFragment.show(fragmentActivity.getSupportFragmentManager(), "");
                }
            });
        }else {
            viewContainer = (ViewContainer) view.getTag();
        }
        Movie movie = movies.get(position);

        viewContainer.txtMovieName.setText(movie.getTitle());
        Picasso.with(activity).load(movie.getImage()).fit().into(viewContainer.imgMoviePoster);
        viewContainer.linearLayoutMovieCard.setTag(position);
        return view;
    }
}
