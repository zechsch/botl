package com.teamnumberseven.botl;

import android.*;
import android.Manifest;
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


public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener{
    public Location current_location = new Location("");
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

    public String getName()
    {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(handle, null);
        if (restoredText != null){
            return restoredText;
        }
        return null;
    }

    public boolean checkLoggedIn()
    {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        int restoredText = prefs.getInt("loggedInKey", 0);
        if (restoredText != 0){
            return true;
        }
        return false;
    }

    public String getDistance()
    {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(dist, "5");
        return restoredText;
    }

    public String getNumPosts()
    {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(numPosts, "25");
        return restoredText;
    }

    public String checkChatSort()
    {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(chatSort, "distance");
        //Log.d("FXN", "Chat Sort: "+restoredText);
        return restoredText;
    }

    public String getUserID()
    {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString("idKey", null);
        if (restoredText != null){
            //Log.d("FXN", "USER ID: "+restoredText);
            return restoredText;
        }
        //Log.d("FXN", "Return NULL String as User ID");
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //list.setAdapter(new Array)

        Typeface fontAwesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        Button refreshButton = (Button)findViewById(R.id.refreshButton);
        refreshButton.setTypeface(fontAwesome);
        TextView profile_chevron = (TextView)findViewById(R.id.profileChevron);
        profile_chevron.setTypeface(fontAwesome);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Log.d("FXN", "LOC WAS DISABLED, NOW ENABLED");
            //while( !checkLocationPermission())
            //{
            //    Log.d("FXN", "CHECKING PERMISSION CURRENTLY FALSE");
            //};
            checkLocationPermission();
        }

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnected(Bundle b) {
        mLocationRequest = new LocationRequest();
        //Log.d("FXN", "CONNECTED");
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

        if(!locationSet) {
            //Move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 1, null);
            locationSet = true;
            mMap.clear();
            getNearbyPosts();
        }


        //Stop location updates
        /*if(mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/


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
        if(mLastLocation != null ) {
            //Log.d("PRESSED PLUS BUTTON", "go to new post");
            Intent intent = new Intent(v.getContext(), NewPost.class);
            intent.putExtra("longitude", mLastLocation.getLongitude());
            intent.putExtra("latitude", mLastLocation.getLatitude());
            startActivity(intent);
            overridePendingTransition(R.animator.enter_threadview_from_main, R.animator.exit_threadview_from_main);
        }
    }

    public void getNewPosts(View v) {
        /*mMap.clear();
        double latitude = currentLatitude;
        double longitude = currentLongitude;
        LatLng latLng = new LatLng(latitude, longitude);
        current_location.setLatitude(latitude);
        current_location.setLongitude(longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f,0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom));
        getNearbyPosts();*/
        if(mLastLocation != null) {
            mMap.clear();
            getNearbyPosts();
        }
    }


    public class Coords{
        double longitude;
        double latitude;

        Coords(double x, double y){
            longitude = x;
            latitude = y;
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Log.d("FXN", " RESUME");
        if(mLastLocation != null) {
            mMap.clear();
            getNearbyPosts();
        }
        //mMap.clear();
        //Refresh your stuff here
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        /*Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);*/
        this.moveTaskToBack(true);
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
        params.put("distance", getDistance());
        params.put("num_posts", getNumPosts());

        String sort_posts = checkChatSort();
        //Log.d("FXN", "SORTING BY: " + sort_posts);
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
                                    post.put("rating", post_obj.getString("rating") + " point");
                                }
                                else {
                                    post.put("rating", post_obj.getString("rating") + " points");
                                }

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
                                    //Log.d("SENDING", "ThreadViewActivity this post_id: " + post_id);
                                    intent.putExtra("thread_id", post_id);
                                    startActivity(intent);
                                    overridePendingTransition(R.animator.enter_threadview_from_main, R.animator.exit_threadview_from_main);
                                }
                            });

                            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                                    String post_id = String.valueOf(post_ids[position]);
                                    //Log.d("FXN", "LONG CLICK ITEM : " + post_id);
                                    Marker m = idToMarker.get(post_id);
                                    onMarkerClick(m);
                                    LatLng loc = m.getPosition();
                                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(loc)
                                            .zoom(mMap.getCameraPosition().zoom)
                                            .build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 400, null);
                                    return true;
                                }
                            });



                            //Populate map with markers and their messages
                            //Log.d("CORDS LIST SIZE", ""+coords_list.size());
                            for (int i = 0; i < coords_list.size(); i++)
                            {
                                //Log.d("NEW PT", "X: " + coords_list.get(i).longitude + " Y " + coords_list.get(i).latitude + " MESSAGE: " + post_list.get(i));
                                //googleMap.addMarker(new MarkerOptions().position(new LatLng(current_location.getLatitude(), current_location.getLongitude())).title("Marker"));
                                Marker m = mMap.addMarker(new MarkerOptions()
                                               .position(new LatLng(coords_list.get(i).latitude, coords_list.get(i).longitude))
                                               .snippet("Post ID: " + post_ids[i] + " Rating: " + post_ratings[i])
                                               .title(post_titles[i]));
                                m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.botl_map_marker3));
                                markerMap.put(m, post_ids[i]);
                                idToMarker.put(post_ids[i], m);

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
        overridePendingTransition(R.animator.enter_threadview_from_main, R.animator.exit_threadview_from_main);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Log.d("MARKER CLICKED", "Marco");
        marker.showInfoWindow();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
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

        //@Override
        //public void onLongPress(MotionEvent event) {
        //    Log.d(DEBUG_TAG, "onLongPress: ");
        //    Toast.makeText(list.getItem())
        //}

        public void onSwipeRight() {
            //Log.d("FXN", "SWIPE RIGHT");
            //User is logged in, go to account settings
            if (checkLoggedIn())
            {
                //Log.d("FXN","Logged In View");
                Intent intent = new Intent(MainActivity.this, AccountSettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.enter_login_from_main, R.animator.exit_login_from_main);
            }
            else
            {
                //Log.d("FXN","Not Logged In View");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.enter_login_from_main, R.animator.exit_login_from_main);
            }
        }
        public void onSwipeLeft() {
            //Log.d("FXN", "SWIPE LEFT");
        }
        public void onSwipeTop() {
            //Log.d("FXN", "SWIPE TOP");
        }
        public void onSwipeBottom() {
            //Log.d("FXN", "SWIPE BOTTOM");
        }
    }
}
