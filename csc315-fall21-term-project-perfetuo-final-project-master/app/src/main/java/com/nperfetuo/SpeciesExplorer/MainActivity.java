package com.nperfetuo.SpeciesExplorer;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    private ConstraintLayout mLoggedInGroup;
    private ConstraintLayout mLoggedOutGroup;
    private TextView mNameLabel;
    private EditText mEmailField;
    private EditText mPasswordField;

    private int length = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoggedInGroup = findViewById(R.id.logged_in_group);
        mLoggedOutGroup = findViewById(R.id.logged_out_group);
        mNameLabel = findViewById(R.id.welcome);
        mEmailField = findViewById(R.id.enter_email);
        mPasswordField = findViewById(R.id.enter_password);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            mLoggedOutGroup.setVisibility(View.GONE);
            mLoggedInGroup.setVisibility(View.VISIBLE);
            mNameLabel.setText(String.format(getResources().getString(R.string.welcome), currentUser.getEmail()));
        } else {
            mLoggedInGroup.setVisibility(View.GONE);
            mLoggedOutGroup.setVisibility(View.VISIBLE);
        }
    }

    public void signOut(View view) {
        mAuth.signOut();
        updateUI(null);
    }

    public void launchCheckListActivity(View view) {
        startActivity(new Intent(this, CheckListActivity.class)
                .putExtra(CheckListActivity.CURRENT_USER, mAuth.getCurrentUser().getUid()));
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }


    public void signIn(View view) {
        if (!validateForm()) {
            return;
        }

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        updateUI(mAuth.getCurrentUser());
                    } else {
                        Exception e = task.getException();
                        Log.w(TAG, "signInWithEmail:failure", e);
                        Toast.makeText(MainActivity.this, "Login failed: " + e.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    public void createAccount(View view) {
        if (!validateForm()) {
            return;
        }

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        DocumentReference documentReference = mDb.collection("users").document(mAuth.getCurrentUser().getUid());
                        Map<String, Object> user = new HashMap<>();
                        List<String> seen = Arrays.asList();
                        user.put("seen", seen);
                        documentReference.set(user).addOnCompleteListener(t -> Log.d(TAG, "successfully linked to database"));
                        updateUI(mAuth.getCurrentUser());
                    } else {
                        Exception e = task.getException();
                        Log.w(TAG, "createUserWithEmail:failure", e);
                        Toast.makeText(MainActivity.this, "Registration failed: " + e.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }
}