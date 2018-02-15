package com.personaltools.renan3m.personaloffice.Fragments;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.personaltools.renan3m.personaloffice.Activities.DailyTask;
import com.personaltools.renan3m.personaloffice.Activities.MainActivity;
import com.personaltools.renan3m.personaloffice.Notification.MyNotificationService;
import com.personaltools.renan3m.personaloffice.R;
import com.personaltools.renan3m.personaloffice.Widgets.PomtimeWidget;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class CurrentTask extends Fragment {

    //protected static final int TIMER_RUNTIME = 1500000; // 25 min

    public static final int TIMER_RUNTIME = 60000;  // (debug)
    private static final int UPDATE_TIMER = 0;
    private static final int RESET_TIMER = 1;
    private static final int RESET_BTN = 2;
    private final String TAG = "CurrentTask";

    public static int taskCount = 0;

    private OnTaskInteraction mListener;
    private TextView taskName;
    private TextView txtTime;

    private Button btnRestart;
    private Button btnTurnOff;

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

        taskCount = 0;

        view = inflater.inflate(R.layout.fragment_current_task, container, false);
        // Inflate the layout for this fragment

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

        btnTurnOff = view.findViewById(R.id.turn_off_screen_btn);

        startService = false;



        btnTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



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
        timerThread.start();

        return view;
    }

    private void onContinue(int waited) {

        btnReseted = true; // Da permissão para iniciar a proxima thread e mudar o texto no botão p/ STOP

        if (waited != TIMER_RUNTIME) return; // Thread interrompida


        list.get(0).decreasePom();
        Log.e(TAG, "decrementado");

        Message message = new Message();
        message.what = RESET_BTN;
        uiHandler.sendMessage(message);

        if (list.get(0).getNumDePom() == 0) {
            list.remove(0);

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
            // A cofiguração da activity foi alterada, a thread antiga já não tem mais acesso à MainActivity que a iniciou originalmente (esta foi reiniciada / destruida);
            // Isso é pessima prática, manter o serviço rodando quando já nem tem mais atividade para ele enviar resposta. (Memória)

            // PRECISO DAR UMA ARRUMADA NISSO !!!  (Só estou escrevendo o comentário aqui porque foi aqui que estorou o erro)
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

        stopService = true;

        Intent intent = new Intent(getActivity(),MyNotificationService.class);
        getActivity().stopService(intent); // Ta dando erro no logCat, da uma olhada!
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
        //  timerThread.start(); // não da pra re-usar threads mané, tem q instanciar dnv!

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
