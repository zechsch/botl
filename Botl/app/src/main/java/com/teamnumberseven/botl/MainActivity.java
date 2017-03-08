package com.teamnumberseven.botl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SupportMapFragment mapFrag =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    // function to go to the thread associated with what is pressed in the feed
    public void goToThread(View view) {
        Intent intent = new Intent(view.getContext(), ThreadViewActivity.class);
        Button pressed = (Button)view;
        String id = String.valueOf(pressed.getTag());
        intent.putExtra("thread_id", id);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
    }
}
