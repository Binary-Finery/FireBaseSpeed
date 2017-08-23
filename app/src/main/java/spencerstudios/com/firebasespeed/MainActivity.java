package spencerstudios.com.firebasespeed;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.internal.kx;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SignInButton signInButton;
    private Button signOut;
    private TextView leaderboard, cpuSpeedTest, signedIn, signInLabel, signInInfo;
    private ImageView launcherIcon;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private final static int RC_SIGN_IN = 2;
    private boolean isSignedIn = false;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        signOut = (Button) findViewById(R.id.button_sign_out);
        signInInfo = (TextView) findViewById(R.id.text_view_sign_info);
        leaderboard = (TextView) findViewById(R.id.button_leader_board);
        cpuSpeedTest = (TextView) findViewById(R.id.button_test_cpu);
        signInLabel = (TextView) findViewById(R.id.tv_sign);
        signedIn = (TextView) findViewById(R.id.text_view_signed_in);
        launcherIcon = (ImageView) findViewById(R.id.image_view_icon);

        signInButton.setOnClickListener(this);
        signOut.setOnClickListener(this);
        leaderboard.setOnClickListener(this);
        cpuSpeedTest.setOnClickListener(this);
        signInInfo.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    displayLoggedInScreen();
                    firebaseUser = mAuth.getCurrentUser();
                    signedIn.setText(firebaseUser.getEmail());
                    isSignedIn = true;
                } else {
                    displayNotLoggedInScreen();
                    isSignedIn = false;
                }
            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                fireBaseAuthWithGoogle(account);
                //displayLoggedInScreen();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                //displayNotLoggedInScreen();
            }
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {

        if (v == signInButton) {
            //sign in
            loadingScreen();
            signIn();
        }
        if (v == signInInfo) {
            displaySignInfoDialog();
        }
        if (v == signOut) {
            //sign out
            FirebaseAuth.getInstance().signOut();
        }
        if (v == leaderboard) {
            //display leader board
            startActivity(new Intent(MainActivity.this, LeaderBoardActivity.class));
        }
        if (v == cpuSpeedTest) {
            //go to cpu test
            Intent i = new Intent(MainActivity.this, CpuSpeedTestActivity.class);
            i.putExtra("signed_in", isSignedIn);
            startActivity(i);
        }
    }

    private void displaySignInfoDialog() {

        AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
        alert.setMessage(getString(R.string.about_dialog_text));
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void displayNotLoggedInScreen() {
        signInButton.setVisibility(View.VISIBLE);
        signInInfo.setVisibility(View.VISIBLE);
        leaderboard.setVisibility(View.GONE);
        signOut.setVisibility(View.GONE);
        signedIn.setVisibility(View.GONE);
        signInLabel.setVisibility(View.GONE);
        launcherIcon.setVisibility(View.GONE);
    }

    private void displayLoggedInScreen() {
        signInButton.setVisibility(View.GONE);
        signInInfo.setVisibility(View.GONE);
        leaderboard.setVisibility(View.VISIBLE);
        signOut.setVisibility(View.VISIBLE);
        signedIn.setVisibility(View.VISIBLE);
        launcherIcon.setVisibility(View.GONE);
        cpuSpeedTest.setVisibility(View.VISIBLE);
        signInLabel.setVisibility(View.VISIBLE);
        try {
            signedIn.setText(firebaseUser.getEmail());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void loadingScreen() {
        signInButton.setVisibility(View.GONE);
        leaderboard.setVisibility(View.GONE);
        signOut.setVisibility(View.GONE);
        cpuSpeedTest.setVisibility(View.GONE);
        signInInfo.setVisibility(View.GONE);
        launcherIcon.setVisibility(View.VISIBLE);
    }
}