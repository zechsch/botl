package com.teamnumberseven.botl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Stephen Kline 3/9/17
 */

public class NewPost extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.teamnumberseven.MESSAGE";
    private double defaultValue = 1;
    private double latitude;
    private double longitude;
    private GestureDetectorCompat mDetector;
    public static final String MyPREFERENCES = "UserInfo";
    public static final String lat = "latKey";
    public static final String lon = "lonKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", defaultValue);
        longitude = intent.getDoubleExtra("longitude", defaultValue);
        mDetector = new GestureDetectorCompat(this, new NewPost.MyGestureListener());
    }

    public String getUserID()
    {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString("idKey", null);
        if (restoredText != null){
            Log.d("FXN", "USER ID: "+restoredText);
            return restoredText;
        }
        Log.d("FXN", "Return NULL String as User ID");
        return null;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.animator.enter_login_from_main, R.animator.exit_login_from_main);
    }

    public void postMessage(View v) {

        final String URL = "http://bttl.herokuapp.com/api/new_post";
        String userID = getUserID();
        RequestQueue queue = Volley.newRequestQueue(this);
        // Post params to be sent to the server
        EditText editText = (EditText) findViewById(R.id.editText);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("latitude", Double.toString(latitude));
        params.put("longitude", Double.toString(longitude));
        params.put("message", editText.getText().toString());
        params.put("user_id", userID);

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Log.d("RESP:" , response.toString(4));

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

        //Log.d("HTTP URL:", req);
        // add the request object to the queue to be executed
        queue.add(req);
        queue.start();

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(lon, Double.toString(longitude));
        editor.putString(lat, Double.toString(latitude));
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.enter_login_from_main, R.animator.exit_login_from_main);
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
            Intent intent = new Intent(NewPost.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.enter_login_from_main, R.animator.exit_login_from_main);
        }
        public void onSwipeLeft() {
        }
        public void onSwipeTop() {
        }
        public void onSwipeBottom() {
        }
    }
}
