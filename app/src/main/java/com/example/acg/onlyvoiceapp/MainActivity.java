package com.example.acg.onlyvoiceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.acg.onlyvoiceapp.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;

    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    EditText emailLgn;
    EditText passLgn;
    Button registerLgn;
    Button signInBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //find by id
        emailLgn = findViewById(R.id.emailLgn);
        passLgn  = findViewById(R.id.passLgn);
        registerLgn   = findViewById(R.id.registerLgn);
        signInBtn = findViewById(R.id.signInBtn);


        //configure Google Sign in
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        //firebase auth
        mAuth =FirebaseAuth.getInstance();

        //Google sign in button
        binding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "On click start sign in");
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);

            }
        });

        //Signin button (without google)
        signInBtn.setOnClickListener(view -> {
            loginUser();
        });

        //Register new user
        registerLgn.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class))
        );

        }

    private void loginUser() {

        String email = binding.emailLgn.getText().toString().trim();
        String password = binding.passLgn.getText().toString().trim();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

            if (TextUtils.isEmpty(email)) {
                emailLgn.setError("Email cannot be empty");
                emailLgn.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                passLgn.setError("Password cannot be empty");
                passLgn.requestFocus();
            } else
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //check if email is verified before signing in
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                checkUser(email);
                            }else if(!mAuth.getCurrentUser().isEmailVerified()){
                                Toast.makeText(MainActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                mAuth.getCurrentUser().sendEmailVerification();}
                        }else if(!mAuth.getCurrentUser().isEmailVerified()){
                            Toast.makeText(MainActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                            mAuth.getCurrentUser().sendEmailVerification();
                        //if email is wrong or not verified show error
                        } else {
                            Toast.makeText(MainActivity.this, "Log in failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Log.d(TAG,"Google sign in result");
            Task<GoogleSignInAccount> accountTask =GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //firebase auth if ok
                GoogleSignInAccount account =accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }
            catch (Exception e){
                //if fail show message
                Log.d(TAG,"Result" + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "Start firebase google auth" );
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login success
                        Log.d(TAG,"Success");

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();
                        Log.d(TAG,"UID"+ uid);
                        Log.d(TAG,"EMAIL"+ email);

                        //Check if user is new or existing
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            //if new user
                            Toast.makeText(MainActivity.this,"Account created", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //if existing user
                            Toast.makeText(MainActivity.this,"Welcome", Toast.LENGTH_SHORT).show();
                        }
                        checkUser(email);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //login failed
                        Log.d(TAG,"Loggin failed");
                    }
                });


    }

    private void checkUser(String email){
        //get a reference of the db
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //get the users node
        DatabaseReference myNodeRef = database.getReference("Users");
        //check the email
        Query query = myNodeRef.orderByChild("email").equalTo(email);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //If user has a profile go to wall activity else go to creation
                if (dataSnapshot.exists()) {
                    startActivity(new Intent(MainActivity.this, WallActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, ProfileCreationActivity.class));
                }
                finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }

}


