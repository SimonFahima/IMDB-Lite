package com.company.simon.imdblite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;


/*
* this activity is in charge of the QR barcode scanner to add a movie to the list
* after reading a barcode
* */
public class QRscanningActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private TextView txtInfo;
    private TextView txtProcess;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Movies movies;
    private Movie movieObject;
    private AppDataBase dataBase;


    /*
    * this onCreate initializes the view's id's and gets the database
    * to be able to add the movie if scanned the right QR barcode
    * and start the scanning process
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanning);

        surfaceView = findViewById(R.id.surfaceView);
        txtInfo = findViewById(R.id.txtInfo);
        txtProcess = findViewById(R.id.txtProcess);
        movies = Movies.getMovies();
        EventsTracker.movieInfoExists = false;

        dataBase = Room
                .databaseBuilder(QRscanningActivity.this, AppDataBase.class, "database")
                .fallbackToDestructiveMigration()
                .build();

        scanBarcode();
    }


    /*
    * this method requests the permission form the user to use the camera
    * to be able to scan.
    * If denied it will send the user back to MovieListActivity and show
    * a message about how to allow camera access.
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Variables.REQUEST_CAMERA_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(this, "Go to settings and give permission for camera.", Toast.LENGTH_SHORT).show();
                    goBackToMovieListActivity();
                }
            break;
        }
    }


    /*
    * this method starts the scanning for QR barcode and handles/processes the information
    * when it detects a QR barcode.
    * It also stops the camera when it detected a QR barcode for processing
    * */
    public void scanBarcode(){
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QRscanningActivity.this,
                            new String[]{Manifest.permission.CAMERA}, Variables.REQUEST_CAMERA_PERMISSION);
                    return;
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if(qrCodes.size() != 0){
                    txtInfo.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            txtInfo.setText("QR barcode found!");
                            txtProcess.setText("Please wait...");

                            try {
                                JSONObject movie = new JSONObject(qrCodes.valueAt(0).displayValue);
                                String title = movie.getString("title");
                                String image = movie.getString("image");
                                String rating = movie.getString("rating");
                                String releaseYear = movie.getString("releaseYear");
                                String genre = movie.getString("genre");
                                movieObject = new Movie(title, image, rating, releaseYear, genre);
                                Log.d(Variables.ADMIN, movieObject.toString());

                                EventsTracker.receivedMovieInfo = true;
                                EventsTracker.waitingForThread = true;
                                insertQRMovieToDatabase();
                                while (EventsTracker.waitingForThread){
                                    while (EventsTracker.waitingForThread){
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                cameraSource.stop();
                            } catch (final JSONException e) {
                                e.getStackTrace();
                            }
                            txtProcess.setText("Qr code received.");
                        }

                    });
                }else {
                    Log.d(Variables.ADMIN, "could not find info on barcode");
                    EventsTracker.receivedMovieInfo  = false;
                }
            }
        });
    }


    /*
    * this method sends the user back to MovieListActivity
    * */
    public void goBackToMovieListActivity(){
        Intent intent = new Intent(this, MovieListActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    * if the right information was processed in the QR barcode
    * this method wil insert it into the database
    * and update the EventsTracker
    * */
    public void insertQRMovieToDatabase(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MovieDao movieDao = dataBase.movieDao();
                if(!movieDao.checkIfMovieExists(movieObject.getTitle())){
                    movieDao.insertMovie(movieObject);
                    EventsTracker.movieInfoExists = true;
                    EventsTracker.addedMovieInDatabase = true;
                }else {
                    EventsTracker.movieInfoExists = true;
                }
                dataBase.close();
                EventsTracker.moviesInDatabase = true;
                EventsTracker.waitingForThread = false;
            }

        }).start();
    }


    /*
    * this method is mainly here for the use of the Snackbar message if the
    * movie already exists it will also try to continue scanning if info is no good
    * else it will send you back to MovieListActivity to view your newly added movie!
    * */
    public void checkIfMovieInfoIsGood(View view) {
        if(EventsTracker.receivedMovieInfo && EventsTracker.movieInfoExists && EventsTracker.addedMovieInDatabase){
            Toast.makeText(this, "Movie added!", Toast.LENGTH_SHORT).show();
            goBackToMovieListActivity();
        }else if(EventsTracker.movieInfoExists){
            Snackbar.make(view, "Movie already exists.", Snackbar.LENGTH_SHORT)
                    .setAction("action", null).show();
            scanBarcode();
        }else {
            scanBarcode();
        }
    }


}

