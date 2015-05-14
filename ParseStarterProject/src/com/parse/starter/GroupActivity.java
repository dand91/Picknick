package com.parse.starter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import com.parse.ParseObject;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GroupActivity extends Activity  {

    String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);


        //Get user information from intent.
        Intent intent = getIntent();

        if (null != intent) {

            mEmail= intent.getStringExtra("email");
            Log.d("ga", "email found: " + mEmail);

        }

        Button mNewButton = (Button) findViewById(R.id.ga_new_button);
        mNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newGroup();


            }

        });

        Button mSelectButton = (Button) findViewById(R.id.ga_select_button);
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectGroup();

            }


        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.ga_menu_logout) {
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

    Log.d("ga", "button newGroup");


    Intent in = new Intent(getApplicationContext(), NewGroupActivity.class);
    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

    Bundle mBundle = new Bundle();
    mBundle.putString("email", mEmail);
    in.putExtras(mBundle);
    startActivity(in);

}
    public void selectGroup(){

        Log.d("ga", "button selectGroup");

        Intent in = new Intent(getApplicationContext(), SelectGroupActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        Bundle mBundle = new Bundle();
        mBundle.putString("email", mEmail);
        in.putExtras(mBundle);
        startActivity(in);

    }
}
