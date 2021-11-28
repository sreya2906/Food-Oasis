package com.example.foodoasis;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.Holder> {
    Context context;
    ArrayList<FavoritesPlaces> list;
    LayoutInflater inflater;
    String location_name,location_website,location_phoneNo;
    public LocationAdapter(Context context, ArrayList<FavoritesPlaces> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        holder.txtPlaceName.setText(list.get(position).getPlaceName().toString().trim());
        holder.txtPlaceWebsite.setText(list.get(position).getWebsite().toString().trim());
        holder.txtPlacePhoneNO.setText(list.get(position).getPhoneNumber().toString().trim());

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

        TextView txtPlaceName, txtPlaceWebsite,txtPlacePhoneNO;

        public Holder(View itemView) {
            super(itemView);

            txtPlaceName = (TextView) itemView.findViewById(R.id.txtPlaceName);
            txtPlaceWebsite = (TextView) itemView.findViewById(R.id.txtPlaceWebsite);
            txtPlacePhoneNO = (TextView) itemView.findViewById(R.id.txtPlacePhoneNO);

        }
    }
}
