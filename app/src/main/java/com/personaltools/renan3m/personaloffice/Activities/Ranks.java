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
    final int vector[]={0,10,50,75,100,150,200,275,350,450,550,700,850,950,1000};

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
                adapter.setData(0, currentPom, vector);
                break;
            case 10:
                adapter.setData(1, currentPom, vector);
                break;
            case 50:
                adapter.setData(2, currentPom, vector);
                break;
            case 75:
                adapter.setData(3, currentPom, vector);
                break;
            case 100:
                adapter.setData(4, currentPom, vector);
                break;
            case 150:
                adapter.setData(5, currentPom, vector);
                break;
            case 200:
                adapter.setData(6, currentPom, vector);
                break;
            case 275:
                adapter.setData(7, currentPom, vector);
                break;
            case 350:
                adapter.setData(8, currentPom, vector);
                break;
            case 450:
                adapter.setData(9, currentPom, vector);
                break;
            case 550:
                adapter.setData(10, currentPom, vector);
                break;
            case 700:
                adapter.setData(11, currentPom, vector);
                break;
            case 850:
                adapter.setData(12, currentPom, vector);
                break;
            case 950:
                adapter.setData(13, currentPom, vector);
                break;
            case 1000:
                adapter.setData(14, currentPom, vector);
                break;
        }
    }
}
