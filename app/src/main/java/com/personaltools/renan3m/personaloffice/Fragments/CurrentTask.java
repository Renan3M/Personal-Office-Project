package com.personaltools.renan3m.personaloffice.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.personaltools.renan3m.personaloffice.Activities.DailyTask;
import com.personaltools.renan3m.personaloffice.Activities.Historical;
import com.personaltools.renan3m.personaloffice.Activities.MainActivity;
import com.personaltools.renan3m.personaloffice.Notification.MyNotificationService;
import com.personaltools.renan3m.personaloffice.R;
import com.personaltools.renan3m.personaloffice.Widgets.PomtimeWidget;

import java.lang.reflect.Method;
import java.security.Permission;
import java.util.ArrayList;

import static android.os.PowerManager.SCREEN_DIM_WAKE_LOCK;
import static java.lang.Thread.MIN_PRIORITY;
import static java.lang.Thread.sleep;

public class CurrentTask extends Fragment {

  //  public static final int TIMER_RUNTIME = 1500000; // 25 min

    public static final int TIMER_RUNTIME = 10000; // 1 min


    private static final int UPDATE_TIMER = 0;
    private static final int RESET_TIMER = 1;
    private static final int RESET_BTN = 2;
    private final String TAG = "CurrentTask";

    public static int taskCount;


    private OnTaskInteraction mListener;
    private TextView taskName;
    private TextView txtTime;

    private Button btnRestart;

    private PomtimeWidget pomtimeWidget;
    private Intent intent;

    private boolean mActivity;
    private boolean btnReseted = false;

    private Thread timerThread;
    private int timePassedPlus = 0;

    private Handler uiHandler;

    private View view;

    private ArrayList<DailyTask.IndividualTask> list;

    public static boolean stopService;
    private boolean startService;
    private int timeService;

    public CurrentTask() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (timeService == 0){ taskCount = 0;}

        view = inflater.inflate(R.layout.fragment_current_task, container, false);
        // Inflate the layout for this fragment

        Log.e(TAG,"Notification created this again, idk why...");

        taskName = view.findViewById(R.id.current_task_name_txt);
        taskName.setText(list.get(0).getNameTask());

        intent = new Intent(getActivity(),MyNotificationService.class);

        txtTime = view.findViewById(R.id.time_txt_plzfindme);

        pomtimeWidget = view.findViewById(R.id.pomtime_widget);

        btnRestart = view.findViewById(R.id.restart_btn);

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                restartTimer(v);
            }
        });


        startService = false;


        uiHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_TIMER:
                        try {
                            int time = msg.getData().getInt("int");

                            int timeS = time;

                            if (time > 60000) {
                                timeS = time % 60000;
                            }

                            timePassedPlus = 60000 - timeS;

                            txtTime.setText("(" + String.valueOf((TIMER_RUNTIME - time) / 60000 + " : "
                                    + timePassedPlus / 1000 + ")"));

                            if (startService && getActivity() != null){

                                intent.putExtra("time", time);
                                getActivity().startService(intent);

                                startService = false;
                                
                            }


                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        break;

                    case RESET_TIMER:
                        txtTime.setText(String.valueOf("(" + TIMER_RUNTIME / 60000 + " : " + 00 + ")"));
                        if (list.size() != 0) taskName.setText(list.get(0).getNameTask());
                        break;

                    case RESET_BTN:

                        btnRestart.setText("START");
                        break;
                }
            }
        };

        timerThread = new Thread() { // A thread tem acesso aos métodos da classe, todavía péssima pratica! (referencia)
            // Um Assync task ia substituir muito bem a função dessa thread, atualizando a UI sem q eu precise me preocupar com handler.
            @Override
            public void run() {
                synchronized (this) {
                    mActivity = true;
                    int waited = timeService;


                    try {
                        while (mActivity && (waited < TIMER_RUNTIME)) {
                            sleep(1000);
                            if (mActivity) {
                                waited += 1000;

                                Message message = new Message();
                                Bundle b = new Bundle();
                                b.putInt("int", waited);
                                message.setData(b);
                                message.what = UPDATE_TIMER;
                                uiHandler.sendMessage(message);

                                updateProgressBar(waited);
                            }
                        }
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    } finally {
                        timeService = 0;
                        resetProgress();
                        onContinue(waited);
                    }
                }
            }
        };
        timerThread.setPriority(Thread.MIN_PRIORITY);

        if (!list.isEmpty()) timerThread.start();

    //    mPowerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);

        int flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

        getActivity().getWindow().addFlags(flags); // this is how your app will wake up the screen you will call this activity later

        return view;
    }

