package com.example.foodoasis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class FavoritesPlacesActivity extends AppCompatActivity {
    RecyclerView list;
    ArrayList<FavoritesPlaces> favoritesPlacesList;
    DatabaseAdapter dbAdapter;
    TextView txtPlaceNotFound;
    LocationAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_places);
        txtPlaceNotFound = findViewById(R.id.txtPlaceNotFound);
        list = (RecyclerView) findViewById(R.id.favoriteLocationList);
        txtPlaceNotFound.setVisibility(View.GONE);

        dbAdapter = new DatabaseAdapter(this);
        favoritesPlacesList = dbAdapter.getDetails();


        if (favoritesPlacesList.size() == 0) {
            txtPlaceNotFound.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);

        } else {
            txtPlaceNotFound.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FavoritesPlacesActivity.this);
            list.setLayoutManager(layoutManager);
            adapter = new LocationAdapter(FavoritesPlacesActivity.this, favoritesPlacesList, txtPlaceNotFound);
            list.setAdapter(adapter);
        }


    }


}