package com.example.foodoasis;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.foodoasis.databinding.ActivityMapsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng userLocation = new LatLng(30, -95);
    private Marker userLocationMarker;
    private SupportMapFragment mapFragment;
    private Spinner typeSpinner;
    private Button btnFind;
    ArrayList<String> listType, listName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialization Variables
        initializationVariables();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment.getMapAsync(this);

        // set variables for locations
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                .RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
            Boolean coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
            if ((fineLocationGranted != null && fineLocationGranted) ||
                    (coarseLocationGranted != null && coarseLocationGranted)) {


                getCurrentLocation();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        });

        // Sets events on clicking button
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get selected item on Spinner
                int i = typeSpinner.getSelectedItemPosition();
                Log.d("log","pressed"+ i + typeSpinner.getSelectedItem().toString());

                //Set url
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+
                        "location=" + userLocation.latitude + "," + userLocation.longitude + //location with latitude and longitude
                        "&radius=10000" + // radius
                        "&type=" + listType.get(i).toString() + // search type
                        "&key=" + getResources().getString(R.string.google_map_key); // google Api key

                Log.d("Url",url);

                new PlaceTask().execute(url);
            }
        });
    }

    private void initializationVariables() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // initilization
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        btnFind = (Button) findViewById(R.id.btnFind);

        // type place list
        listType = new ArrayList<>();
        listType.add("grocery_or_supermarket");

        // Display place list
        listName = new ArrayList<>();
        listName.add("Grocery Store");

        setAdapter(typeSpinner,listName);

    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        // get current location
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                null).addOnSuccessListener(this, location -> {
            if (location != null) {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d("MapsActivity", "User location set to " + location.getLatitude() + ", " + location.getLongitude());
                onLocationChanged();
            } else {
                Log.d("MapsActivity", "Current location is null");
            }
        });
    }

    private void onLocationChanged() {
        // Add a marker in user's current location (or default location) and move the camera
        userLocationMarker.setPosition(userLocation);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        Log.d("MapsActivity", "Marker moved to " + userLocation.latitude + ", " + userLocation.longitude);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            locationPermissionRequest.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
        }
        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // set value of makrer of map
        userLocationMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));


        Log.d("MapsActivity", "Marker placed at " + userLocation.latitude + ", " + userLocation.longitude);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();

            }
        }
    }

    // for execute url
    public class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Log.e("data in PlaceTask: ",data);
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //executing parser task: to store data in hashmap format
            new ParserTask().execute(s);
        }
    }

    public String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.connect();
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
//            Log.e("line in buffer read: ",line.toString());
        }

        String data = stringBuilder.toString();
        bufferedReader.close();

        return data;
    }

    // For parsing fetched data from googel api
    public class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            PlaceResult nearLocatedPlacesFromGoogleMap = new PlaceResult();
            List<HashMap<String, String>> mapList = null;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                mapList = nearLocatedPlacesFromGoogleMap.parseResult(jsonObject);
//                Log.e("mapList of json parser:", mapList.toString());
//                Log.e("json object: ", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            mMap.clear();

            //after getting nearer stores show them on google map
            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap<String, String> hashMap = hashMaps.get(i);

                //get details for the location
                String name = hashMap.get("name");
                String icon = hashMap.get("icon");
                String place_id = hashMap.get("place_id");
                double lat = Double.parseDouble(hashMap.get("lat"));
                double lng = Double.parseDouble(hashMap.get("lng"));

                LatLng latLng = new LatLng(lat, lng);  // merged latitude and longitude
                MarkerOptions mMarker = new MarkerOptions(); // new marker
                mMarker.position(latLng); // set marker
                mMarker.title(name);
                mMap.addMarker(mMarker);


            }

        }
    }

    // Set adapter for spinner and bind items with it
    private void setAdapter(Spinner spinner, ArrayList<String> listType) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MapsActivity.this, R.layout.layout_spinner, R.id.txt_view, listType) {


            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view; // gives better look to spinner

                tv.setTextColor(Color.WHITE);

                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(arrayAdapter);
    }

}