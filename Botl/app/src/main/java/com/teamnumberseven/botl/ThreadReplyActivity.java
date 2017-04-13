package com.teamnumberseven.botl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ThreadReplyActivity extends AppCompatActivity {

    String thread_id = new String();
    public static final String MyPREFERENCES = "UserInfo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_reply);

        Intent intent = getIntent();
        thread_id = intent.getStringExtra("thread_id");
        //EditText editText = (EditText) findViewById(R.id.editText);
    }

    public String getUserID()
    {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString("idKey", null);
        if (restoredText != null){
            return restoredText;
        }
        return null;
    }

    public void cancelReply(View view) {
        Intent intent = new Intent(view.getContext(), ThreadViewActivity.class);
        intent.putExtra("thread_id", thread_id);
        startActivity(intent);
    }

    public void sendReply(final View view) {
        EditText editText = (EditText) findViewById(R.id.editText);

        final String URL = "http://bttl.herokuapp.com/api/reply";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("thread", thread_id);
        params.put("message", editText.getText().toString());
        params.put("user_id", getUserID());

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Intent intent = new Intent(view.getContext(), ThreadViewActivity.class);
                            intent.putExtra("thread_id", thread_id);
                            startActivity(intent);
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
