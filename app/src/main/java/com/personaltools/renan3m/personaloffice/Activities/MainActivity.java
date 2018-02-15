package com.personaltools.renan3m.personaloffice.Activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.personaltools.renan3m.personaloffice.Fragments.*;
import com.personaltools.renan3m.personaloffice.R;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnTaskInteraction {

    private static final String TAG = "MainClass";

    public static final String PREFS_NAME = "Preferences";

    public static final int HANDLER_UPDATE_POMODOR_LIST = 0;
    public static final int HANDLER_UPDATE_TASK_LIST = 1;
    public static final String CURRENT_TASK_FLAG = "Current_task_flag";
    public static final String LIST_TAG = "list";


    private BlankTask blankTask;
    private CurrentTask currentTask;
    private WaitingTask waitingTask;

    private ListView listOfTasks;
    private ListView listOfPomodors;

    private ArrayList<DailyTask.IndividualTask> list;
    private ArrayAdapter arrayAdapter;

    private MyCustomAdapter mAdapter;

    public Handler mHandler;

    private int oldCount = CurrentTask.taskCount;

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"STAAAARTED");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG,"CREATED");

        blankTask = new BlankTask();
        currentTask = new CurrentTask();
        waitingTask = new WaitingTask();

        listOfTasks = findViewById(R.id.lista_de_tarefas_do_dia);
        listOfPomodors = findViewById(R.id.lista_de_pomodoros);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case HANDLER_UPDATE_POMODOR_LIST:

                        refreshPomodorsList();
                        break;

                    case HANDLER_UPDATE_TASK_LIST:

                        arrayAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };


        if (getIntent().hasExtra(CURRENT_TASK_FLAG)) {
            if (getIntent().getExtras().getInt(CURRENT_TASK_FLAG) != 0)
            {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotificationManager.cancel(1);

                Log.e(TAG,"O tempo passado é " + String.valueOf(getIntent().getExtras().getInt(CURRENT_TASK_FLAG,0)));
            }

            list = DailyTask.getListFromShared(getApplicationContext(),LIST_TAG);

            if (list == null){ Log.e(TAG,"Lista não sendo recuperada"); }

            getSupportFragmentManager().beginTransaction().replace(R.id.tarefa, currentTask).commit();

            ArrayList<String> valuesT = new ArrayList();

            for (int i = 0; i < list.size(); i++) {
                valuesT.add("Tarefa " + (i + 1) + "\n(" + String.valueOf((list.get(i)).getNumDePom()) + ")");
            }

            listOfTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                    Toast.makeText(getApplicationContext(), list.get(i).getNameTask(), Toast.LENGTH_SHORT).show();
                }
            });


            arrayAdapter = new ArrayAdapter<String>(this, R.layout.main_list_tasks, valuesT){

                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);


                    if (position == oldCount){
                        textView.setTextColor(Color.GRAY);
                        oldCount = CurrentTask.taskCount;
                    }


                    if (position == CurrentTask.taskCount) {
                        textView.setTextColor(Color.YELLOW);
                    }

                    return textView;
                }
            };

            listOfTasks.setAdapter(arrayAdapter);

            mAdapter = new MyCustomAdapter();

            for (int i = 0; i < list.get(0).getNumDePom() - 1; i++) {
                mAdapter.addItem("item " + i);
            }

            listOfPomodors.setAdapter(mAdapter);



        } else getSupportFragmentManager().beginTransaction().add(R.id.tarefa, blankTask).commit();

    }


    public void refreshPomodorsList(){
        mAdapter.clear();

        try{
        for (int i = 0; i < list.get(0).getNumDePom() - 1; i++) {
            mAdapter.addItem("item " + i);
        }} catch (Exception ie){
            // Nothing to worry about
        }
    }

    private class MyCustomAdapter extends BaseAdapter {

        private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomAdapter (){
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public void clear(){
            mData.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
            convertView = mInflater.inflate(R.layout.main_list_pom, null);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.pomodor);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

            return convertView;
    }

}

public static class ViewHolder {
    public ImageView imageView;
}



    public void intentTasks(View view) {
        startActivity(new Intent(this, DailyTask.class));
    }

    public void intentConfig(View view) {
    }
    public void intentSocial(View view) {
    }

    public ArrayList<DailyTask.IndividualTask> getList(){
        return list;
    }
}
