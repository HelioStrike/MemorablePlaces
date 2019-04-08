package com.example.sande.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView places;
    static ArrayList<String> placesList = new ArrayList<String>();;
    static ArrayList<LatLng> latLngList = new ArrayList<LatLng>();;

    public void toMaps(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("move", "1");

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.sande.memorableplaces", Context.MODE_PRIVATE);

        if(placesList.size() == 0) {
            try {
                placesList = (ArrayList) ObjectSerializer.deserialize(sharedPreferences.getString("placesList", ObjectSerializer.serialize(new ArrayList<String>())));
                latLngList = (ArrayList) ObjectSerializer.deserialize(sharedPreferences.getString("latLngList", ObjectSerializer.serialize(new ArrayList<String>())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                sharedPreferences.edit().putString("placesList", ObjectSerializer.serialize(placesList)).apply();
                sharedPreferences.edit().putString("latLngList", ObjectSerializer.serialize(latLngList)).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        places = (ListView) findViewById(R.id.places);

        ArrayAdapter listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, placesList);
        places.setAdapter(listAdapter);

        places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("move", "0");
                intent.putExtra("address", placesList.get(position));
                intent.putExtra("latlng", latLngList.get(position).toString());
                startActivity(intent);
            }
        });
    }
}
