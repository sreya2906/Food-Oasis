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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PlacesClient placesClient;
    private String apiKey = BuildConfig.MAPS_API_KEY;
    private ActivityMapsBinding binding;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng userLocation = new LatLng(30, -95);
    private LatLng inputLocation;
    private LatLng selectedMarkerLocation;
    private SupportMapFragment mapFragment;
    private AutocompleteSupportFragment locationEntry;
    private Button nearCurrentButton, nearInputButton;


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

        // Place autocomplete field filters
        locationEntry.setCountries("US");

        // Place autocomplete return data
        locationEntry.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Event on place selection
        locationEntry.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Log.i("FoodOasis", "An error occurred: " + status);
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                locationEntry.setText(place.getName());
                inputLocation = place.getLatLng();
                onLocationChanged(inputLocation);
                nearInputButton.setEnabled(true);
                Log.i("FoodOasis", "Selected location: " + place.getName() + ", " + place.getId());
            }
        });

        // Sets events on clicking button
        nearCurrentButton.setOnClickListener(view -> {

            //Set url
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+
                    "location=" + userLocation.latitude + "," + userLocation.longitude + //location with latitude and longitude
                    "&keyword=grocery_store" +
                    "&maxprice=3" + //exclude expensive results
                    "&radius=10000" + // radius
                    "&key=" + getResources().getString(R.string.google_map_key); // google Api key

            Log.d("Url",url);

            new PlaceTask().execute(url);
        });

        nearInputButton.setOnClickListener(view -> {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+
                    "location=" + inputLocation.latitude + "," + inputLocation.longitude + //location with latitude and longitude
                    "&keyword=grocery_store" +
                    "&maxprice=3" + //exclude expensive results
                    "&radius=10000" + // radius
                    "&key=" + getResources().getString(R.string.google_map_key); // google Api key

            Log.d("Url", url);

            new PlaceTask().execute(url);
        });
    }

    private void initializationVariables() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // initialization
        Places.initialize(getApplicationContext(), apiKey);
        placesClient = Places.createClient(this);
        locationEntry = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.locationEntry);
        locationEntry.setHint("Search near...");
        nearCurrentButton = findViewById(R.id.nearCurrentButton);
        nearInputButton = findViewById(R.id.nearInputButton);
        nearInputButton.setEnabled(false);
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        // get current location
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                null).addOnSuccessListener(this, location -> {
            if (location != null) {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d("MapsActivity", "User location set to " + location.getLatitude() + ", " + location.getLongitude());
                onLocationChanged(userLocation);
            } else {
                Log.d("MapsActivity", "Current location is null");
            }
        });
    }

    private void onLocationChanged(LatLng newLocation) {
        // Add a marker in user's current location (or default location) and move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 13));
        Log.d("MapsActivity", "Marker moved to " + newLocation.latitude + ", " + newLocation.longitude);
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

        // set value of marker of map
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));

        // Marker click listener to enable favorite location button
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                selectedMarkerLocation = marker.getPosition();
                Log.d("MapsActivity", "Selected marker at " + selectedMarkerLocation.latitude + ", " + selectedMarkerLocation.longitude);
                return false;
            }
        });
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
}