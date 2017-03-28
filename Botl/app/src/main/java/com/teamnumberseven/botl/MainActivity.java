package com.teamnumberseven.botl;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.*;

import static com.teamnumberseven.botl.R.id.omega;


public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {
    private LocationManager locationManager;
    private LocationListener locationListener;
    public Location current_location = new Location("");
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    ArrayList<Marker> marker_list;
    ArrayList<GoogleMap.InfoWindowAdapter> info_window_list;
    HashMap<Marker, String> markerMap = new HashMap<Marker, String>();
    double currentLatitude = 0;
    double currentLongitude = 0;

    int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnected(Bundle b) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //code
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //code
    }

    @Override
    public void onLocationChanged(Location location) {
        /*double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        current_location.setLatitude(latitude);
        current_location.setLongitude(longitude);
        currentLatitude = latitude;
        currentLongitude = longitude;
        mMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f,0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
        //m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.botl_map_marker3));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));*/

        mLastLocation = location;
        if(mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);*/

        //Move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 1, null);

        //Stop location updates
        if(mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        mMap.clear();
        getNearbyPosts();
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
        Log.d("PRESSED PLUS BUTTON", "go to new post");
        Intent intent = new Intent(v.getContext(), NewPost.class);
        intent.putExtra("longitude", mLastLocation.getLongitude());
        intent.putExtra("latitude", mLastLocation.getLatitude());
        startActivity(intent);
    }

    public void getNewPosts(View v) {
        mMap.clear();
        double latitude = currentLatitude;
        double longitude = currentLongitude;
        LatLng latLng = new LatLng(latitude, longitude);
        current_location.setLatitude(latitude);
        current_location.setLongitude(longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f,0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom));
        getNearbyPosts();
    }


    public class Coords{
        double longitude;
        double latitude;

        Coords(double x, double y){
            longitude = x;
            latitude = y;
        }
    }

    public void getNearbyPosts() {
        final ListView listView = (ListView)findViewById(R.id.listView);
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // get thread from API
        final String URL = "http://bttl.herokuapp.com/api/get_posts";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("latitude", Double.toString(mLastLocation.getLatitude()));
        params.put("longitude", Double.toString(mLastLocation.getLongitude()));
        params.put("distance", "1000");
        params.put("num_posts", "50");

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray posts = response.getJSONArray("posts");
                            List<Map<String, String>> post_list = new ArrayList<Map<String, String>>();
                            final String[] post_titles = new String[posts.length()];
                            final String[] post_ids = new String[posts.length()];
                            final String[] post_ratings = new String[posts.length()];
                            ArrayList<Coords> coords_list = new ArrayList<Coords>();
                            for (int i = 0; i < posts.length(); i++) {
                                JSONObject post_obj = posts.getJSONObject(i);
                                //thread_posts += post_obj.getString("message") + '\n';
                                Map<String, String> post = new HashMap<String, String>(2);
                                post.put("title", post_obj.getString("message"));
                                post.put("rating", "Rating: " + post_obj.getString("rating"));
                                post_list.add(post);
                                post_titles[i] = post_obj.getString("message");
                                post_ids[i] = post_obj.getString("post_id");
                                post_ratings[i] = post_obj.getString("rating");
                                coords_list.add(i, new Coords(post_obj.getDouble("longitude"), post_obj.getDouble("latitude")));
                            }
                            //ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, post_list);
                            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, post_list, android.R.layout.simple_list_item_2, new String[] {"title", "rating"}, new int[] {android.R.id.text1, android.R.id.text2});
                            listView.setAdapter(adapter);
                            //listView.setAdapter(adapter2);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(view.getContext(), ThreadViewActivity.class);
                                    String post_id = String.valueOf(post_ids[position]);
                                    Log.d("SENDING", "ThreadViewActivity this post_id: " + post_id);
                                    intent.putExtra("thread_id", post_id);
                                    startActivity(intent);
                                }
                            });


                            //Populate map with markers and their messages
                            Log.d("CORDS LIST SIZE", ""+coords_list.size());
                            for (int i = 0; i < coords_list.size(); i++)
                            {
                                Log.d("NEW PT", "X: " + coords_list.get(i).longitude + " Y " + coords_list.get(i).latitude + " MESSAGE: " + post_list.get(i));
                                //googleMap.addMarker(new MarkerOptions().position(new LatLng(current_location.getLatitude(), current_location.getLongitude())).title("Marker"));
                                Marker m = mMap.addMarker(new MarkerOptions()
                                               .position(new LatLng(coords_list.get(i).latitude, coords_list.get(i).longitude))
                                               .snippet("Post ID: " + post_ids[i] + " Rating: " + post_ratings[i])
                                               .title(post_titles[i]));
                                m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.botl_map_marker3));
                                markerMap.put(m, post_ids[i]);
                                //marker_list.add(i,m);
                                //mMap.setOnMarkerClickListener();

                            }

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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        //getNearbyPosts();

        /*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                current_location = location;
            }
        };
        mMap = googleMap;

        int MY_PERMISSIONS_REQUEST_LOCATION = 99;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
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
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(current_location.getLatitude(), current_location.getLongitude())).title("Marker"))
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        getNearbyPosts();*/
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Toast.makeText(this, "Info window clicked",
        //        Toast.LENGTH_SHORT).show();
        /*String s = marker.getTitle();
        s = s.substring(9);*/
        String s = markerMap.get(marker);
        //Log.d("SUBSTRING: ", s);
        Intent intent = new Intent(getApplicationContext(), ThreadViewActivity.class);

        //s = "1";
        intent.putExtra("thread_id", s);
        startActivity(intent);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Log.d("MARKER CLICKED", "Marco");
        marker.showInfoWindow();
        return true;
    }
}
