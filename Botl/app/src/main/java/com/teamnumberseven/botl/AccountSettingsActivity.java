package com.teamnumberseven.botl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Kevin on 4/13/2017.
 */

public class AccountSettingsActivity extends AppCompatActivity{

    public static final String MyPREFERENCES = "UserInfo";
    public static final String Name = "nameKey";
    public static final String UserID = "idKey";
    public static final String LoggedIn = "loggedInKey";
    public static final String chatSort = "chartsortKey";
    String selectedSort = null;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);

        //Intent intent = getIntent();
        //thread_id = intent.getStringExtra("thread_id");
        //EditText editText = (EditText) findViewById(R.id.editText);

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
        String restoredText = prefs.getString(chatSort, null);
        Log.d("FXN", "Chat Sort: "+restoredText);
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

        SharedPreferences.Editor editor = sharedpreferences.edit();

        if (!selectedSort.equals(null) && !selectedSort.equals(chatsortSelection))
        {
            Log.d("FXN", "CHANGING TO: " + selectedSort);
            editor.putString(chatSort, selectedSort);
            editor.commit();

        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.enter_from_login, R.animator.exit_from_login);

    }

}
