package com.personaltools.renan3m.personaloffice.Activities.Authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.personaltools.renan3m.personaloffice.Fragments.CurrentTask;
import com.personaltools.renan3m.personaloffice.R;

public class Configuration extends AppCompatActivity {

    public static final String CONFIG_SHARED = "CONFIGURATION_SHARED_NAME";
    public static final String SWITCH_STATE_LOGIN = "LAST_STATE_OF_SWITCH_LOGIN";
    public static final String SWITCH_STATE_SCREEN = "LAST_STATE_OF_SWITCH_SCREEN";
    public static final String SWITCH_STATE_SOUND = "LAST_STATE_OF_SWITCH_SOUND";
    private static final String TAG = "Configuration";

    // Layout stuff
    ImageButton btnPassword;
    EditText txtPassword;

    Switch stcShared; // Login
    Switch stcScreen; // Keep the screen ON, (keep the wakelock active, it is not the same as I was trying to do before, witch was to shut the screen OFF and ON as I pleased).
    Switch stcSound; // Turn the sound on and off (Also need to create sound for task finished =D)

    // SharedPrefs stuff
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        // SharedPrefs stuff
        sharedPreferences = getSharedPreferences(CONFIG_SHARED,0);
        editor = sharedPreferences.edit();

        // Layout stuff
        btnPassword = findViewById(R.id.btn_redefine_password);
        txtPassword = findViewById(R.id.txt_redefine_password);

        stcShared = findViewById(R.id.switch_prefs_user);
        stcScreen = findViewById(R.id.switch_screen_lock);
        stcSound = findViewById(R.id.switch_sound_enable);

        stcShared.setChecked(sharedPreferences.getBoolean(SWITCH_STATE_LOGIN,false));
        stcScreen.setChecked(sharedPreferences.getBoolean(SWITCH_STATE_SCREEN,false));
        stcSound.setChecked(sharedPreferences.getBoolean(SWITCH_STATE_SOUND,false));

        // Defining listeners
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null){ // Case user not logged
                    Toast.makeText(Configuration.this,"Please log first",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (txtPassword.getText().toString().isEmpty() || txtPassword.getText().length() < 4
                        || txtPassword.getText().length() > 10) {
                    Toast.makeText(Configuration.this,"Entre 4 e 10 caracteres",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().getCurrentUser().updatePassword(txtPassword.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Configuration.this,"Senha atualizada com sucesso",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(Configuration.this,"Erro ao atualizar a senha",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                FirebaseAuth.getInstance().getCurrentUser().reload();

                if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.e(TAG, "Submited with sucess");
                            } else Log.e(TAG, "Failed to submite");  // Tá caindo aqui ... QUE ESTRANHOOOOOOOO *-*
                        }
                    });
                }
                else Log.e(TAG,"Email is already verified");
            }
        });

        stcShared.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    editor.putBoolean(SWITCH_STATE_LOGIN, isChecked);
                    editor.commit();
            }
        });

        stcScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                editor.putBoolean(SWITCH_STATE_SCREEN, isChecked);
                editor.commit();
            }
        });

        stcSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                editor.putBoolean(SWITCH_STATE_SOUND, isChecked);
                editor.commit();
            }
        });
    }

    public void logUserOut(View view) {

        CurrentTask.stopService = true;

        if (FirebaseAuth.getInstance().getCurrentUser() == null){ // Case user not logged
            Toast.makeText(Configuration.this,"Please log first",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent loginIntent = new Intent(Configuration.this,LoginActivity.class);
        loginIntent.putExtra("oldUserEmail",FirebaseAuth.getInstance().getCurrentUser().getEmail());

        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut(); // Only changes de state

        startActivity(loginIntent);
        finish();
    }
}
