package com.guvery.notifyme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Aaron on 10/27/2014.
 */
public class Notifier {
    private int mId;
    private NotificationManager mNotifManager;
    private SharedPreferences mPrefs;
    private Context mContext;

    public Notifier(Context context) {
        mNotifManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mId = mPrefs.getInt("id", 0);
        mContext = context;
    }

    public boolean notifyMe(Notif n) {
        mId++; // Increment the ID for this notification
        mPrefs.edit().putInt("id", mId).commit();
        n.setId(mId);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle(n.getTitle())
                .setContentText(n.getBody())
                .setSmallIcon(n.getImageId())
                .setOngoing(n.isOngoing())
                //.setContentInfo()
                .setTicker(n.getTitle())
                .setPriority(n.getPriority());

        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        resultIntent.putExtra("com.guvery.notifyme.isNotification", true);
        resultIntent.putExtra("com.guvery.notifyme.Id", mId);
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        //stackBuilder.addParentStack(MyActivity.class);
        //stackBuilder.addNextIntent(resultIntent);

        //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
        //0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, mId,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        if (n.isBigTextStyle()) {
            String list = howManyItems(n.getBody()) +
                    (howManyItems(n.getBody()) == 1 ? " item" : " items");
            mBuilder.setContentText(n.getBody());
            NotificationCompat.BigTextStyle notification =
                    new NotificationCompat.BigTextStyle(mBuilder);
            notification.bigText(n.getBody());
            notification.setSummaryText(list);
            mNotifManager.notify(mId, notification.build());
        } else {
            mNotifManager.notify(mId, mBuilder.build());
        }

        return true;
    }

    public void notifyFromHistory(Notif n) {
        // Save current id
        int tmpId = mId;

        // Set the id back to what the notification was, minus
        // one to account for notifying incrementing the id
        mId = n.getId() - 1;

        // notify
        notifyMe(n);

        // restore old id
        mId = tmpId;
    }

    private int howManyItems(String body) {
        if (body.length() == 0) return 0;
        int count = 0;
        for (int i = 0; i < body.length() - 1; i++)
            if (body.charAt(i) == '\n')
                count++;
        return count + 1;
    }

    public void clearNotif(int mId) {
        mNotifManager.cancel(mId);
    }

    public void clearNotifs() {
        mNotifManager.cancelAll();
    }
}
