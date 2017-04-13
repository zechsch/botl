package com.teamnumberseven.botl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    public static final String MyPREFERENCES = "UserInfo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", defaultValue);
        longitude = intent.getDoubleExtra("longitude", defaultValue);
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

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.enter_login_from_main, R.animator.exit_login_from_main);
    }
}
