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
import android.widget.TextView;

import com.parse.FindCallback;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CountActivity extends Activity {

    double totalAmount = 0.0;
    long tol = (long)0.001;

    SortedList<CountEntry> userList;

    String mEmail;
    String mGroup;
    String mNote;

    boolean isReady;

    EditText mAmountView;
    EditText mResponseView;
    EditText mTextView;

    TextView mNoteView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

       mAmountView = (EditText) findViewById(R.id.ca_response1);
       mResponseView = (EditText) findViewById(R.id.ca_response2);
       mTextView = (EditText) findViewById(R.id.ca_text);
       mNoteView = (TextView) findViewById(R.id.ca_note);

        //Get email and group from previous activity
        Intent intent = getIntent();

        if (null != intent) {

            mEmail= intent.getStringExtra("email");
            mGroup= intent.getStringExtra("group");

            Log.d("ca", "email found: " + mEmail);
            Log.d("ca", "group found: " + mGroup);

        }

        //Check if group exists
        ParseQuery<ParseObject> noteQuery = ParseQuery.getQuery("Groups");
        noteQuery.whereEqualTo("groupName", mGroup);

        noteQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {

                if (e == null) {

                    if (parseObjects.size() > 0) {

                        mNote = parseObjects.get(0).getString("note");

                        mNoteView.setText(mNote);

                    }

                } else {

                    Log.d("ca", e.getMessage());

                }

            }
        });

        //Check if user exists
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupUsers");
        query.whereEqualTo("email", mEmail);
        query.whereEqualTo("groupName", mGroup);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {

                if (e == null) {

                    if(parseObjects.size() > 0){


                        //Get current amount from database
                        ParseObject object  = parseObjects.get(0);

                        Log.d("ca_main", "User found: " + object.getString("email"));
                        Log.d("ca_main", "Group found: " + object.getString("groupName"));

                        String currentAmountString = object.getString("amount");

                        if(((String)currentAmountString).compareTo("N/I") == 0) {

                            totalAmount = 0.0;

                        }else{

                            totalAmount = Double.valueOf(currentAmountString);

                        }

                        mAmountView.setText("Current amount: " + Double.valueOf(totalAmount));

                    }else{

                        Log.d("ca_main", "User not found");

                        mResponseView.setText("Unable to find user");

                    }

                } else {

                    Log.d("ca_main", "Parser not responding");

                }
            }
        });



        Button mAddButton = (Button) findViewById(R.id.ca_button_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   addButton();

            }

        });

        Button mDoneButton = (Button) findViewById(R.id.ca_button_done);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   doneButton();

              }

                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_count, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.ca_menu_logout) {
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

    public void addButton(){

        //Update amount, not sending to database.
        Log.d("ca_add", "button add");

        String intTemp = mTextView.getText().toString();
        totalAmount = totalAmount + Double.valueOf(intTemp);
        mTextView.setText("");
        mAmountView.setText("Current amount: " + totalAmount);
    }

    public void doneButton(){

        //Check is user is in group
        Log.d("ca_done", "button done");
        Log.d("ca_done_try", "User found: " + mEmail);
        Log.d("ca_done_try", "Group found: " + mGroup);

        //Retreives user
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupUsers");
        query.whereEqualTo("email", mEmail);
        query.whereEqualTo("groupName", mGroup);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {

                if (e == null) {

                    ParseObject  object = parseObjects.get(0);

                    Log.d("ca_done_result", "User found: " + object.getString("email"));
                    Log.d("ca_done_result", "Group found: " + object.getString("groupName"));

                    //Update users contribution to group with totalamount.
                    object.put("amount", String.valueOf(totalAmount));

                    object.saveInBackground(new SaveCallback() {

                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {

                                mResponseView.setText("User updated");

                                //Check is all group members have responded.
                                if(isReady()){

                                    Log.d("ca_countAmount", "Initiate");

                                    //Start calculations
                                    ArrayList<CountEntry> fullPay = null;

                                    try {

                                        //Retrive user name, contribution e.t.c and calculate the amount to be transfered to other users.
                                        fullPay = countAmount(fillEntryList(), new ArrayList<CountEntry>());

                                        Log.d("ca_countAmount", "Nbr transactions" + fullPay.size());

                                        HashMap<String, ArrayList<CountEntry>> userMap = new HashMap<String, ArrayList<CountEntry>>();

                                        for(int i = 0 ; i < fullPay.size(); i++){

                                            CountEntry temp = fullPay.get(i);

                                            Log.d("ca_done_result", temp.getName() + " ows " + temp.getPayTo() + ": " + temp.getSum());
                                            sendMail("Picknick division is done", "You owe "+ temp.getPayTo() + " " + temp.getSum(), temp.getName(), "name");

                                        }

                                        //Start resultActivity
                                        Intent in = new Intent(getApplicationContext(), ResultActivity.class);
                                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                        Bundle mBundle = new Bundle();
                                        mBundle.putString("text", "Division is complete, emails are sent to participants");

                                        in.putExtras(mBundle);
                                        startActivity(in);




                                    } catch (Exception e1) {

                                        Log.d("ca_done_result", "Error while calculating sum");

                                    }
                                }

                            } else {

                                mResponseView.setText("User not updated");

                            }
                        }
                    });

                    mTextView.setText("");

                } else {

                    mTextView.setText("");
                    mResponseView.setText("User not found, restart app");
                }
            }
        });

    }

    private boolean isReady(){

        //Check if all participants are ready.
        Log.d("ca_isReady", "method called");

        //Get all users not ready
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupUsers");
        query.whereEqualTo("groupName", mGroup);
        query.whereEqualTo("amount", "N/I");

        Log.d("ca_isReady", "Group found: " + mGroup);

        try {

            List<ParseObject> list = query.find();
            //The number of returned object are the users who are not ready.
            if(list.size() > 0 ) {

                String sendString = "";
                //Create a string with users who are not ready.
                for(ParseObject p : list){

                    String name = p.getString("email");

                    sendString = sendString + name + "\n";
                }
                sendString = sendString + "is still to answer!";

                Log.d("ca_isReady", "sendString: " + sendString );

                //Start RetultActivity and send the names of users who are not ready.
                Intent in = new Intent(getApplicationContext(), ResultActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                Bundle mBundle = new Bundle();
                mBundle.putString("text", sendString);

                in.putExtras(mBundle);
                startActivity(in);

                isReady = false;

            }else{
                isReady = true;
            }

            Log.d("ca_isReady", "isReady: " + isReady );

        } catch (ParseException e) {

            Log.d("ca_isReady", "isReady exception");
        }

        return isReady;
    }



    private SortedList<CountEntry> fillEntryList() {

       //Fills list with users who are in the selected group.
        userList = new SortedList<CountEntry>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupUsers");
        query.whereEqualTo("groupName", mGroup);

        Log.d("ca_fillEntryList", "Group found: " + mGroup);


        List<ParseObject> list = null;

        try {

            list = query.find();


        if (list.size() > 0) {


                        for (int i = 0; i < list.size(); i++) {

                            double tempAmount = Double.valueOf(list.get(i).getString("amount"));
                            String tempName = list.get(i).getString("email");

                            userList.add(new CountEntry(tempAmount, tempName));

                        }

                    }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (userList.size() > 0) {

            double totSum = 0;

            for (int i = 0; i < userList.size(); i++) {

                totSum += userList.get(i).getSum();
            }

            totSum /= userList.size();

            for (int i = 0; i < userList.size(); i++) {

                userList.get(i).setSum(userList.get(i).getSum() - totSum);
            }

            userList.sort();

            return userList;

        } else {

            Log.d("ca_fillEntryList", "error retrieving userList");

            return null;

        }

    }
    private ArrayList<CountEntry> countAmount(SortedList<CountEntry> list,
                                         ArrayList<CountEntry> pay){

        if (list.size() == 1) {


        } else if (list.size() > 1) {

            CountEntry p1 = list.remove(0);
            CountEntry p2 = list.remove(list.size() - 1);

            double sum = p2.getSum() + p1.getSum();

            if (sum < tol) {

                p1.setSum(sum);
                list.add(p1);
                pay.add(new CountEntry(Math.abs(p2.getSum()), p1.getName(), p2
                        .getName()));
                return countAmount(list, pay);

            } else if (sum > tol) {

                p2.setSum(sum);
                list.add(p2);
                pay.add(new CountEntry(Math.abs(p1.getSum()), p1.getName(), p2
                        .getName()));
                return countAmount(list, pay);

            } else {

                pay.add(new CountEntry(Math.abs(p2.getSum()), p1.getName(), p2
                        .getName()));
                return countAmount(list, pay);

            }

        }

        return pay;
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

                Log.e("cloud code example" , "response: " + response);
            }
        });
    }

}
