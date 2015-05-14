package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class SelectGroupActivity extends Activity {

    EditText mResponseView;
    EditText mTextView;
    String mEmail;
    String mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        mResponseView = (EditText) findViewById(R.id.sga_response);
        mTextView = (EditText) findViewById(R.id.sga_text);

        Intent intent = getIntent();

        if (null != intent) { //Null Checking
            mEmail= intent.getStringExtra("email");
            Log.d("sga", "email found: " + mEmail);

        }


       Button  mNextButton = (Button) findViewById(R.id.sga_next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    next();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sga_menu_logout) {

            //Clear saved user data, start LoginActivity.

            SharedPreferences pref = getApplicationContext().getSharedPreferences("data", 0);
            SharedPreferences.Editor editor = pref.edit();

            editor.clear();
            editor.commit();

            Intent in = new Intent(getApplicationContext(), LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(in);
            return true;

        } else if (id == R.id.sga_menu_remove) {


            //TODO add remove
        }


            return super.onOptionsItemSelected(item);
    }


    public void next(){

        Log.d("sga", "button next");

        mGroup = mTextView.getText().toString();

        if(mGroup.length() > 0) {

            Log.d("sga", "text passed");

            ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupUsers");

            query.whereEqualTo("groupName", mGroup);
            query.whereEqualTo("email", mEmail);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {

                    if (e == null) {

                        if (parseObjects.size() > 0) {

                            Log.d("sga", "group found: " + mGroup);

                            Intent in = new Intent(getApplicationContext(), CountActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                            Bundle mBundle = new Bundle();
                            mBundle.putString("group", mGroup);
                            mBundle.putString("email", mEmail);
                            in.putExtras(mBundle);
                            startActivity(in);


                        } else {


                            mResponseView.setText("Group not found");

                        }

                    } else {

                        Log.d("sga",e.getMessage());


                    }
                }
            });

        }else{

            mResponseView.setText("Input group name");

        }
    }

}
