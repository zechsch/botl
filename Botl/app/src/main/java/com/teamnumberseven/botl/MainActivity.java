package com.teamnumberseven.botl;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import com.google.android.gms.maps.model.CameraPosition;
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

import static com.teamnumberseven.botl.R.id.feed;
import static com.teamnumberseven.botl.R.id.textView;


public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    HashMap<Marker, String> markerMap = new HashMap<Marker, String>();
    HashMap<String, Marker> idToMarker = new HashMap<String, Marker>();
    boolean locationSet = false;
    private GestureDetectorCompat mDetector;
    public static final String MyPREFERENCES = "UserInfo";
    public static final String handle = "handleKey";
    public static final String chatSort = "chatsortKey";
    public static final String dist = "distanceKey";
    public static final String numPosts = "numPostsKey";
    public static final String lat = "latKey";
    public static final String lon = "lonKey";


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    public String getName() {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(handle, null);
        if (restoredText != null) {
            return restoredText;
        }
        return null;
    }

    public void setLoc()
    {
        Log.d("FXN", "SETTING LOC");
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(lon, Double.toString(mLastLocation.getLongitude()));
        editor.putString(lat, Double.toString(mLastLocation.getLatitude()));
        editor.commit();
    }

    public boolean checkLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        int restoredText = prefs.getInt("loggedInKey", 0);
        if (restoredText != 0) {
            return true;
        }
        return false;
    }

    public Location getLoc() {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Log.d("FXN", "GETTING LOC");
        String restoredLongitude = prefs.getString(lon, "0.00");
        String restoredLatitude = prefs.getString(lat, "0.00");
        Log.d("FXN", "Longitude: " + restoredLongitude+ " Latitude: "+ restoredLatitude );
        Location l = new Location("dummy");
        Log.d("FXN", "Longitude: " + restoredLongitude+ " Latitude: "+ restoredLatitude );
        double x = Double.valueOf(restoredLongitude);
        double y = Double.valueOf(restoredLatitude);
        Log.d("FXN", "Longitude: " + x+ " Latitude: "+ y );
        l.setLongitude(x);
        Log.d("FXN", "Longitude: " + restoredLongitude+ " Latitude: "+ restoredLatitude );
        l.setLatitude(y);
        Log.d("FXN", "Longitude: " + restoredLongitude+ " Latitude: "+ restoredLatitude );
        return l;
    }

    public String getDistance() {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(dist, "5");
        return restoredText;
    }

    public String getNumPosts() {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(numPosts, "25");
        return restoredText;
    }

    public String checkChatSort() {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(chatSort, "distance");
        //Log.d("FXN", "Chat Sort: "+restoredText);
        return restoredText;
    }

    public String getUserID() {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString("idKey", null);
        if (restoredText != null) {
            return restoredText;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("FXN", "ON CREATE MAIN");

        Typeface fontAwesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setTypeface(fontAwesome);
        TextView profile_chevron = (TextView) findViewById(R.id.profileChevron);
        profile_chevron.setTypeface(fontAwesome);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("FXN", "FINISHED MAIN");
    }

    @Override
    public void onConnected(Bundle b) {
        Log.d("FXN", "ON CONNECTED");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        Location location = getLoc();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 1, null);
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
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (!locationSet) {
            //Move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 1, null);
            locationSet = true;
            mMap.clear();
            getNearbyPosts();
        }

    }


    // function to go to the thread associated with what is pressed in the feed
    public void goToThread(View view) {
        Intent intent = new Intent(view.getContext(), ThreadViewActivity.class);
        Button pressed = (Button) view;
        String id = String.valueOf(pressed.getTag());
        intent.putExtra("thread_id", id);
        setLoc();
        startActivity(intent);
    }

    public void goToNewPost(View v) {
        if (mLastLocation != null) {
            Intent intent = new Intent(v.getContext(), NewPost.class);
            intent.putExtra("longitude", mLastLocation.getLongitude());
            intent.putExtra("latitude", mLastLocation.getLatitude());
            setLoc();
            startActivity(intent);
            overridePendingTransition(R.animator.enter_threadview_from_main, R.animator.exit_threadview_from_main);
        }
    }

    public void getNewPosts(View v) {
        if (mLastLocation != null) {
            mMap.clear();
            getNearbyPosts();
        }
    }


    public class Coords {
        double longitude;
        double latitude;

        Coords(double x, double y) {
            longitude = x;
            latitude = y;
        }
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();


        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/

        Log.d("FXN", " RESUME");
        if(mLastLocation != null) {
            Toast.makeText(this, "LAST LOC NOT NULL", Toast.LENGTH_SHORT).show();
            mMap.clear();
            getNearbyPosts();
        }
        else{
            Log.d("FXN", "ELSE");
            Location tmpp = new Location(getLoc());
            //tmpp.setLatitude(0.00);
            //tmpp.setLongitude(0.00);
            Log.d("FXN", "DONE");
            Log.d("FXN", "LAST LONG: " + tmpp.getLongitude() + " LAST LAT: " + tmpp.getLatitude());
            mLastLocation = new Location("");
            mLastLocation.setLongitude(tmpp.getLongitude());
            mLastLocation.setLatitude(tmpp.getLatitude());
            Log.d("FXN", "HOLLA");
            //mMap.clear();
            Log.d("FXN", "WHAT");
            getNearbyPosts();
            Log.d("FXN", "GETTING NEARBY POSTS");
            //Toast.makeText(this, "LOC NULL", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.moveTaskToBack(true);
    }

    public void getNearbyPosts() {
        Log.d("FXN", "GOING IN");
        final ListView listView = (ListView)findViewById(R.id.listView);
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // get thread from API
        final String URL = "http://bttl.herokuapp.com/api/get_posts";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> params = new HashMap<String, String>();
        Log.d("FXN", "Hi1");
        params.put("latitude", Double.toString(mLastLocation.getLatitude()));
        params.put("longitude", Double.toString(mLastLocation.getLongitude()));
        Log.d("FXN", "Hi2");
        params.put("distance", getDistance());
        params.put("num_posts", getNumPosts());

        String sort_posts = checkChatSort();
        params.put("sort", sort_posts);

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
                                if(Integer.parseInt(post_obj.getString("rating")) == 1) {
                                    post.put("rating","user: " + post_obj.getString("user_id") + "\t\t\t\t\t\t\t\t\t" + post_obj.getString("rating") + " point");
                                }
                                else {
                                    post.put("rating", "user: " + post_obj.getString("user_id") + "\t\t\t\t\t\t\t\t\t" + post_obj.getString("rating") + " points");
                                }

                                post_list.add(post);
                                post_titles[i] = post_obj.getString("message");
                                post_ids[i] = post_obj.getString("post_id");
                                post_ratings[i] = post_obj.getString("rating");
                                coords_list.add(i, new Coords(post_obj.getDouble("longitude"), post_obj.getDouble("latitude")));
                            }
                            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, post_list, android.R.layout.simple_list_item_2, new String[] {"title", "rating"}, new int[] {android.R.id.text1, android.R.id.text2});
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(view.getContext(), ThreadViewActivity.class);
                                    String post_id = String.valueOf(post_ids[position]);
                                    intent.putExtra("thread_id", post_id);
                                    setLoc();
                                    startActivity(intent);
                                    overridePendingTransition(R.animator.enter_threadview_from_main, R.animator.exit_threadview_from_main);
                                }
                            });

                            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                                    String post_id = String.valueOf(post_ids[position]);
                                    Marker m = idToMarker.get(post_id);
                                    onMarkerClick(m);
                                    LatLng loc = m.getPosition();
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(loc)
                                            .zoom(mMap.getCameraPosition().zoom)
                                            .build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 400, null);
                                    return true;
                                }
                            });



                            //Populate map with markers and their messages
                            for (int i = 0; i < coords_list.size(); i++)
                            {
                                Marker m = mMap.addMarker(new MarkerOptions()
                                               .position(new LatLng(coords_list.get(i).latitude, coords_list.get(i).longitude))
                                               .snippet("Post ID: " + post_ids[i] + " Rating: " + post_ratings[i])
                                               .title(post_titles[i]));
                                m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.botl_map_marker3));
                                markerMap.put(m, post_ids[i]);
                                idToMarker.put(post_ids[i], m);
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
        Log.d("FXN", "MAP IS READY");
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String s = markerMap.get(marker);
        Intent intent = new Intent(getApplicationContext(), ThreadViewActivity.class);

        //s = "1";
        intent.putExtra("thread_id", s);
        setLoc();
        startActivity(intent);
        overridePendingTransition(R.animator.enter_threadview_from_main, R.animator.exit_threadview_from_main);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //Swipe Gestures
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        private static final String DEBUG_TAG = "FXN Gestures";

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = event2.getY() - event1.getY();
                float diffX = event2.getX() - event1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        public void onSwipeRight() {
            //User is logged in, go to account settings
            if (checkLoggedIn())
            {
                Intent intent = new Intent(MainActivity.this, AccountSettingsActivity.class);
                setLoc();
                startActivity(intent);
                overridePendingTransition(R.animator.enter_login_from_main, R.animator.exit_login_from_main);
            }
            else
            {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                setLoc();
                startActivity(intent);
                overridePendingTransition(R.animator.enter_login_from_main, R.animator.exit_login_from_main);
            }
        }
        public void onSwipeLeft() {
        }
        public void onSwipeTop() {
        }
        public void onSwipeBottom() {
        }
    }
}
