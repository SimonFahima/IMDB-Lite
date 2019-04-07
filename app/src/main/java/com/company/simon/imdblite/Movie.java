package com.company.simon.imdblite;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
* this class is the object creator of Movie
* in addition to that it also determines what the names of the database columns
* will be
* */

@Entity
public class Movie implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "Image")
    private String image;

    @ColumnInfo(name = "Rating")
    private String rating;

    @ColumnInfo(name = "ReleaseYear")
    private String releaseYear;

    @ColumnInfo(name = "Genre")
    private String genre;


    /*
    * constructor
    * */
    public Movie(String title, String image, String rating, String releaseYear, String genre) {
        this.title = title;
        this.image = image;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.genre = genre;
    }


    /*
    * getters and setters
    * */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    /*
     *toString() to read Movie object
     */
    @Override
    public String toString() {
        return title + ", " + image + ", " + rating + ", " + releaseYear + ", " + genre;
    }
}
