package com.parse.starter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.text.ParseException;


public class NewUserActivity extends Activity {

    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPhoneView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        Log.d("nua", "");

        mNameView = (EditText) findViewById(R.id.nua_name);
        mEmailView = (EditText) findViewById(R.id.nua_email);
        mPasswordView = (EditText) findViewById(R.id.nua_password);
        mPhoneView = (EditText) findViewById(R.id.nua_phone);

        Button mLoginButton = (Button) findViewById(R.id.nua_button_signup);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreate();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nua_menu_logout) {

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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void attemptCreate() {

        Log.d("nua", "button attemptCreate");

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();
        String phone = mPhoneView.getText().toString();
        Log.d("nua", "email :" + email);
        Log.d("nua", "password :" + password);
        Log.d("nua", "name :" + name);
        Log.d("nua", "phone :" + phone);

        boolean cancel = false;
        View focusView = null;
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(!isNameValid(name)){
            mNameView.setError("Input not a correct name. Use ______");
            focusView = mNameView;
            cancel = true;
        }

        if(!isPhoneValid(phone)){
            mNameView.setError("Input not a correct phone number. User _____");
            focusView = mPhoneView;
            cancel = true;

        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Log.d("nua", "Creating user");

            ParseUser user = new ParseUser();
            user.setUsername(name);
            user.setPassword(password);
            user.setEmail(email);
            user.put("phone", phone);
            user.put("pass", password);
            user.signUpInBackground(new SignUpCallback() {

                @Override
                public void done(com.parse.ParseException e) {

                        if (e == null) {
                            Log.d("nua", "user created");
                        } else {
                            Log.d("nua", "user not created");
                            Log.d("nua", e.getMessage());

                        }
                    }

            });

            Intent in = new Intent(getApplicationContext(), LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            Bundle mBundle = new Bundle();
            mBundle.putString("sign", "User created");
            in.putExtras(mBundle);
            startActivity(in);


        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isNameValid(String name) {
        //TODO: Replace this with your own logic
        return true;
    }
    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return true;
    }
}