/* Tried to make an turn off mechanism, failed at trying this, wouldn't turn off the screen.

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    public void turnOnScreen(){
        // turn on screen

        //  mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag");  QUE INCRIVEL, VEJA!!
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();
    }


    public void turnOffScreen(){
        // "Turn off screen"

            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lp.dimAmount= 1;
            getActivity().getWindow().setAttributes(lp);


    }
*/

    private void onContinue(int waited) {

        btnReseted = true; // Da permissão para iniciar a proxima thread e mudar o texto no botão p/ STOP

        if (waited != TIMER_RUNTIME) return; // Thread interrompida


        list.get(0).decreasePom();
        Log.e(TAG, "decrementado");

        Message message = new Message();
        message.what = RESET_BTN;
        uiHandler.sendMessage(message);

        if (list.get(0).getNumDePom() == 0) {
            list.remove(0);  // list é decrementada, quem era index 1 agora é index 0.

            taskCount++;
            Log.e(TAG, "removido");

            if (mListener != null) {
                Message msg = new Message();
                msg.what = MainActivity.HANDLER_UPDATE_TASK_LIST;
                ((MainActivity) mListener).mHandler.sendMessage(msg);
            }

        }

        if (mListener != null) {
            Message msg = new Message();
            msg.what = MainActivity.HANDLER_UPDATE_POMODOR_LIST;
            ((MainActivity) mListener).mHandler.sendMessage(msg);
        }

        // Aqui vou apenas salvar no sharedPreferences para lá acessar.
        if (list.isEmpty()) {

            DailyTask.setListToSharedSet(getActivity(),MainActivity.LIST_OF_LISTS_TAG); // Uma vez que a lista de tarefas acabou
            DailyTask.setListToShared(MainActivity.LIST_TAG,new ArrayList<>()); // Reseta o sharedList
        }
    }



    private void updateProgressBar(final int timePassed) {
        if (null != pomtimeWidget) {
            final int progress = pomtimeWidget.getMax() * timePassed / TIMER_RUNTIME;
            pomtimeWidget.setProgress(progress);

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopService = false;
        startService = true;
        Log.e(TAG,"service ready to start, waiting for thread to pass the current time count");
    }

    @Override
    public void onStart() {
        super.onStart();


    //    if (mPowerManager == null) mPowerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);

        if (timeService != 0 && txtTime != null) txtTime.setText("(" + String.valueOf((TIMER_RUNTIME - timeService)
                / 60000 + " : " + timePassedPlus / 1000 + ")"));

        stopService = true; // <-- Flag usada pelo serviço

        // Parando a notificação
        Intent intent = new Intent(getActivity(),MyNotificationService.class);
        getActivity().stopService(intent);
    }

    private void resetProgress() {
        pomtimeWidget.setProgress(0);

        Message message = new Message();
        message.what = RESET_TIMER;
        uiHandler.sendMessage(message);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskInteraction) {
            mListener = (MainActivity) context;

            list = ((MainActivity) mListener).getList();

            timeService = ((MainActivity) mListener).getTimeService();

            if (list == null){Log.e(TAG,"ERROR, LISTA É NULA!");}
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteraction listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void restartTimer(View view) {
        //  timerThread.start(); // não da pra re-usar threads, tem q instanciar dnv!

        if (list.isEmpty()) return;

        mActivity = false;

        if (btnReseted ){
            Thread thread = new Thread(timerThread);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();

            ((Button) view).setText("STOP");

            btnReseted = false;
            return;
        }

        ((Button) view).setText("START");
    }
}
