package com.personaltools.renan3m.personaloffice.Activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.personaltools.renan3m.personaloffice.Activities.RecyclerView.RankAdapter;
import com.personaltools.renan3m.personaloffice.R;

public class Ranks extends AppCompatActivity {

    // Essa activity vai servir de justificativa para o login no firebase xD

    private int currentPom;

    RecyclerView recyclerView;
    RankAdapter adapter;

    SharedPreferences sharedPreferences;

    public static final String RANKS_SHARED = "POMODORS_FOR_RANK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ranks);

        adapter = new RankAdapter(this);

        recyclerView = findViewById(R.id.recycler_view_ranks);

        if (recyclerView == null) return;

        // Vertical recycler view.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        sharedPreferences = getSharedPreferences(RANKS_SHARED, 0);

        currentPom = sharedPreferences.getInt("currentPom",0);

        rankCheck(currentPom);

        adapter.notifyDataSetChanged(); // talvez isso n seja necessario se eu fizer o rankCheck antes de setar o adapter
    }

    public void rankCheck(int currentPom) {
        switch (currentPom) { // I love switch.
            case 0:
                adapter.setPosition(0);
                break;
            case 10:
                adapter.setPosition(1);
                break;
            case 50:
                adapter.setPosition(2);
                break;
            case 75:
                adapter.setPosition(3);
                break;
            case 100:
                adapter.setPosition(4);
                break;
            case 150:
                adapter.setPosition(5);
                break;
            case 200:
                adapter.setPosition(6);
                break;
            case 275:
                adapter.setPosition(7);
                break;
            case 350:
                adapter.setPosition(8);
                break;
            case 450:
                adapter.setPosition(9);
                break;
            case 550:
                adapter.setPosition(10);
                break;
            case 700:
                adapter.setPosition(11);
                break;
            case 850:
                adapter.setPosition(12);
                break;
            case 950:
                adapter.setPosition(13);
                break;
            case 1000:
                adapter.setPosition(14);
                break;
        }
    }
}
