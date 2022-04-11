package com.skrb7f16.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skrb7f16.chatapp.Models.Users;
import com.skrb7f16.chatapp.databinding.ActivitySigninBinding;

public class SigninActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    ActivitySigninBinding activitySigninBinding;
    FirebaseDatabase database;
    ProgressDialog progressBar;
    FirebaseUser firebaseUser;
    Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        activitySigninBinding=ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(activitySigninBinding.getRoot());
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#03b1fc"));
        actionBar.setBackgroundDrawable(colorDrawable);
        firebaseAuth=FirebaseAuth.getInstance();

        database=FirebaseDatabase.getInstance("https://chatapp-104cb-default-rtdb.firebaseio.com/");
        database.setPersistenceEnabled(true);
        Log.d("meow", "onCreate: ");
        database.getReference().keepSynced(true);


        database.getReference().child("hello").setValue("u r heee");
        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Logging in");
        progressBar.setTitle("Please wait.....");


       if(firebaseAuth.getUid()!=null){
            Intent intent=new Intent(SigninActivity.this,MainActivity.class);
            startActivityForResult(intent,100);
        }
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        activitySigninBinding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    int RC_SIGN_IN=60;
    private void signIn() {
        progressBar.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

            }
        }
        else if(requestCode==100){
            finish();
        }

    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            firebaseUser=firebaseAuth.getCurrentUser();
                            getUserIfExist(firebaseUser.getUid());


                        } else {

                            Log.w("meow", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SigninActivity.this,"Failed to sigin",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    public void getUserIfExist(String userId){
        database.getReference().child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    users = new Users(firebaseUser.getUid(), firebaseUser.getDisplayName());
                    Log.d("meow", "onComplete: " + users.getUserId());
                    database.getReference().child("Users").child(userId).setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.hide();
                            Intent intent=new Intent(SigninActivity.this,MainActivity.class);
                            startActivityForResult(intent,100);
                        }
                    });
                }
                else{
                    progressBar.hide();
                    Intent intent=new Intent(SigninActivity.this,MainActivity.class);
                    startActivityForResult(intent,100);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SigninActivity.this,"cannot to connect to server",Toast.LENGTH_SHORT).show();
            }
        });


    }
}