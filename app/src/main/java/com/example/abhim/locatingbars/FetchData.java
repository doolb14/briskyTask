package com.example.abhim.locatingbars;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by abhim on 13-10-2017.
 */

class FetchData extends AsyncTask<Object,String,String> {
    private String url;
    private GoogleMap mMap;
    private List<HashMap<String,String>> placeList=new ArrayList<>();

    @Override
    protected String doInBackground(Object... params) {
        String data="";
        url=(String) params[0];
        mMap=(GoogleMap) params[1];

        try {
            data=getQueryResult(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    String getQueryResult(String x) throws IOException {
        URL url;
        HttpURLConnection cUrl=null;
        BufferedReader bReader=null;
        StringBuilder sBuilder=new StringBuilder();
        try{
            url=new URL(x);
            cUrl=(HttpURLConnection)url.openConnection();
            cUrl.connect();
            bReader= new BufferedReader(new InputStreamReader(cUrl.getInputStream()));
            String line;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(bReader!=null)
                bReader.close();
            if(cUrl!=null)
                cUrl.disconnect();
        }

        String result="";
        result=sBuilder.toString();

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(s);
        placeList.clear();
        PlacesList x=new PlacesList();
        placeList=x.getList(s);
        Log.d("SetMarker",Integer.toString(placeList.size()));
        setMarkers(placeList);

    }


    private void setMarkers(List<HashMap<String,String>> nearbyPlaceList){

        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String rating = googlePlace.get("rating");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lng"));

            LatLng latLng = new LatLng( lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : "+ rating);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mMap.addMarker(markerOptions);

        }
    }
}
