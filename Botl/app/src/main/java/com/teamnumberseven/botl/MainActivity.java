package com.teamnumberseven.botl;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.*;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private LocationManager locationManager;
    private LocationListener locationListener;
    public Location current_location = new Location("");
    GoogleMap mMap;
    int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mMap == null) {
            SupportMapFragment mapFrag =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }

    }

    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        current_location.setLatitude(latitude);
        current_location.setLongitude(longitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    // function to go to the thread associated with what is pressed in the feed
    public void goToThread(View view) {
        Intent intent = new Intent(view.getContext(), ThreadViewActivity.class);
        Button pressed = (Button) view;
        String id = String.valueOf(pressed.getTag());
        intent.putExtra("thread_id", id);
        startActivity(intent);
    }

    public void goToNewPost(View v) {
        Intent intent = new Intent(v.getContext(), NewPost.class);
        intent.putExtra("longitude", current_location.getLongitude());
        intent.putExtra("latitude", current_location.getLatitude());
        startActivity(intent);
    }

    public void getNearbyPosts() {
        final ListView listView = (ListView)findViewById(R.id.listView);
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // get thread from API
        final String URL = "http://bttl.herokuapp.com/api/get_posts";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("latitude", Double.toString(current_location.getLatitude()));
        params.put("longitude", Double.toString(current_location.getLongitude()));
        params.put("distance", "1000");
        params.put("num_posts", "10");

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray posts = response.getJSONArray("posts");
                            ArrayList<String> post_list = new ArrayList<String>();
                            final String[] post_ids = new String[posts.length()];
                            for (int i = 1; i < posts.length(); i++) {
                                JSONObject post_obj = posts.getJSONObject(i);
                                //thread_posts += post_obj.getString("message") + '\n';
                                post_list.add(post_obj.getString("message"));
                                post_ids[i - 1] = post_obj.getString("post_id");
                            }
                            ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, post_list);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(view.getContext(), ThreadViewActivity.class);
                                    String post_id = String.valueOf(post_ids[position]);
                                    intent.putExtra("thread_id", post_id);
                                    startActivity(intent);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        queue.add(req);
        queue.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                current_location = location;
            }
        };
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        //locationManager.requestLocationUpdates(bestProvider, 20000, 0, (android.location.LocationListener) this);
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(current_location.getLatitude(), current_location.getLongitude())).title("Marker"));
        getNearbyPosts();
    }

}
