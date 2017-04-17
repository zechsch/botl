package com.teamnumberseven.botl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

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
    int rating = 0;
    int user_rating = 0;
    Boolean hasVoted = false;
    Boolean reply_visible = false;
    private GestureDetectorCompat mDetector;
    ArrayList<String> post_list = new ArrayList<String>();
    public static final String MyPREFERENCES = "UserInfo";


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.animator.enter_login_from_main, R.animator.exit_login_from_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_view);

        mDetector = new GestureDetectorCompat(this, new ThreadViewActivity.MyGestureListener());

        // get the intent that started this activity
        Intent intent = getIntent();
        thread_id = intent.getStringExtra("thread_id");
        final TextView textView = (TextView) findViewById(R.id.textView);
        final TextView ratingView = (TextView) findViewById(R.id.ratingView);
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
                            //String thread_posts = new String();
                            for(int i = 1; i < posts.length(); i++) {
                                JSONObject post_obj = posts.getJSONObject(i);
                                //thread_posts += post_obj.getString("message") + '\n';
                                post_list.add(post_obj.getString("message"));
                            }
                            //textView.setText(thread_posts);
                            textView.setText(posts.getJSONObject(0).getString("message"));
                            if(Integer.parseInt(posts.getJSONObject(0).getString("rating")) == 1) {
                                ratingView.setText(posts.getJSONObject(0).getString("user_id") + "\t\t\t\t\t\t\t\t\t\t" + posts.getJSONObject(0).getString("rating") + " point");
                            }
                            else {
                                ratingView.setText(posts.getJSONObject(0).getString("user_id") + "\t\t\t\t\t\t\t\t\t\t" + posts.getJSONObject(0).getString("rating") + " points");
                            }
                            rating = Integer.parseInt(posts.getJSONObject(0).getString("rating"));
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

        // get previous ranking if there is any and modify buttons accordingly
        SharedPreferences user_ratings = getSharedPreferences("user_ratings", Context.MODE_PRIVATE);
        user_rating = user_ratings.getInt(thread_id, 0);
        if(user_rating == 1) {
            ToggleButton upButton = (ToggleButton) findViewById(R.id.upButton);
            //upButton.setBackgroundColor(Color.parseColor("#f4ad42"));
            //upButton.setTextColor(Color.WHITE);
            upButton.setChecked(true);
        }
        else if (user_rating == -1) {
            ToggleButton downButton = (ToggleButton) findViewById(R.id.downButton);
            //downButton.setBackgroundColor(Color.BLUE);
            //downButton.setTextColor(Color.WHITE);
            downButton.setChecked(true);
        }
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

    public void goToThreadReply(View view) {
        /*Intent intent = new Intent(view.getContext(), ThreadReplyActivity.class);
        intent.putExtra("thread_id", thread_id);
        startActivity(intent);*/
        Button reply_button = (Button) findViewById(R.id.replybutton);
        EditText reply_entry = (EditText) findViewById(R.id.reply_entry);
        Button send_button = (Button) findViewById(R.id.sendbutton);
        if(reply_visible == false) {
            reply_visible = true;
            reply_button.setText("Cancel");
            reply_entry.setVisibility(View.VISIBLE);
            send_button.setVisibility(View.VISIBLE);
        }
        else {
            reply_visible = false;
            reply_button.setText("Reply");
            reply_entry.setVisibility(View.GONE);
            send_button.setVisibility(View.GONE);
            reply_entry.setText("");

            // hide keyboard
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void sendReply(View view) {
        // hide keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        // change visibility of buttons and text area
        reply_visible = false;
        Button reply_button = (Button) findViewById(R.id.replybutton);
        EditText reply_entry = (EditText) findViewById(R.id.reply_entry);
        Button send_button = (Button) findViewById(R.id.sendbutton);
        reply_button.setText("Reply");
        reply_entry.setVisibility(View.GONE);
        send_button.setVisibility(View.GONE);

        // add response to current view
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        post_list.add(reply_entry.getText().toString());
        ArrayAdapter adapter = new ArrayAdapter<String>(ThreadViewActivity.this, android.R.layout.simple_list_item_1, post_list);
        ListView listView = (ListView) findViewById(R.id.listViewThread);
        listView.setAdapter(adapter);

        // send response to API
        final String URL = "http://bttl.herokuapp.com/api/reply";
        String userID = getUserID();
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("thread", thread_id);
        params.put("message", reply_entry.getText().toString());
        params.put("user_id", userID);

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
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

        reply_entry.setText("");
    }

    public void upVote(final View view) {
        String rate_string = "up";
        boolean twice = false;

        if(user_rating == 1) {
            user_rating = 0;
            rating--;
            rate_string = "down";
        }
        else if(user_rating == 0) {
            user_rating = 1;
            rating++;
        }
        else if(user_rating == -1) {
            user_rating = 1;
            rating += 2;
            twice = true;
            ToggleButton downButton = (ToggleButton) findViewById(R.id.downButton);
            downButton.setChecked(false);
        }

        TextView ratingView = (TextView) findViewById(R.id.ratingView);
        if(rating == 1) {
            ratingView.setText(rating + " point");
        }
        else {
            ratingView.setText(rating + " points");
        }

        SharedPreferences user_ratings = getSharedPreferences("user_ratings", Context.MODE_PRIVATE);
        // key is post id, 0 means not voted, 1 means upvoted, -1 means downvoted
        SharedPreferences.Editor editor = user_ratings.edit();
        editor.putInt(thread_id, user_rating);
        editor.commit();

        final String URL = "http://bttl.herokuapp.com/api/rate_post";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("post", thread_id);
        params.put("vote", rate_string);
        if(twice) {
            params.put("revote", "true");
        }
        else {
            params.put("revote", "false");
        }

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
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

    public void downVote(final View view) {
        String rate_string = "down";
        boolean twice = false;
        if(user_rating == -1) {
            user_rating = 0;
            rating++;
            rate_string = "up";
        }
        else if(user_rating == 0) {
            user_rating = -1;
            rating--;
        }
        else if(user_rating == 1) {
            user_rating = -1;
            rating -= 2;
            twice = true;
            ToggleButton upButton = (ToggleButton) findViewById(R.id.upButton);
            upButton.setChecked(false);
        }

        TextView ratingView = (TextView) findViewById(R.id.ratingView);
        if(rating == 1) {
            ratingView.setText(rating + " point");
        }
        else {
            ratingView.setText(rating + " points");
        }

        SharedPreferences user_ratings = getSharedPreferences("user_ratings", Context.MODE_PRIVATE);
        // key is post id, 0 means not voted, 1 means upvoted, -1 means downvoted
        SharedPreferences.Editor editor = user_ratings.edit();
        editor.putInt(thread_id, user_rating);
        editor.commit();

        final String URL = "http://bttl.herokuapp.com/api/rate_post";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("post", thread_id);
        params.put("vote", rate_string);
        if(twice) {
            params.put("revote", "true");
        }
        else {
            params.put("revote", "false");
        }

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
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
            Intent intent = new Intent(ThreadViewActivity.this, MainActivity.class);
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
