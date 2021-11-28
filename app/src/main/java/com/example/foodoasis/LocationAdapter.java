package com.example.foodoasis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.Holder> {
    Context context;
    ArrayList<FavoritesPlaces> list;
    LayoutInflater inflater;
    String location_id;
    TextView txtPlaceNotFound;

    public LocationAdapter(Context context, ArrayList<FavoritesPlaces> list, TextView tv) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        txtPlaceNotFound = tv;
    }


    @Override
    public void onBindViewHolder(Holder holder, @SuppressLint("RecyclerView") final int position) {
        holder.txtPlaceName.setText(list.get(position).getPlaceName().toString().trim());
        holder.txtPlaceWebsite.setText(list.get(position).getWebsite().toString().trim());
        holder.txtPlacePhoneNO.setText(list.get(position).getPhoneNumber().toString().trim());
        holder.iconRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabseAdapter db = new DatabseAdapter(context);
                db.deleteLocation(list.get(position).getLocation_id().toString());
                list.remove(position);
                notifyDataSetChanged();
                if (list.size() == 0) {
                    txtPlaceNotFound.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.location_list_view, parent, false);

        Holder holder = new Holder(view);
        return holder;
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView txtPlaceName, txtPlaceWebsite, txtPlacePhoneNO;
        ImageView iconRemove;

        public Holder(View itemView) {
            super(itemView);
            iconRemove = (ImageView) itemView.findViewById(R.id.iconRemove);
            txtPlaceName = (TextView) itemView.findViewById(R.id.txtPlaceName);
            txtPlaceWebsite = (TextView) itemView.findViewById(R.id.txtPlaceWebsite);
            txtPlacePhoneNO = (TextView) itemView.findViewById(R.id.txtPlacePhoneNO);

        }
    }


}
