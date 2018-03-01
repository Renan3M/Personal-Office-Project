
package com.personaltools.renan3m.personaloffice.Activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.personaltools.renan3m.personaloffice.Entity.User;
import com.personaltools.renan3m.personaloffice.R;

public class Registration extends AppCompatActivity {

    // Firebase stuff
    private static final String TAG = "Registration activity";
    private FirebaseAuth mAuth;
    private User user;
    private DatabaseReference mDatabase;
    private String email;
    private String password;

    // UI stuff
    private EditText _nameText;
    private EditText _emailText;
    private EditText _passwordText;
    private Button _signupButton;
    private TextView _loginLink;

    private boolean loginWithSucess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // UI Injections
        _nameText = findViewById(R.id.input_name);
        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    private void register() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);
        // ProgressDialog != ProgressBar ... Atenção

        final ProgressDialog progressDialog =
                new ProgressDialog(Registration.this, R.style.Theme_AppCompat_DayNight_Dialog);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (loginWithSucess) {
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                                Toast.makeText(Registration.this,
                                                        "Verification email sent to "+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Toast.LENGTH_LONG).show();
                                            else Toast.makeText(Registration.this,
                                                    "Failed to send verification email",Toast.LENGTH_SHORT).show();


                                        }
                                    });
                            try {
                                wait(1500);
                            }catch (Exception e){}

                            onSignupSuccess();
                        } else
                            onSignupFailed();

                        progressDialog.dismiss();
                    }
                }, 3000);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        createUser(name, email, password);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Email already exists", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private void createUser(final String name, String email, String password) {
        // TODO: register the new account here.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        try {
                            user = new User(task.getResult().getUser().getDisplayName(),
                                    task.getResult().getUser().getEmail());

                            user.setName(name);

                            mDatabase.child(task.getResult().getUser().getUid()).setValue(user);

                            loginWithSucess = true;

                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage().toString());
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // Cry
                            loginWithSucess = false;
                        }

                        // ...
                    }
                });
    }
}
