package com.teamnumberseven.botl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

public class ThreadViewActivity extends AppCompatActivity {

    String thread_id = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_view);

        // get the intent that started this activity
        Intent intent = getIntent();
        thread_id = intent.getStringExtra("thread_id");
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

        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

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
                            JSONArray posts = response.getJSONArray("thread");
                            ArrayList<String> post_list = new ArrayList<String>();
                            //String thread_posts = new String();
                            for(int i = 1; i < posts.length(); i++) {
                                JSONObject post_obj = posts.getJSONObject(i);
                                //thread_posts += post_obj.getString("message") + '\n';
                                post_list.add(post_obj.getString("message"));
                            }
                            //textView.setText(thread_posts);
                            textView.setText(posts.getJSONObject(0).getString("message"));
                            ArrayAdapter adapter = new ArrayAdapter<String>(ThreadViewActivity.this, android.R.layout.simple_list_item_1, post_list);
                            ListView listView = (ListView) findViewById(R.id.listViewThread);
                            listView.setAdapter(adapter);



                            /*VolleyLog.v("Response:%n %s", response.toString(4));
                            textView.setText(response.toString());

                            for(int i = 0; i < 5; i++) {
                                TextView textView = new TextView(ThreadViewActivity.this);
                                textView.setText("LINE");
                                linearLayout.addView(textView);

                            }*/
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

    public void goToThreadReply(View view) {
        Intent intent = new Intent(view.getContext(), ThreadReplyActivity.class);
        intent.putExtra("thread_id", thread_id);
        startActivity(intent);
    }
}
