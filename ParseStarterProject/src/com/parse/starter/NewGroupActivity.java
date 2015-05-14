package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class NewGroupActivity extends Activity {

    TextView mResponseView;
    EditText mTextView;
    EditText mNoteView;
    Button mNextButton;
    String mText;
    String mNote;
    String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        mResponseView = (TextView) findViewById(R.id.nga_response);
        mTextView = (EditText) findViewById(R.id.nga_name);
        mNoteView = (EditText) findViewById(R.id.nga_note);

        //Retrives email from previous activity

        Intent intent = getIntent();

        if (null != intent) {
            mEmail= intent.getStringExtra("email");
            Log.d("nga", "email found: " + mEmail);

        }

        mNextButton = (Button) findViewById(R.id.nga_next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newGroup();

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

        if (id == R.id.nga_menu_logout) {

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

    public void newGroup(){

        //Retrive GUI data.
        mText = mTextView.getText().toString();
        mNote = mNoteView.getText().toString();

        //If group name is not blank.
        if(mText.length() > 0) {

            Log.d("nga", "button newGroup");
            Log.d("nga", "name passed " + mText);
            Log.d("nga", "note passed " + mNote);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Groups");
            query.whereEqualTo("groupName", mText);

            List<ParseObject> loginList = null;

            try {
                loginList = query.find();
                Log.d("la", "Size: " + loginList.size());

                if (loginList.size() == 0) {

            //Save group in database.
            ParseObject object1 = new ParseObject("Groups");

            object1.put("groupName", mText);
            object1.put("note", mNote);

            object1.saveInBackground(new SaveCallback() {

             @Override
             public void done(ParseException e) {

                    if (e == null) {

                        Log.d("nga", "group found: " + mText);

                        //Save group creating user to group.
                        ParseObject object2 = new ParseObject("GroupUsers");

                        object2.put("groupName", mText);
                        object2.put("email", mEmail);
                        object2.put("amount", "N/I");

                        try {

                            object2.save();

                        } catch (ParseException e1) {

                            Log.d("nga", e.getMessage());

                        }

                        //Initiates NewGroupMemberActivity and saves group name and email in Intent.
                        Intent in = new Intent(getApplicationContext(), NewGroupMemberActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                        Bundle mBundle = new Bundle();
                        mBundle.putString("group", mText);
                        mBundle.putString("email", mEmail);

                        in.putExtras(mBundle);
                        startActivity(in);


                        } else {

                        Log.d("nga", e.getMessage());

                    }

                }
            });

                }else{

                    mResponseView.setText("Group name taken");

                }

            } catch (ParseException e) {

                Log.d("la", e.getMessage());
            }

        }else{

            mResponseView.setText("Input group name");

        }

    }
}
