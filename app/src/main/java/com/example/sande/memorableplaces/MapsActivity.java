package com.example.sande.memorableplaces;

import android.Manifest;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    String move;
    Intent intent;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intent = getIntent();

        move = intent.getStringExtra("move");

        if(move.equals("1")) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    LatLng currPos = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currPos));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currPos, 9.0f));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if(move == "1") {
            if (Build.VERSION.SDK_INT < 23) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, locationListener);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, locationListener);
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(move.equals("0")) {
            Log.i("ayy", intent.getStringExtra("latlng"));
            String[] latlong = intent.getStringExtra("latlng").split(",");

            double latitude = Double.parseDouble(latlong[0].substring(10, latlong[0].length()));
            double longitude = Double.parseDouble(latlong[1].substring(0, latlong[1].length() - 1));
            String title = intent.getStringExtra("address");

            LatLng currPos = new LatLng(latitude, longitude);

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currPos).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currPos, 9.0f));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                    mMap.addMarker(new MarkerOptions().position(latLng).title(listAddress.get(0).getAddressLine(0)));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                    MainActivity.placesList.add(listAddress.get(0).getAddressLine(0));
                    MainActivity.latLngList.add(latLng);


                    startActivity(intent);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
