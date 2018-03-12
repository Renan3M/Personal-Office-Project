package com.personaltools.renan3m.personaloffice.Notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.personaltools.renan3m.personaloffice.Activities.MainActivity;
import com.personaltools.renan3m.personaloffice.Fragments.CurrentTask;
import com.personaltools.renan3m.personaloffice.R;

import static com.personaltools.renan3m.personaloffice.Fragments.CurrentTask.TIMER_RUNTIME;

public class MyNotificationService extends IntentService {

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;
    private int timePassedPlus = 0;

    public MyNotificationService() {
        super("MyNotificationService");

    }


    @Override
    protected void onHandleIntent(Intent intent) {

        synchronized (this) {

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            int time = intent.getIntExtra("time",0);

            Intent mIntent = new Intent(this, MainActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.apple_off)
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentTitle("Pomodoro time left...");


            while (time < TIMER_RUNTIME && !CurrentTask.stopService) {
                try {
                    this.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                time += 1000;

                int timeS = time;
                if (time > 60000) {
                    timeS = time % 60000;
                }

                timePassedPlus = 60000 - timeS;

                builder.setContentText(String.valueOf((TIMER_RUNTIME - time) / 60000 + " : "
                        + timePassedPlus / 1000 ));

                mIntent.putExtra(MainActivity.CURRENT_TASK_FLAG,time);
                mIntent.setAction("foo");

                builder.setContentIntent(
                        PendingIntent.getActivity(this, 0, mIntent,PendingIntent.FLAG_UPDATE_CURRENT));


                mNotificationManager.notify(1, builder.build());

            }

            if (!CurrentTask.stopService) {
                builder.setContentTitle("Pomodoro finished").setContentText("Go back to work!");

                mNotificationManager.notify(1, builder.build());
            } else mNotificationManager.cancel(1);
        }
    }
}
