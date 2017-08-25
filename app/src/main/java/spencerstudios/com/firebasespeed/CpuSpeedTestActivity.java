package spencerstudios.com.firebasespeed;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String username = "", userID;
    private String make;
    private String model;

    private TextView tvThis, tvMake, tvModel, tvPerformed, tvTime;
    private FloatingActionButton fabPerform, fabUpload;
    private LinearLayout rootView;

    private long opsDuration = 0L;
    private boolean hasUsername = true;

    private Animation ltr, rtl, ft, fabUploadAnim;

    private boolean isSignedIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_speed_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewsAndInitialiseAnims();

        Intent i = getIntent();
        isSignedIn = i.getBooleanExtra("signed_in", true);

        if(isSignedIn) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            userID = user != null ? user.getUid() : null;
        }

        make = Build.BRAND.toUpperCase();
        model = Build.MODEL;

        tvMake.setText(make);
        tvModel.setText(model);

        if (!isSignedIn) fabUpload.setVisibility(View.INVISIBLE);

        fabPerform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabPerform.setClickable(false);
                fabUpload.setVisibility(View.INVISIBLE);
                new LongOperation().execute("");
            }
        });

        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabUpload.startAnimation(fabUploadAnim);
                if (hasUsername) {
                    commitTimeToDatabase();
                } else {
                    promptNewUsername();
                }
            }
        });

        if (isSignedIn) {
            assert userID != null;
            databaseReference = mFirebaseDatabase.getReference(userID).child("userName");
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
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void promptNewUsername() {

        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View usernameInfo = inflater.inflate(R.layout.user_name_alert_dialog, null);

        final EditText et = (EditText) usernameInfo.findViewById(R.id.et_username);

        AlertDialog.Builder popup = new AlertDialog.Builder(CpuSpeedTestActivity.this);
        popup.setView(usernameInfo);
        popup.setTitle("Enter a username");
        popup.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = et.getText().toString().trim();
                if (str.length() > 0) {
                    username = str;
                    commitTimeToDatabase();
                } else {
                    Toast.makeText(CpuSpeedTestActivity.this, "Oops, invalid username!", Toast.LENGTH_LONG).show();
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



    private class LongOperation extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = new ProgressDialog(CpuSpeedTestActivity.this);

        @Override
        protected String doInBackground(String... params) {

            long pre = System.currentTimeMillis();
            int MAX = 200000000;
            for (int i = 0; i < MAX; i++){}
            long post = System.currentTimeMillis();

            opsDuration = post - pre;

            return NumberFormat.getNumberInstance(Locale.getDefault()).format(opsDuration).concat("\nmilliseconds");
        }

        @Override
        protected void onPostExecute(String result) {

            progressDialog.dismiss();
            fabPerform.setClickable(true);
            if (isSignedIn) fabUpload.setVisibility(View.VISIBLE);
            tvThis.startAnimation(ft);
            tvTime.startAnimation(ltr);
            tvPerformed.startAnimation(rtl);

            tvThis.setVisibility(View.VISIBLE);
            tvPerformed.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);

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

        FirebaseUser fbu = mAuth.getCurrentUser();

        if (username.equals("")) username = "Anonymous";

        if (hasUsername) {
            assert fbu != null;
            mDatabase.child(fbu.getUid()).child("userName").setValue(username);
            mDatabase.child(fbu.getUid()).child("make").setValue(make);
            mDatabase.child(fbu.getUid()).child("model").setValue(model);
            mDatabase.child(fbu.getUid()).child("time").setValue(opsDuration);
        } else {
            Data userData = new Data(username, make, model, opsDuration);
            mDatabase.child(fbu.getUid()).setValue(userData);
        }
        Snackbar.make(rootView, "Time uploaded to Global Rankings", Snackbar.LENGTH_LONG).show();
    }

    private void findViewsAndInitialiseAnims() {

        rootView = (LinearLayout)findViewById(R.id.cpu_root_view) ;
        tvThis = (TextView) findViewById(R.id.text_view_this);
        tvMake = (TextView) findViewById(R.id.text_view_make);
        tvModel = (TextView) findViewById(R.id.text_view_model);
        tvPerformed = (TextView) findViewById(R.id.text_view_performed);
        tvTime = (TextView) findViewById(R.id.text_view_time);
        fabPerform = (FloatingActionButton) findViewById(R.id.fab_test);
        fabUpload = (FloatingActionButton) findViewById(R.id.fab_upload_time);

        tvThis.setVisibility(View.INVISIBLE);
        tvPerformed.setVisibility(View.INVISIBLE);
        tvTime.setVisibility(View.INVISIBLE);
        fabUpload.setVisibility(View.INVISIBLE);

        ltr = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        rtl = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        ft = AnimationUtils.loadAnimation(this, R.anim.from_top);
        fabUploadAnim = AnimationUtils.loadAnimation(this, R.anim.zoom);
    }
}