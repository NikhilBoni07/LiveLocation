package com.share.livelocation.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.share.livelocation.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    EditText usrid_edtxt, passwrd_edtxt;
    Button login_btn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        usrid_edtxt = (EditText) findViewById(R.id.usrid_edtxt);
        passwrd_edtxt = (EditText) findViewById(R.id.passwrd_edtxt);

        login_btn = (Button) findViewById(R.id.login_btn);

        login_btn.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Sign in logic here.
                    Toast.makeText(LoginActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                String email = usrid_edtxt.getText().toString();
                String password = passwrd_edtxt.getText().toString();
                if (!email.equals("") && !password.equals("")) {
                    mAuth.signInWithEmailAndPassword(email, password);
                } else {
                    Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }
}
