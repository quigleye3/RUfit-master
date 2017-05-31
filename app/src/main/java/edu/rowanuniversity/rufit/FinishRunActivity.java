package edu.rowanuniversity.rufit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.rowanuniversity.rufit.rufitObjects.Goal;
import edu.rowanuniversity.rufit.rufitObjects.Info;
import edu.rowanuniversity.rufit.rufitObjects.Leader;
import edu.rowanuniversity.rufit.rufitObjects.Run;
import edu.rowanuniversity.rufit.rufitObjects.RunType;
import edu.rowanuniversity.rufit.rufitObjects.Shoe;
import edu.rowanuniversity.rufit.rufitObjects.User;

public class FinishRunActivity extends AppCompatActivity {

    private Run run;

    private TextView dateView,distanceView,timeView,paceDisplay,caloriesDisplay;
    private EditText editName,notesEdit;
    private Spinner shoeSpinner, typeSpinner;
    private SeekBar seekBar;
    private Button submit;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference myRef, runRef,shoeRef, goalRef;
    private String userID;
    private HashMap<String,Object> currentUser;
    private Goal userGoals;
    private HashMap<String,Shoe> shoes;
    private GenericTypeIndicator<HashMap<String,Shoe>> sGeneric = new GenericTypeIndicator<HashMap<String,Shoe>>() {};
    private GenericTypeIndicator<User<String,Object>> generic = new GenericTypeIndicator<User<String,Object>>() {};
    private int check = 0;
    private int weight;

    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_run);

        Intent intent = getIntent();
        run = (Run) intent.getSerializableExtra("Run");

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner) ;
        shoeSpinner = (Spinner) findViewById(R.id.shoeSpinner);

        distanceView = (TextView) findViewById(R.id.distanceView);
        timeView = (TextView) findViewById(R.id.timeView);
        paceDisplay = (TextView) findViewById(R.id.paceDisplay);
        dateView = (TextView) findViewById(R.id.dateView);
        caloriesDisplay = (TextView) findViewById(R.id.caloriesEdit);

        editName = (EditText) findViewById(R.id.editName);
        notesEdit = (EditText) findViewById(R.id.notesEdit);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        submit = (Button) findViewById(R.id.submit);

        distanceView.setText(Double.toString(run.getMileage()));
        int time = run.getTime();
        timeView.setText(Integer.toString(time));
        paceDisplay.setText(Integer.toString(run.getPace()));
        dateView.setText(run.getDate());
        caloriesDisplay.setText(Integer.toString(run.getCalories()));

        //retrieve current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        //database instance
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users").child(userID);
        runRef = myRef.child("runs");
        shoeRef = myRef.child("shoes");
        goalRef = myRef.child("goals");

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run.setName(editName.getText().toString());
            }
        });
        notesEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run.setNotes(notesEdit.getText().toString());
            }
        });

        //TYPE
        final List<String> spinnerArray1 = new ArrayList<>();
        for(int i = 0; i < RunType.values().length; i++) {
            spinnerArray1.add((RunType.values()[i]).toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                progressChanged = progress;
                if (progress == 0) {
                    seekBar.setBackgroundColor(Color.rgb(53, 123, 173));
                } else if (progress ==1) {
                    seekBar.setBackgroundColor(Color.rgb(53, 173, 56));
                } else if (progress ==2) {
                    seekBar.setBackgroundColor(Color.rgb(247, 225, 59));
                }else if ( progress ==3) {
                    seekBar.setBackgroundColor(Color.rgb(255,140,0));
                }else if ( progress == 4) {
                    seekBar.setBackgroundColor(Color.rgb(198, 19, 19));
                }
                run.setFeel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Retrieve user shoes
                shoes = new HashMap<>();
                shoes = dataSnapshot.child("shoes").getValue(sGeneric);
                if(check == 0) {
                    populateShoeSpinner();
                }

                //Retrive User's weight
                currentUser = dataSnapshot.getValue(generic);
                HashMap<String,Object> info = (HashMap<String,Object>) currentUser.get("info");
                weight = Integer.parseInt(info.get("weight").toString());


                //Retrieve user goals
                DataSnapshot goalsSnapshot = dataSnapshot.child("goals");
                userGoals = new Goal();
                userGoals.setMilesPerWeekTarget(Double.parseDouble(goalsSnapshot.child("milesPerWeekTarget").getValue().toString()));
                userGoals.setRunsPerWeekTarget(Integer.parseInt(goalsSnapshot.child("runsPerWeekTarget").getValue().toString()));
                if (goalsSnapshot.child("dateOfRace").getValue() == null) {
                    userGoals.setDaysUntilRace("");
                } else {
                    userGoals.setDaysUntilRace(goalsSnapshot.child("dateOfRace").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runRef.push().setValue(run);

                //Update goals
                if(userGoals.getMilesPerWeekTarget()>0) {
                    userGoals.addMiles(run.getMileage());
                }

                //Leaderboard stuff:
                //prompt user to share on leaderboard w/ dialog box
                createLeaderboardDialog();
                //createFBShareDialog();
                //leaveActivity();
            }
        });
    }

    private void leaveActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    private void populateShoeSpinner(){
        final List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("None");
        //SHOES
        final List<String> spinnerArray2 = new ArrayList<>();
        if(shoes!=null) {
            for (String s : shoes.keySet()) {
                spinnerArray2.add(shoes.get(s).getName());
            }
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray2);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shoeSpinner.setAdapter(adapter2);
        }
        check = 1;
    }

    private void createLeaderboardDialog()
    {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.leaderboard_dialog);

        //Retrieve components
        Button dialogYesButton = (Button) dialog.findViewById(R.id.yesButton);
        Button dialogNoButton = (Button) dialog.findViewById(R.id.noButton);

        //When "no" button is clicked, close dialog.
        dialogNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                createFBShareDialog();
                //leaveActivity();
            }
        });

        //When yes button is clicked, compare run data to current leaderboards
        //and update accordingly
        dialogYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDistanceLeaderboard();
                checkPaceLeaderboard();
                checkTimeLeaderboard();
                dialog.dismiss();
                createFBShareDialog();
                //leaveActivity();
            }
        });

        dialog.show(); //display dialog
    }

    //check if the current run's distance places on the leaderboard,
    //and update it accordingly
    private void checkDistanceLeaderboard()
    {
        //get database reference to distance leaderboard
        final DatabaseReference distRef =
                database.getReference().child("leaderboard").child("distance");

        distRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get the current leaders from the database
                GenericTypeIndicator<HashMap<String,Leader>> generic = new GenericTypeIndicator<HashMap<String,Leader>>() {};
                HashMap<String,Leader> leaders;
                leaders = dataSnapshot.getValue(generic);

                //set up the potential new leaderboard entry
                int distance = (int)run.getMileage();
                Leader newLeader = new Leader();
                newLeader.setData(distance);
                newLeader.setName("temp"); //this will be replaced via insertUsername()

                //check if the time for this run is better than any of the existing entries
                //the longest distance is the best distance here
                if(distance < leaders.get("distuser1").getData()
                        && distance < leaders.get("distuser2").getData()
                        && distance > leaders.get("distuser3").getData())
                {
                    distRef.child("distuser3").setValue(newLeader);
                    insertUsername("distance", "distuser3");
                }
                else if(distance < leaders.get("distuser1").getData()
                        && distance > leaders.get("distuser2").getData()
                        && distance > leaders.get("distuser3").getData())
                {
                    distRef.child("distuser2").setValue(newLeader);
                    distRef.child("distuser3").setValue(leaders.get("distuser2"));
                    insertUsername("distance", "distuser2");
                }
                else if(distance > leaders.get("distuser1").getData()
                        && distance > leaders.get("distuser2").getData()
                        && distance > leaders.get("distuser3").getData())
                {
                    distRef.child("distuser1").setValue(newLeader);
                    distRef.child("distuser2").setValue(leaders.get("distuser1"));
                    distRef.child("distuser3").setValue(leaders.get("distuser2"));
                    insertUsername("distance", "distuser1");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    //check if the current run's pace places on the leaderboard,
    //and update it accordingly
    private void checkPaceLeaderboard()
    {
        //get database reference to pace leaderboard
        final DatabaseReference paceRef =
                database.getReference().child("leaderboard").child("pace");

        paceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get the current leaders from the database
                GenericTypeIndicator<HashMap<String,Leader>> generic =
                        new GenericTypeIndicator<HashMap<String,Leader>>() {};
                HashMap<String,Leader> leaders;
                leaders = dataSnapshot.getValue(generic);

                //set up the potential new leaderboard entry
                int pace = (int)run.getPace();
                Leader newLeader = new Leader();
                newLeader.setData(pace);
                newLeader.setName("temp"); //this will be replaced via insertUsername()

                //check if the time for this run is better than any of the existing entries
                //the greatest pace is the best pace here
                if(pace < leaders.get("paceuser1").getData()
                        && pace < leaders.get("paceuser2").getData()
                        && pace > leaders.get("paceuser3").getData())
                {
                    paceRef.child("paceuser3").setValue(newLeader);
                    insertUsername("pace", "paceuser3");
                }
                else if(pace < leaders.get("paceuser1").getData()
                        && pace > leaders.get("paceuser2").getData()
                        && pace > leaders.get("paceuser3").getData())
                {
                    paceRef.child("paceuser2").setValue(newLeader);
                    paceRef.child("paceuser3").setValue(leaders.get("paceuser2"));
                    insertUsername("pace", "paceuser2");
                }
                else if(pace > leaders.get("paceuser1").getData()
                        && pace > leaders.get("paceuser2").getData()
                        && pace > leaders.get("paceuser3").getData())
                {
                    paceRef.child("paceuser1").setValue(newLeader);
                    paceRef.child("paceuser2").setValue(leaders.get("paceuser1"));
                    paceRef.child("paceuser3").setValue(leaders.get("paceuser2"));
                    insertUsername("pace", "paceuser1");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    //check if the current run's time places on the leaderboard,
    //and update it accordingly
    private void checkTimeLeaderboard()
    {
        //get database reference to time leaderboard
        final DatabaseReference timeRef =
                database.getReference().child("leaderboard").child("time");

        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get the current leaders from the database
                GenericTypeIndicator<HashMap<String,Leader>> generic =
                        new GenericTypeIndicator<HashMap<String,Leader>>() {};
                HashMap<String,Leader> leaders;
                leaders = dataSnapshot.getValue(generic);

                //set up the potential new leaderboard entry
                int time = run.getTime();
                Leader newLeader = new Leader();
                newLeader.setData(time);
                newLeader.setName("temp"); //this will be replaced via insertUsername()

                //check if the time for this run is better than any of the existing entries
                //the quickest time is the best time here
                if(time > leaders.get("timeuser1").getData()
                        && time > leaders.get("timeuser2").getData()
                        && time < leaders.get("timeuser3").getData())
                {
                    timeRef.child("timeuser3").setValue(newLeader);
                    insertUsername("time", "timeuser3");
                }
                else if(time > leaders.get("timeuser1").getData()
                        && time < leaders.get("timeuser2").getData()
                        && time < leaders.get("timeuser3").getData())
                {
                    timeRef.child("timeuser2").setValue(newLeader);
                    timeRef.child("timeuser3").setValue(leaders.get("timeuser2"));
                    insertUsername("time", "timeuser2");
                }
                else if(time < leaders.get("timeuser1").getData()
                        && time < leaders.get("timeuser2").getData()
                        && time < leaders.get("timeuser3").getData())
                {
                    timeRef.child("timeuser1").setValue(newLeader);
                    timeRef.child("timeuser2").setValue(leaders.get("timeuser1"));
                    timeRef.child("timeuser3").setValue(leaders.get("timeuser2"));
                    insertUsername("time", "timeuser1");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    //replace the temp username in the leaderboards with the user's actual username
    private void insertUsername(String board, String key)
    {
        final String leaderboard = board;
        final String leaderKey = key;

        DatabaseReference infoRef =
                database.getReference().child("users").child(userID).child("info");

        infoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(Info.class).getUsername();
                DatabaseReference leaderRef =
                        database.getReference().child("leaderboard").child(leaderboard).child(leaderKey).child("name");
                leaderRef.setValue(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void createFBShareDialog() {

        final Dialog dialog = new Dialog(context);
        //not sure if this next line is necessary, but it works when it's here so I'm leaving it
        FacebookSdk.sdkInitialize(getApplicationContext());
        dialog.setContentView(R.layout.facebook_share_dialog);

        final ShareButton shareButton = (ShareButton)dialog.findViewById(R.id.fb_share_button);
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://academics.rowan.edu/csm/departments/cs/"))
                .setQuote("Just finished a run with RUFit!")
                .build();
        shareButton.setShareContent(content);

        //When "share" button clicked, share and then close dialog & activity after sharing
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                leaveActivity();
            }
        });

        Button dialogDoNotShareButton = (Button) dialog.findViewById(R.id.doNotShareButton);

        //When "don't share" button is clicked, close dialog.
        dialogDoNotShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                leaveActivity();
            }
        });

        dialog.show();
    }
}
