package edu.rowanuniversity.rufit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import edu.rowanuniversity.rufit.rufitObjects.Leader;

public class LeaderboardActivity extends AppCompatActivity {

    FirebaseDatabase database;

    DatabaseReference distRef, paceRef, timeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);

        database = FirebaseDatabase.getInstance();

        updateDist();
        updatePace();
        updateTime();

    }

    private void updateDist()
    {
        //set database reference
        distRef = database.getReference().child("leaderboard").child("distance");

        distRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get leader info from database
                GenericTypeIndicator<HashMap<String,Leader>> generic =
                        new GenericTypeIndicator<HashMap<String,Leader>>() {};
                HashMap<String,Leader> leaders;
                leaders = dataSnapshot.getValue(generic);

                //create the views
                TextView topDistUser1 = (TextView) findViewById(R.id.num1DistUser);
                TextView topDist1 = (TextView) findViewById(R.id.num1Dist);
                TextView topDistUser2 = (TextView) findViewById(R.id.num2DistUser);
                TextView topDist2 = (TextView) findViewById(R.id.num2Dist);
                TextView topDistUser3 = (TextView) findViewById(R.id.num3DistUser);
                TextView topDist3 = (TextView) findViewById(R.id.num3Dist);

                //show the data
                topDistUser1.setText(leaders.get("distuser1").getName());
                topDist1.setText(Integer.toString(leaders.get("distuser1").getData()));
                topDistUser2.setText(leaders.get("distuser2").getName());
                topDist2.setText(Integer.toString(leaders.get("distuser2").getData()));
                topDistUser3.setText(leaders.get("distuser3").getName());
                topDist3.setText(Integer.toString(leaders.get("distuser3").getData()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void updatePace()
    {
        //set database reference
        paceRef = database.getReference().child("leaderboard").child("pace");

        paceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get leader info from database
                GenericTypeIndicator<HashMap<String,Leader>> generic =
                        new GenericTypeIndicator<HashMap<String,Leader>>() {};
                HashMap<String,Leader> leaders;
                leaders = dataSnapshot.getValue(generic);

                //create the views
                TextView topPaceUser1 = (TextView) findViewById(R.id.num1PaceUser);
                TextView topPace1 = (TextView) findViewById(R.id.num1Pace);
                TextView topPaceUser2 = (TextView) findViewById(R.id.num2PaceUser);
                TextView topPace2 = (TextView) findViewById(R.id.num2Pace);
                TextView topPaceUser3 = (TextView) findViewById(R.id.num3PaceUser);
                TextView topPace3 = (TextView) findViewById(R.id.num3Pace);

                //show the data
                topPaceUser1.setText(leaders.get("paceuser1").getName());
                topPace1.setText(Integer.toString(leaders.get("paceuser1").getData()));
                topPaceUser2.setText(leaders.get("paceuser2").getName());
                topPace2.setText(Integer.toString(leaders.get("paceuser2").getData()));
                topPaceUser3.setText(leaders.get("paceuser3").getName());
                topPace3.setText(Integer.toString(leaders.get("paceuser3").getData()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void updateTime()
    {
        //set specific database references
        timeRef = database.getReference().child("leaderboard").child("time");

        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get leader info from database
                GenericTypeIndicator<HashMap<String,Leader>> generic =
                        new GenericTypeIndicator<HashMap<String,Leader>>() {};
                HashMap<String,Leader> leaders;
                leaders = dataSnapshot.getValue(generic);

                //create the views
                TextView topTimeUser1 = (TextView) findViewById(R.id.num1TimeUser);
                TextView topTime1 = (TextView) findViewById(R.id.num1Time);
                TextView topTimeUser2 = (TextView) findViewById(R.id.num2TimeUser);
                TextView topTime2 = (TextView) findViewById(R.id.num2Time);
                TextView topTimeUser3 = (TextView) findViewById(R.id.num3TimeUser);
                TextView topTime3 = (TextView) findViewById(R.id.num3Time);

                //show the data
                topTimeUser1.setText(leaders.get("timeuser1").getName());
                topTime1.setText(Integer.toString(leaders.get("timeuser1").getData()));
                topTimeUser2.setText(leaders.get("timeuser2").getName());
                topTime2.setText(Integer.toString(leaders.get("timeuser2").getData()));
                topTimeUser3.setText(leaders.get("timeuser3").getName());
                topTime3.setText(Integer.toString(leaders.get("timeuser3").getData()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}