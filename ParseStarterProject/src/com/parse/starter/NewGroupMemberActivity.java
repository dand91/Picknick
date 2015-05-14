package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class NewGroupMemberActivity extends Activity {

    private TextView mResponseView;
    private EditText mTextView;

    String tempName;
    String mEmail;
    String mGroup;

    private ListView mList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_member);


        mTextView = (EditText) findViewById(R.id.ngma_text);
        mResponseView = (TextView) findViewById(R.id.ngma_response);

        mList = (ListView) findViewById(R.id.ngma_list);
        arrayList = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        mList.setAdapter(adapter);


        //Retrive data form Intent
        Intent intent = getIntent();

        if (null != intent) { //Null Checking

            mGroup= intent.getStringExtra("group");
            mEmail= intent.getStringExtra("email");

            Log.d("ngma", "email found: " + mEmail);
            Log.d("ngma", "group found: " + mGroup);

        }

        Button mNewMemberButton = (Button) findViewById(R.id.ngma_button_add);
        mNewMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              checkUser();
            }
        });

        Button mNewMemberDoneButton = (Button) findViewById(R.id.ngma_button_done);
        mNewMemberDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              done();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.ngma_menu_logout) {

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

    public void done(){

        Log.d("ngma", "button done");

        //If at least one user is selected.

        if(arrayList.size() >= 1){

            //Start CountActivity and save emial and group to Intent.
            Intent in = new Intent(getApplicationContext(),CountActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            Bundle mBundle = new Bundle();
            mBundle.putString("email", mEmail);
            mBundle.putString("group", mGroup);

            in.putExtras(mBundle);
            startActivity(in);


        }else{

            mResponseView.setText("Input users");

        }
    }

    public void checkUser() {

        Log.d("ngma", "button checkUser");

        tempName = mTextView.getText().toString();
        //Clear text fields.
        mTextView.setText("");
        mResponseView.setText("");

        Log.d("ngma", "name: " + tempName);

        //Checks if user extists.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");

        query.whereEqualTo("email", tempName);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects1, com.parse.ParseException e) {

                if (e == null) {

                    if(parseObjects1.size() > 0) {

                        //Check is in group already.

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupUsers");

                        query.whereEqualTo("email", tempName);
                        query.whereEqualTo("groupName", mGroup);

                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> parseObjects2, com.parse.ParseException e) {

                                if (e == null) {

                                    //If user already exists in group
                                    Log.d("ngma", "size: " + parseObjects2.size());

                                    if (parseObjects2.size() > 0) {

                                        Log.d("ngma", "user found on server");

                                        mResponseView.setText("User already added");

                                    } else {


                                        //Add user to group

                                        arrayList.add(tempName);
                                        adapter.notifyDataSetChanged();

                                        ParseACL acl = new ParseACL();
                                        acl.setPublicReadAccess(true);
                                        acl.setPublicWriteAccess(true);
                                        ParseObject create = new ParseObject("GroupUsers");
                                        create.put("groupName", mGroup);
                                        create.put("email", tempName);
                                        create.put("amount", "N/I");
                                        create.setACL(acl);
                                        create.saveInBackground();

                                        sendMail("Picknick", "You have been added to group: "
                                                + mGroup + ". Please respond as soon as possible", tempName, "name");

                                    }

                                } else {

                                    Log.d("ngma", e.getMessage());

                                }
                            }
                        });


                    }else{

                        mResponseView.setText("No such user");

                    }


                } else {

                    Log.d("ngma", e.getMessage());

                }
            }
        });

    }
    private void sendMail(String subject, String text, String toE, String toN) {
        Map<String, String> params = new HashMap<>();

        params.put("subject", subject);
        params.put("text", text);
        params.put("toEmail", toE);
        params.put("toName", toN);
        params.put("fromEmail", "davidandersson91@me.com");
        params.put("fromName", "Picknick");

        ParseCloud.callFunctionInBackground("sendMail", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                Log.e("cloud code example", "response: " + response);
            }
        });
    }
}
