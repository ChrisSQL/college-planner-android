package com.chris.collegeplanner.reminders;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.chris.collegeplanner.R;
import com.chris.collegeplanner.app.SummaryActivity;

/**
 * Created by Chris on 16/02/2015.
 */
public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub

        String title = intent.getStringExtra("Title");
        String subject = intent.getStringExtra("Subject");


        // Setup Notification Details.

        NotificationManager mNM;
        mNM = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, intent.getStringExtra("Title")+" Due Soon",
                System.currentTimeMillis());
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, SummaryActivity.class), 0);
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(context, context.getText(R.string.title_section1), subject + " - " + title+" : Due in 2 days.", contentIntent);
        // Send the notification.
        // We use a layout id because it is a unique number. We use it later to cancel.
        mNM.notify(R.string.title_section2, notification);


        // Setup SMS Details.
        String phoneNumberReciever="0863066702";
        String message = subject + " - " + title+" : Due in 2 days.";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumberReciever, null, message, null, null);

        Toast.makeText(context, "SMS Reminder Sent", Toast.LENGTH_LONG).show();
    }

}
