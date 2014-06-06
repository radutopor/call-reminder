package ro.raduarin.callreminder.activities;

import ro.raduarin.callreminder.R;
import ro.raduarin.callreminder.dao.Dao;
import ro.raduarin.callreminder.dao.Reminder;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class NotificationCallEndActivity extends NotificationActivity
{
	private Dao dao;
	private View btn_done, lay_single_reminder_options, btn_delete_single_reminder, btn_edit_single_reminder, btn_keep_single_reminder;
	
    protected void linkUI()
    {
    	super.linkUI();
		dao = new Dao(NotificationCallEndActivity.this);
    	btn_done = findViewById(R.id.btn_done);
    	lay_single_reminder_options = (ViewGroup) findViewById(R.id.lay_single_reminder_options);
    	btn_delete_single_reminder = findViewById(R.id.btn_delete_single_reminder);
    	btn_edit_single_reminder = findViewById(R.id.btn_edit_single_reminder);
    	btn_keep_single_reminder = findViewById(R.id.btn_keep_single_reminder);
    }
    
    protected void populateUI()
    {
    	lay_reminders.removeAllViews();
    	btn_done.setVisibility(View.GONE);
    	super.populateUI();
    	txt_notification_message.setText(R.string.notification_message_call_end);
    	
    	if (reminders.size() > 1)
    	{
    		for (int i = 0; i < reminders.size(); i++)
    		{
    			ViewGroup reminderItem = (ViewGroup)lay_reminders.getChildAt(i);
        		reminderItem.findViewById(R.id.lay_reminder_options).setVisibility(View.VISIBLE);
    			new ReminderOptionsController(reminders.get(i), reminderItem.findViewById(R.id.btn_delete_reminder), reminderItem.findViewById(R.id.btn_edit_reminder));
    		}
    		btn_done.setVisibility(View.VISIBLE);
    		btn_done.setOnClickListener(new OnClickListener(){ public void onClick(View v)
			{
				finish();
			}});
    	}
    	else
    	{
    		lay_single_reminder_options.setVisibility(View.VISIBLE);
    		new ReminderOptionsController(reminders.get(0), btn_delete_single_reminder, btn_edit_single_reminder);
    		btn_keep_single_reminder.setOnClickListener(new OnClickListener(){ public void onClick(View v)
			{
				finish();
			}});
    	}
    }
    
    private class ReminderOptionsController
    {
    	public ReminderOptionsController(final Reminder reminder, View btn_delete_reminder, View btn_edit_reminder)
    	{
    		btn_delete_reminder.setOnClickListener(new OnClickListener() {
				public void onClick(View v)
				{
					if (reminder.contactIds.size() == 1)
						dao.deleteReminder(reminder);
					else
					{
						reminder.contactIds.remove((Integer)contact.id);
						dao.putReminder(reminder);
					}
					Toast.makeText(NotificationCallEndActivity.this, R.string.reminder_deleted, Toast.LENGTH_LONG).show();
					if (reminders.size() > 1)
					{
						reminders.remove(reminder);
						populateUI();
					}
					else
						finish();
				}
			});

    		btn_edit_reminder.setOnClickListener(new OnClickListener() {
				public void onClick(View v)
				{
					startActivityForResult(new Intent(NotificationCallEndActivity.this, AddEditReminderActivity.class)
						.putExtra(AddEditReminderActivity.KEY_REMINDER, reminder), 0);
				}
			});
    	}
    }
    
	public void onActivityResult(int requestCode, int resultCode, Intent returnIntent)
    {
		if (resultCode == RESULT_CANCELED)
			return;
		
		Reminder reminder = (Reminder) returnIntent.getSerializableExtra(AddEditReminderActivity.KEY_REMINDER);
		if (resultCode == RESULT_OK)
		{
			dao.putReminder(reminder);
			Toast.makeText(NotificationCallEndActivity.this, R.string.reminder_edited, Toast.LENGTH_LONG).show();
			if (reminders.size() > 1)
			{
				if (reminder.contactIds.contains(contact.id))
					reminders.set(reminders.indexOf(reminder), reminder);
				else
					reminders.remove(reminder);
				populateUI();
			}
			else
				finish();
		}
		else if (resultCode == AddEditReminderActivity.RESULT_DELETE)
		{
			dao.deleteReminder(reminder);
			Toast.makeText(NotificationCallEndActivity.this, R.string.reminder_deleted, Toast.LENGTH_LONG).show();
			if (reminders.size() > 1)
			{
				reminders.remove(reminder);
				populateUI();
			}
			else
				finish();
		}
    }
    
    protected void setListeners() {}
}