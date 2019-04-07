package com.company.simon.imdblite;


/*
* this class handles the tracking of which events occurred
* and which did'nt by holding static boolean variables
* to keep track.
* */
public class EventsTracker {

    static boolean existingUser;
    static boolean moviesInClient;
    static boolean moviesInDatabase;
    static boolean waitingForThread;
    static boolean movieListIsSorted;
    static boolean receivedMovieInfo;
    static boolean movieInfoExists;
    static boolean addedMovieInDatabase;
}
