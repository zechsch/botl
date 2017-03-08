package com.teamnumberseven.botl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ThreadViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_view);

        // get the intent that started this activity
        Intent intent = getIntent();
        String thread_id = intent.getStringExtra("thread_id");
        final TextView textView = (TextView) findViewById(R.id.textView);
        //textView.setText(thread_id);

        /*
        // create JSON to send to the get_thread route
        JSONObject thread_request = new JSONObject();
        try {
            thread_request.put("post_id", Integer.parseInt(thread_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // print resulting JSON
        textView.setText(thread_request.toString());
        */

        // get thread from API
        final String URL = "http://bttl.herokuapp.com/api/get_thread";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("post_id", thread_id);

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            textView.setText(response.toString());
                        }
                        catch (JSONException e) {
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
}
