package com.company.simon.imdblite;

import java.util.List;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


/*
* this interface is in charge of handling calls to the database
* to change, insert, delete, etc... info in the database.
* */
@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    List<Movie> getMovies();

    @Query("SELECT * FROM movie WHERE uid = (:uid)")
    Movie getMovieById(int uid);

    @Insert
    void insertMovie(Movie... movies);

    @Query("SELECT * FROM movie WHERE title == (:title)")
    boolean checkIfMovieExists(String title);

    @Delete
    void deleteMovie(Movie movie);
}
