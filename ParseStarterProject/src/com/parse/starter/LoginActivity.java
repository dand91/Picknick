package com.parse.starter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.AsyncTask;
import android.service.textservice.SpellCheckerService;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class LoginActivity extends Activity {

    EditText mEmailView;
    EditText mPasswordView;
    TextView mInfoView;

    String mPassword;
    String mEmail;
    String mSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Check if user is saved.
        SharedPreferences pref = getApplicationContext().getSharedPreferences("data", 0);

        String email = pref.getString("key_email", null);
        String password = pref.getString("key_password", null);

        //if so, login.
        if(email != null && password != null) {

            //Check is user is in database
            ParseQuery<ParseObject> loginSave = ParseQuery.getQuery("_User");
            loginSave.whereEqualTo("email", email);
            loginSave.whereEqualTo("pass", password);

            Log.d("la_Save", "email: " + email);
            Log.d("la_Save", "password: " + password);

            List<ParseObject> loginList = null;

            try {
                loginList = loginSave.find();

                Log.d("la_Save", "Size: " + loginList.size());

                if (loginList.size() > 0) {

                    //Start GroupActicity, set email in intent.
                    Intent in = new Intent(getApplicationContext(), GroupActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                    Bundle mBundle = new Bundle();
                    mBundle.putString("email", email);
                    in.putExtras(mBundle);
                    startActivity(in);
                }

            } catch (ParseException e) {
                Log.d("la", e.getMessage());
            }
        }

        mEmailView = (EditText) findViewById(R.id.la_email);
        mPasswordView = (EditText) findViewById(R.id.la_password);
        mInfoView = (TextView) findViewById(R.id.la_response);

        //Get user information from intent.
        Intent intent = getIntent();

        if (null != intent) {

            mEmail= intent.getStringExtra("email");
            mSign = intent.getStringExtra("sign");

            if(mEmail != null) {
                mInfoView.setText("Wrong email or password");
            }else if(mSign != null){
                mInfoView.setText(mSign);

            }
        }

        Button mNextButton = (Button) findViewById(R.id.la_login_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();
            }

        });

        Button mNewButton = (Button) findViewById(R.id.la_create_button);
        mNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newUser();

            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.la_menu_logout) {

            //Clear saved user data, start LoginActivity.
            SharedPreferences pref = getApplicationContext().getSharedPreferences("data", 0);
            SharedPreferences.Editor editor = pref.edit();

            editor.clear();
            editor.commit();

            Intent in = new Intent(getApplicationContext(), LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(in);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void login() {

        Log.d("la", "button login");

        //Get GUI data
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        Log.d("la", "button login email " + mEmail);
        Log.d("la", "button login password " + mPassword);

        //Check is user is in database
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("email", mEmail);
        query.whereEqualTo("pass", mPassword);

            List<ParseObject> loginList = null;

            try {
                loginList = query.find();
                Log.d("la", "Size: " + loginList.size());

                if (loginList.size() > 0) {

                    //Save user information for automated login
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("data", 0);

                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("key_email", mEmail);
                    editor.putString("key_password", mPassword);

                    editor.commit();

                    Log.d("la", "Login successful: " + mEmail);

                    //Start GroupActivity and save user information in intent.
                    Intent in = new Intent(getApplicationContext(), GroupActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                    Bundle mBundle = new Bundle();
                    mBundle.putString("email", mEmail);
                    in.putExtras(mBundle);
                    startActivity(in);

                } else {

                   mInfoView.setText("Wrong email or password");
                   mEmailView.setText("");
                   mPasswordView.setText("");

                }

            } catch (ParseException e) {
                Log.d("la", e.getMessage());
            }
        }

                public void newUser () {

                    Log.d("la", "button newUser");
                    Intent in = new Intent(getApplicationContext(), NewUserActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    startActivity(in);

                }

            }