package com.company.simon.imdblite;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.getbase.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


public class MovieListActivity extends AppCompatActivity {

    private Movies movies;
    private int dbMovieListSize;
    private ArrayAdapter<Movie> movieArrayAdapter;
    private ListView movieListView;
    private AppDataBase dataBase;
    private String result = "";
    private LoadingMoviesFragment loadingMoviesFragment;

    /*
    * this activity/class is in charge of everything that has to do with
    * movie list retrieval form JSON or database as well as saving list locally
    * and save from client to database after JSON retrieval
    *---------------------------------------------------------------------------
    * */



    /*
    * in this onCreate
    * 1) i create or import movielist from class Movies - its never null and it is static
    * so another instance of it cannot be created
    *
    * 2) i bring the database that was created last time (if first time - database is automatically
    * created)
    *
    * 3) i bring up a dialog fragment that is up and non cancelable until Async task
    * is finished*
    *
    * 4) i set the onClickListener for the add movie floating button which takes you to
    * the QRscanningActivity
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_listview);
        movies = Movies.getMovies();
        movieListView = findViewById(R.id.movieList);

        dataBase = Room
                .databaseBuilder(MovieListActivity.this, AppDataBase.class, "database")
                .fallbackToDestructiveMigration()
                .build();

        updateEventTracker();

        loadingMoviesFragment = new LoadingMoviesFragment();
        loadingMoviesFragment.show(getSupportFragmentManager(), "");
        if(loadingMoviesFragment != null){
            loadingMoviesFragment.setCancelable(false);
        }

        new GetMoviesFromJSONOrRoom().execute();

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateBarcodeScanner();

            }
        });
    }


    /*
    * This method updates the EventTracker class which holds booleans to track if certain
    * things have been done or not... so in this method which is called in onCreate()
    * checks what the status is on the movies and where they are and are'nt then updates
    * EventTracker.
    * */
    private void updateEventTracker(){
        EventsTracker.movieListIsSorted = false;
        if(movies.movieArrayList.size() == 0){
            EventsTracker.moviesInClient = false;
        }else {
            EventsTracker.moviesInClient = true;
        }
        EventsTracker.waitingForThread = true;
        getMovieListSizeFromDatabase();
        while (EventsTracker.waitingForThread){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /*
    * this method retrieves the amount of movies from the database to make sure
    * that the movies are there.
    * */
    public void getMovieListSizeFromDatabase(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MovieDao movieDao = dataBase.movieDao();
                List<Movie> movies = movieDao.getMovies();
                if(movies.size() == 0){
                    EventsTracker.moviesInDatabase = false;
                }else {
                    EventsTracker.moviesInDatabase = true;
                }
                EventsTracker.waitingForThread = false;
                dbMovieListSize = movies.size();
            }
        }).start();

    }


    /*
    * this method is in charge of activating the adapter and calling for a Newest -> Oldest
    * sort.
    * */
    private void loadMoviesToAdapter(){

        movieArrayAdapter = new MovieAdapter(this, checkIfSorted(movies.movieArrayList), this);

        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movie = String.valueOf(movies.movieArrayList.get(position));
                Toast.makeText(MovieListActivity.this, "You clicked on: " + movie, Toast.LENGTH_SHORT).show();
            }
        });
        movieListView.setAdapter(movieArrayAdapter);
    }


    /*
    * this method checks with EventTracker to see if the movies are sorted and
    * if not it will call method -> sortMoviesByReleaseYear() to sort the list
    * */
    public List<Movie> checkIfSorted(List<Movie> movieList){
        List<Movie> sortedMovies;
        if(!EventsTracker.movieListIsSorted){
            sortedMovies = sortMoviesByReleaseYear(movies.movieArrayList);
        }else {
            sortedMovies = movieList;
        }
        EventsTracker.movieListIsSorted = true;
        return sortedMovies;
    }


    /*
    *this method is in charge of sorting the movie list by Newest -> Oldest
    * */
    public List<Movie> sortMoviesByReleaseYear(List<Movie> movieList){
        List<Integer> tempMovieReleaseYearList = new ArrayList<>();
        List<Movie> tempMovieList = new ArrayList<>();
        Movie movie;
        for(int i = 0; i < movieList.size(); i++){
            movie = movieList.get(i);
            int difference = Variables.CURRENT_YEAR - Integer.valueOf(movie.getReleaseYear());
            tempMovieReleaseYearList.add(difference);
        }
        Collections.sort(tempMovieReleaseYearList);
        for (int i = 0; i < tempMovieReleaseYearList.size(); i++) {
            for (int j = 0; j < tempMovieReleaseYearList.size(); j++) {
                movie = movieList.get(j);
                if (tempMovieReleaseYearList.get(i) == Variables.CURRENT_YEAR - (Integer.valueOf(movie.getReleaseYear()))){
                    tempMovieList.add(movie);
                    movieList.remove(movie);
                    break;
                }
            }
        }
        Log.d(Variables.ADMIN, String.valueOf(tempMovieList));
        return tempMovieList;
    }


    /*
    *this method is called by the onClickListener() set on the add movie floating button
    * and sends you to QRscanningActivity
    * */
    public void activateBarcodeScanner() {
        Intent intent = new Intent(this, QRscanningActivity.class);
        startActivity(intent);
    }



    /*
    * this nested class extending AsyncTask<> is activated in onCreate()
    * and is in charge of getting the movies to the client
    * depending on the EventTracker and then calling in onPostExecute() the loadMoviesToAdapter()
    * if its a new user it will call HttpHandler() to handle the call
    * for the JSONArray.
    * else it will activate getMoviesFromDatabase() to get movies from the database
    * */
    @SuppressLint("StaticFieldLeak")
    private class GetMoviesFromJSONOrRoom extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (!EventsTracker.existingUser && !EventsTracker.moviesInClient && !EventsTracker.moviesInDatabase) {
                if(loadingMoviesFragment != null) {
                    publishProgress("New user - getting JSON...");
                }
                HttpHandler httpHandler = new HttpHandler();
                String jsonString = httpHandler.makeServiceCall(Variables.MOVIE_LIST_API);
                if (jsonString != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(jsonString);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject movie = jsonArray.getJSONObject(i);
                            String title = movie.getString("title");
                            String image = movie.getString("image");
                            String rating = movie.getString("rating");
                            String releaseYear = movie.getString("releaseYear");
                            String genre = movie.getString("genre");
                            Movie movieObject = new Movie(title, image, rating, releaseYear, genre);
                            if (!movies.addMovie(movieObject)) {
                                Log.d(Variables.ADMIN, "movie " + movieObject.getTitle() + " already exists");
                            }
                        }

                        EventsTracker.moviesInClient = true;
                        EventsTracker.existingUser = true;
                        EventsTracker.waitingForThread = true;
                        insertMoviesToDatabase();
                        while (EventsTracker.waitingForThread){
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        result = "existing user has movies in client and db";

                    } catch (final JSONException e) {
                        e.getStackTrace();
                        EventsTracker.moviesInClient = false;
                        EventsTracker.moviesInDatabase = false;
                        result = "json parsing error no movies in client or database";
                    }
                }
            }

            if(EventsTracker.moviesInDatabase){
                EventsTracker.waitingForThread = true;
                getMoviesFromDatabase();
                while (EventsTracker.waitingForThread){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                publishProgress("fetching movies from db");
                EventsTracker.moviesInClient = true;
            }
            if (loadingMoviesFragment != null) {
                publishProgress("Movies loaded to client");
            }
            EventsTracker.moviesInDatabase = true;
            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d(Variables.ADMIN, Arrays.toString(values));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(Variables.ADMIN, "result is " + result);
            if(EventsTracker.existingUser && EventsTracker.moviesInClient) {
                loadMoviesToAdapter();
                loadingMoviesFragment.dismiss();
            }else {
                Log.d(Variables.ADMIN, String.valueOf(EventsTracker.existingUser) +
                        String.valueOf(EventsTracker.moviesInClient) +
                        String.valueOf(EventsTracker.moviesInDatabase));
            }
        }
    }


    /*
    * this method is in charge of inserting movies into the database
    * if the EventTracker says there's no movies in database
    * */
    public void insertMoviesToDatabase(){
       new Thread(new Runnable() {
           @Override
           public void run() {
               MovieDao movieDao = dataBase.movieDao();

               List<Movie> moviesList = movies.movieArrayList;

               for (int j = 0; j < moviesList.size(); j++) {
                   movieDao.insertMovie(moviesList.get(j));
               }
               dataBase.close();

               EventsTracker.waitingForThread = false;
           }
       }).start();
    }


    /*
    * this method get all the movies to the client from the database and is
    * called in GetMoviesFromJSONOrRoom nested class if the EventTracker says movies
    * are in databse
    * */
    public void getMoviesFromDatabase(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MovieDao movieDao = dataBase.movieDao();
                List<Movie> moviesFromRoom = movieDao.getMovies();
                for(Movie m : moviesFromRoom){
                    if(movies.addMovie(m)){
                    Log.d(Variables.ADMIN, "movie " + m + " added to client");
                    }else {
                        Log.d(Variables.ADMIN, "movie " + m + " already exists in client");
                    }
                }
                EventsTracker.waitingForThread = false;
            }
        }).start();
    }



}
