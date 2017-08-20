package spencerstudios.com.firebasespeed;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class CpuSpeedTestActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID, username = "", make, model;

    private TextView tvThis, tvMake, tvModel, tvPerformed, tvTime;
    private long pre = 0L, post = 0L, diff = 0L;
    private final int MAX = 200000000;
    private boolean hasUsername = true;

    FloatingActionButton fabPerform, fabUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_speed_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user != null ? user.getUid() : null;

        make = Build.BRAND.toUpperCase();
        model = Build.MODEL;

        tvMake.setText(make);
        tvModel.setText(model);

        fabPerform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabPerform.setClickable(false);
                new LongOperation().execute("");
            }
        });

        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasUsername){
                    commitTimeToDatabase();
                }else{
                    promptNewUsername();
                }
            }
        });

        DatabaseReference databaseReference = mFirebaseDatabase.getReference(userID).child("userName");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = (dataSnapshot.getValue(String.class));
                    hasUsername = true;
                } else {
                    hasUsername = false;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(CpuSpeedTestActivity.this, getString(R.string.error_message),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void promptNewUsername() {

        LayoutInflater inflater = getLayoutInflater();
        View usernameInfo = inflater.inflate(R.layout.user_name_alert_dialog, null);
        final EditText et = (EditText) usernameInfo.findViewById(R.id.et_username);

        AlertDialog.Builder popup = new AlertDialog.Builder(CpuSpeedTestActivity.this);
        popup.setView(usernameInfo);
        popup.setTitle("Enter a username");
        popup.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = et.getText().toString().trim();
                if (str.length() > 0){
                    username = str;
                    commitTimeToDatabase();
                }else{
                    Toast.makeText(CpuSpeedTestActivity.this, "Oops, invalid username!",Toast.LENGTH_LONG).show();
                }
            }
        });
        popup.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog d = popup.create();
        d.show();
    }

    private void findViews() {
        tvThis = (TextView) findViewById(R.id.text_view_this);
        tvMake = (TextView) findViewById(R.id.text_view_make);
        tvModel = (TextView) findViewById(R.id.text_view_model);
        tvPerformed = (TextView) findViewById(R.id.text_view_performed);
        tvTime = (TextView) findViewById(R.id.text_view_time);
        fabPerform = (FloatingActionButton) findViewById(R.id.fab_test);
        fabUpload = (FloatingActionButton) findViewById(R.id.fab_upload_time);
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = new ProgressDialog(CpuSpeedTestActivity.this);

        @Override
        protected String doInBackground(String... params) {

            pre = System.currentTimeMillis();
            for (int i = 0; i < MAX; i++) {
            }
            post = System.currentTimeMillis();
            diff = (post - pre);

            return NumberFormat.getNumberInstance(Locale.getDefault())
                    .format(diff)
                    .concat("\nmilliseconds");
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            fabPerform.setClickable(true);
            tvTime.setText(result);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Working on it...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    private void commitTimeToDatabase() {
        //Data data = new Data(username, make, model, diff);
        FirebaseUser fbu = mAuth.getCurrentUser();
        mDatabase.child(fbu.getUid()).child("userName").setValue(username);
        mDatabase.child(fbu.getUid()).child("make").setValue(make);
        mDatabase.child(fbu.getUid()).child("model").setValue(model);
        mDatabase.child(fbu.getUid()).child("time").setValue(diff);

    }
}