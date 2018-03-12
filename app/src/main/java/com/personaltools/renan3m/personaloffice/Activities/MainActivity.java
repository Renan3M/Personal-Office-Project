package com.personaltools.renan3m.personaloffice.Activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.personaltools.renan3m.personaloffice.Activities.Authentication.Configuration;
import com.personaltools.renan3m.personaloffice.Fragments.*;
import com.personaltools.renan3m.personaloffice.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnTaskInteraction {

    private static final String TAG = "MainClass";

    public static final String PREFS_NAME = "Preferences";

    public static final int HANDLER_UPDATE_POMODOR_LIST = 0;
    public static final int HANDLER_UPDATE_TASK_LIST = 1;

    public static final String CURRENT_TASK_FLAG = "Current_task_flag";
    public static final String LIST_TAG = "list";
    public static final String LIST_OF_LISTS_TAG = "list_of_lists"; // So ugly, Jesus..


    private BlankTask blankTask;
    private CurrentTask currentTask;

    private ListView listOfTasks;
    private ListView listOfPomodors;

    private ArrayList<DailyTask.IndividualTask> list;
    private ArrayAdapter arrayAdapter;

    private MyCustomAdapter mAdapter;

    private SharedPreferences sharedPreferences;

    public static Handler mHandler;

    private int oldCount;
    private int timeService;


    @Override
    protected void onStart() {
        super.onStart();

        // Deveria fazer essa verificação somente caso a activity que tenha dado start aqui tenha sido a Configuration. (memória)
        if (sharedPreferences.getBoolean(Configuration.SWITCH_STATE_SCREEN, false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){finish(); return;}

        setContentView(R.layout.activity_main);

        /*android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.drawable.ship_icon);
        menu.setDisplayUseLogoEnabled(true);*/

        SharedPreferences sP = getApplicationContext().getSharedPreferences(MainActivity.PREFS_NAME, 0);

        Log.e(TAG,"create method called");
        sharedPreferences = getSharedPreferences(Configuration.CONFIG_SHARED, 0);


        blankTask = new BlankTask();
        currentTask = new CurrentTask();

        listOfTasks = findViewById(R.id.lista_de_tarefas_do_dia);
        listOfPomodors = findViewById(R.id.lista_de_pomodoros);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_UPDATE_POMODOR_LIST:

                        refreshPomodorsList();
                        break;

                    case HANDLER_UPDATE_TASK_LIST:

                        arrayAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };

        if (DailyTask.getListFromShared(getApplicationContext(), LIST_TAG) == null ||
                DailyTask.getListFromShared(getApplicationContext(), LIST_TAG).isEmpty()) {

            getSupportFragmentManager().beginTransaction().add(R.id.tarefa, blankTask).commit();

            return;

        } else if (getIntent().hasExtra(CURRENT_TASK_FLAG) || sP.contains(LIST_TAG)) {
            CurrentTask.taskCount = 0;

            if (getIntent().getExtras().getInt(CURRENT_TASK_FLAG) != 0) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotificationManager.cancel(1);

                timeService = getIntent().getExtras().getInt(CURRENT_TASK_FLAG, 0);

            }

            list = DailyTask.getListFromShared(getApplicationContext(), LIST_TAG);
            // O Reset na list só acontence quando vc sobrescreve ela no dailyTask com uma nova lista, mas smp q iniciar essa atividade
            // ele vai pegar o ultimo valor do sharedPreferences.

            if (list == null) {
                Log.e(TAG, "Lista não sendo recuperada");
            }


            ArrayList<String> valuesT = new ArrayList();
            try {
            for (int i = 0; i < list.size(); i++) {
                valuesT.add("Tarefa " + (i + 1) + "\n(" +list.get(i).getNumDePom() +")");
            }}catch (IndexOutOfBoundsException e){e.getLocalizedMessage();}

            listOfTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                    // Como atualizar a lista de tasks sem remove-las que nem com a lista de pomodoros?
                    // Pois eu não estou dando refresh nessa sub-lista justamente para evitar remoção de tarefas da listview.

                    Toast.makeText(getApplicationContext(), list.get(i - CurrentTask.taskCount).getNameTask(), Toast.LENGTH_SHORT).show();
                }
            });


            oldCount = CurrentTask.taskCount;
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.main_list_tasks, valuesT) { // valuesT n muda

                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);


                    if (position == oldCount) {
                        textView.setTextColor(Color.GRAY);
                        oldCount = CurrentTask.taskCount;
                    }


                    if (position == CurrentTask.taskCount) {
                        textView.setTextColor(Color.YELLOW);
                    }

                    if (list.isEmpty()) CurrentTask.taskCount = 0;

                    return textView;
                }
            };

            listOfTasks.setAdapter(arrayAdapter);

            mAdapter = new MyCustomAdapter();

            try{
            for (int i = 0; i < list.get(0).getNumDePom() - 1; i++) {
                mAdapter.addItem("item " + i);
            }} catch (IndexOutOfBoundsException e){e.getLocalizedMessage();}

            listOfPomodors.setAdapter(mAdapter);


            getSupportFragmentManager().beginTransaction().replace(R.id.tarefa, currentTask).commit();


        } else getSupportFragmentManager().beginTransaction().add(R.id.tarefa, blankTask).commit();

    }


    public void refreshPomodorsList() {
        mAdapter.clear();
                                                         // could use a counter and add to the onItemClick of listOfTasks for each
                                                         // refresh, this way I simulate a refresh to the tasks, not needing to
        try {                                            // change their values (valuesT). Or can get the taskCount from current
            for (int i = 0; i < list.get(0).getNumDePom() - 1; i++) {
                mAdapter.addItem("item " + i);
            }
        } catch (Exception ie) {
            // Nothing to worry about
        }
    }

    public int getTimeService() {
        return timeService;
    }


    private class MyCustomAdapter extends BaseAdapter {

        private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;

        MyCustomAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        void clear() {
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
                holder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }

    }

    static class ViewHolder {
        public ImageView imageView;
    }

    public void intentTasks(View view) {
        startActivity(new Intent(this, DailyTask.class));
    }

    public void intentConfig(View view) { startActivity(new Intent(this, Configuration.class)); }

    public void intentHist(View view) {
        if (DailyTask.getListsFromSharedSet(LIST_OF_LISTS_TAG) == null ||
                DailyTask.getListsFromSharedSet(LIST_OF_LISTS_TAG).isEmpty()){ return; }

                Intent intent = new Intent(this, Historical.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
    }

    public ArrayList<DailyTask.IndividualTask> getList() {
        return list;
    }



    @Override
    public void onBackPressed() {
        // Does not finish the activity, but simply move it to the back. (not always work, should find out why)
        moveTaskToBack(true);
    }
}