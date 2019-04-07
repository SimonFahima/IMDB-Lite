package com.company.simon.imdblite;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {



    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override

    /*
    * this onCreate delays start of movie list to check
    * shared prefs. I put it at 4 second delay for good calm
    * entrance to app
    * */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sharedPreferencesExistingUser()) {
                    EventsTracker.existingUser  = false;
                }
                goToMovieListActivity();
            }
        },4000);
    }



    /*
    * this method right here is for absolute certainty
    * of knowing if this is the users first time
    * visiting the app
    * */
    @SuppressLint("ApplySharedPref")
    public Boolean sharedPreferencesExistingUser() {
        SharedPreferences prefs = getSharedPreferences(Variables.PREFERENCES, MODE_PRIVATE);
        if (!prefs.contains(Variables.EXISTING_USER)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Variables.EXISTING_USER, Variables.TRUE);
            editor.commit();
            return false;
        } else if (prefs.contains(Variables.EXISTING_USER)) {
            String userExists = prefs.getString(Variables.EXISTING_USER, Variables.TRUE);
            EventsTracker.existingUser = Boolean.valueOf(userExists);
        }
        return true;
    }


    /*
    * method to continue to movie list after checking
    * authentication of user
    * */
    public void goToMovieListActivity() {
        Intent intent = new Intent(this, MovieListActivity.class);
        startActivity(intent);
        finish();
    }


}
