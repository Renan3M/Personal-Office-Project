package com.personaltools.renan3m.personaloffice.Activities;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.personaltools.renan3m.personaloffice.R;

public class Configuration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
    }

    public void logUserOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Configuration.this,LoginActivity.class));
        finish();
    }
}
