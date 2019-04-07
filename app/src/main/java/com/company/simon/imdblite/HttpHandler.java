package com.company.simon.imdblite;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/*
* this class handles the https request to movies api to get the JSON
* that has all the movies
* */
public class HttpHandler {


    public HttpHandler() { }

    /*
    * this method handle the call to the api for retrieval of movies
    * and calls convertStreamToString() to read the inputStream
    * and returns the JSONArray
    * */
    public String makeServiceCall(String urlString) {
        String response = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // read the response
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    /*
    * this method uses the inputStream to read the JSONArray and
    * uses the StringBuilder() to create the string that will become the
    * JSONArray.
    * */
    private String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}