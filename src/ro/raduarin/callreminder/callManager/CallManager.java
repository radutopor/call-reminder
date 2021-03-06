package ro.raduarin.callreminder.callManager;

import java.util.ArrayList;
import java.util.Calendar;

import ro.raduarin.callreminder.activities.NotificationActivity;
import ro.raduarin.callreminder.activities.NotificationCallEndActivity;
import ro.raduarin.callreminder.contactsQuery.Contact;
import ro.raduarin.callreminder.contactsQuery.ContactsQuery;
import ro.raduarin.callreminder.dao.Dao;
import ro.raduarin.callreminder.dao.Reminder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class CallManager
{
	private static Intent notificationIntent;
	private static boolean releventCallReceived; 
	
	public static void onNewCall(final Context context, String phoneNumber)
	{
		final Contact contact = new ContactsQuery(context).getContactByPhoneNo(phoneNumber);
		
		final ArrayList<Reminder> releventReminders = new ArrayList<Reminder>();
		Calendar now = Calendar.getInstance();
		for (Reminder reminder : new Dao(context).getAllReminders())
			if (reminder.isEnabled && (reminder.startDate==null||reminder.startDate.compareTo(now)<0) && reminder.contactIds.contains(contact.id))
				releventReminders.add(reminder);
		
		notificationIntent = null;
		if (!releventReminders.isEmpty())
			new Handler().postDelayed(new Runnable(){ public void run()
			{
				notificationIntent = new Intent(context, NotificationActivity.class)
					.putExtra(NotificationActivity.KEY_CONTACT, contact)
					.putExtra(NotificationActivity.KEY_REMINDERS, releventReminders)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				context.startActivity(notificationIntent);				
			}}, 100);
	}
	
	public static void onCallReceived()
	{
		releventCallReceived = notificationIntent != null;
	}
	
	public static void onCallEnd(final Context context)
	{
		if (releventCallReceived)
			new Handler().postDelayed(new Runnable(){ public void run() {
				context.startActivity(notificationIntent.setClass(context, NotificationCallEndActivity.class));
			}}, 100);
		releventCallReceived = false;
	}
}