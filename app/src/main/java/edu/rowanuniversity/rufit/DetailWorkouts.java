package edu.rowanuniversity.rufit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.rowanuniversity.rufit.rufitObjects.Info;
import edu.rowanuniversity.rufit.rufitObjects.Leader;
import edu.rowanuniversity.rufit.rufitObjects.Run;

/**
 * Created by shiv on 3/31/2017.
 *
 * Display a detailed display of a workout.
 * Allows user to move to editing or deleting currently selected run.
 */

public class DetailWorkouts extends AppCompatActivity {

    Button donebutton;
    ImageView backbutton;
    Run currentRun;
    TextView DateTitle;
    TextView CaloriesBurned;
    TextView DistanceRan;
    TextView TimeWorkout;
    TextView notes;
    TextView shoe, pace;
    private ImageView feel1, feel2, feel3, feel4, feel5, edit, delete;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference runRef;
    final String ROOT = "users";
    FirebaseUser user;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_workout);
        currentRun = (Run) getIntent().getSerializableExtra("Key");
        DateTitle = (TextView) findViewById(R.id.date_value);
        CaloriesBurned = (TextView) findViewById(R.id.calories_workout);
        DistanceRan = (TextView) findViewById(R.id.distance_workout);
        TimeWorkout = (TextView) findViewById(R.id.duration_workout);
        pace  =(TextView) findViewById(R.id.speed_workout);
        notes = (TextView) findViewById(R.id.note_value);
        shoe = (TextView) findViewById(R.id.shoe_value);
        feel1 = (ImageView) findViewById(R.id.feel_value1) ;
        feel2 = (ImageView) findViewById(R.id.feel_value2) ;
        feel3 = (ImageView) findViewById(R.id.feel_value3) ;
        feel4 = (ImageView) findViewById(R.id.feel_value4) ;
        feel5 = (ImageView) findViewById(R.id.feel_value5) ;
        backbutton = (ImageView) findViewById(R.id.backbuttonnnnnn);
        edit = (ImageView) findViewById(R.id.editttttt);
        delete = (ImageView) findViewById(R.id.del);

        //Retrieve user and Firebase instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        runRef = database.getReference(ROOT).child(user.getUid()).child("runs");

        //Initialize text displays
        DateTitle.setText(currentRun.getDate());
        CaloriesBurned.setText("" + currentRun.getCalories());
        DistanceRan.setText("" + currentRun.getMileage());
        TimeWorkout.setText(String.format("%02d",currentRun.getTime()/3600) + ":" + String.format("%02d", currentRun.getTime()/60) + ":" + String.format("%02d", currentRun.getTime()%60));
        pace.setText(String.format("%02d", currentRun.getPace()/60) + ":" + String.format("%02d", currentRun.getPace()%60));
        notes.setText(currentRun.getNotes() == null ? "" : currentRun.getNotes());
        shoe.setText("" + currentRun.getShoe());

        //Initalize difficulty display
        switch(currentRun.getFeel()) {
            case 0 : feel1.setColorFilter(Color.rgb(53, 123, 173));
                break;
            case 1 : feel1.setColorFilter(Color.rgb(53, 173, 56));
                feel2.setColorFilter(Color.rgb(53, 173, 56));
               break;
            case  2 :  feel1.setColorFilter(Color.rgb(247, 225, 59));
                feel2.setColorFilter(Color.rgb(247, 225, 59));
                feel3.setColorFilter(Color.rgb(247, 225, 59));
                break;
            case 3: feel1.setColorFilter(Color.rgb(255, 140, 0));
                feel2.setColorFilter(Color.rgb(255, 140, 0));
                feel3.setColorFilter(Color.rgb(255, 140, 0));
                feel4.setColorFilter(Color.rgb(255, 140, 0));
                break;
            case 4 : feel1.setColorFilter(Color.rgb(198, 19, 19));
                feel2.setColorFilter(Color.rgb(198, 19, 19));
                feel3.setColorFilter(Color.rgb(198, 19, 19));
                feel4.setColorFilter(Color.rgb(198, 19, 19));
                feel5.setColorFilter(Color.rgb(198, 19, 19));
        }

        donebutton = (Button) findViewById(R.id.backButton_detailWorkout);
        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Edit current run
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailWorkouts.this, AddRunActivity.class);
                intent.putExtra("run",currentRun);
                startActivity(intent);
                finish();
            }
        });

        //Delete run
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder a  = new AlertDialog.Builder(DetailWorkouts.this);
                a.setTitle("Delete");
                a.setMessage("Are you sure you want to delete this run?");
                a.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runRef.child(currentRun.getId()).removeValue();
                        finish();
                    }
                });
                a.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        a.create();
                a.show();
            }
        });

        //set up facebook share button here
        FacebookSdk.sdkInitialize(getApplicationContext());
        ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://academics.rowan.edu/csm/departments/cs/"))
                .setQuote("Get running & get fit with RUFit!")
                .build();
        shareButton.setShareContent(content);

        //set up publish button
        Button publishButton = (Button)findViewById(R.id.publishButton);

        //When "publish" button clicked, check to see if the run currently being
        //looked at qualifies for the leaderboards
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDistanceLeaderboard();
                checkPaceLeaderboard();
                checkTimeLeaderboard();
            }
        });
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
                int distance = (int)currentRun.getMileage();
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
                int pace = (int)currentRun.getPace();
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
                int time = currentRun.getTime();
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
                database.getReference().child("users").child(user.getUid()).child("info");

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
}