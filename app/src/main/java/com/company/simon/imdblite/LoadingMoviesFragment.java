package com.company.simon.imdblite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.DialogFragment;


/*
* this is the DialogFragment that runs while the application is determining where
* the movies will come from in MovieListActivity's nested class GetMoviesFromJSONOrRoom which
* extends AsyncTask<>. This fragment is dismissed in onPostExecute().
* */
public class LoadingMoviesFragment extends DialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading_movies, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(800, 770);
    }

}
