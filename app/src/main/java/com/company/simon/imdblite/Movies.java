package com.company.simon.imdblite;

import java.util.ArrayList;


/*
* this class is the class that holds the movie list by the client
* */
public class Movies {

    private static Movies movies;
    ArrayList<Movie> movieArrayList;


    /*
    * constructor
    * */
    Movies(){
        movieArrayList = new ArrayList<>();
    }

    /*
    * this method is static so there is no more than one instance
    * created of the list and checks with the EventsTracker to see if the movies
    * are here already and if not it will create an empty list ready to be
    * loaded with movies. It returns either an empty list or the full movie list depends
    * on what it has and updates EventsTracker to let it know what happened.
    * */
    public static Movies getMovies(){
        if(movies == null){
            movies = new Movies();
            EventsTracker.moviesInClient = false;
        }else {
            EventsTracker.moviesInClient = true;
        }
        return movies;
    }

    /*
    * this method add a movie into the list
    * */
    public boolean addMovie(Movie movie){
        if(!movieArrayList.contains(movie.getTitle())){
            movieArrayList.add(movie);
            return true;
        }else {
            return false;
        }
    }

}
