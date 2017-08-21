package spencerstudios.com.firebasespeed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    private ArrayList<Data> userInfo;
    private ListView leaderboardListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){getSupportActionBar().hide();}

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        userInfo = new ArrayList<>();

        leaderboardListView = (ListView)findViewById(R.id.leader_board_list_view) ;

        final LeaderBoardAdapter leaderBoardAdapter = new LeaderBoardAdapter(this, userInfo);

        leaderboardListView.setAdapter(leaderBoardAdapter);

        myRef.orderByChild("time").limitToLast(1000).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userInfo.clear();

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                    String username = (String) childDataSnapshot.child("userName").getValue();
                    String make = (String) childDataSnapshot.child("make").getValue();
                    String model = (String)childDataSnapshot.child("model").getValue();
                    long time = (long)childDataSnapshot.child("time").getValue();

                    userInfo.add(new Data(username, make, model, time));
                }
                leaderBoardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
