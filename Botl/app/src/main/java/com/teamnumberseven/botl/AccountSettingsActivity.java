package com.teamnumberseven.botl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Kevin on 4/13/2017.
 */

public class AccountSettingsActivity extends AppCompatActivity{

    public static final String MyPREFERENCES = "UserInfo";
    public static final String Name = "nameKey";
    public static final String UserID = "idKey";
    public static final String handle = "handleKey";
    public static final String LoggedIn = "loggedInKey";
    public static final String chatSort = "chatsortKey";
    public static final String dist = "distanceKey";
    public static final String numPosts = "numPostsKey";
    String selectedSort = null;
    SharedPreferences sharedpreferences;

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

    public String getName()
    {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(handle, null);
        if (restoredText != null){
            return restoredText;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);

        //Intent intent = getIntent();
        //thread_id = intent.getStringExtra("thread_id");
        //EditText editText = (EditText) findViewById(R.id.editText);
        EditText name = (EditText) findViewById(R.id.enter_name);
        EditText distance_from_view = (EditText) findViewById(R.id.distance);
        EditText numposts_from_view = (EditText) findViewById(R.id.num_posts);

        name.setText(getName());
        distance_from_view.setText(getDistance());
        numposts_from_view.setText(getNumPosts());

        Spinner s = (android.widget.Spinner) findViewById(R.id.chatsort);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSort = parent.getSelectedItem().toString();
                Log.d("FXN", "SELECTED: " + selectedSort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.animator.enter_from_login, R.animator.exit_from_login);
    }

    public String checkChatSort()
    {
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(chatSort, "distance");
        return restoredText;
    }

    public void logout(View v) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(LoggedIn, 0);
        editor.commit();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.enter_from_login, R.animator.exit_from_login);
    }

    public void saveChanges(View v) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String chatsortSelection = checkChatSort();
        EditText name = (EditText) findViewById(R.id.enter_name);
        EditText distance_from_view = (EditText) findViewById(R.id.distance);
        EditText numposts_from_view = (EditText) findViewById(R.id.num_posts);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(handle, name.getText().toString());
        editor.putString(dist, distance_from_view.getText().toString());
        editor.putString(numPosts, numposts_from_view.getText().toString());

        if (!selectedSort.equals(null) && !selectedSort.equals(chatsortSelection))
        {
            Log.d("FXN", "CHANGING TO: " + selectedSort);
            editor.putString(chatSort, selectedSort);
        }
        editor.commit();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.enter_from_login, R.animator.exit_from_login);

    }

}
