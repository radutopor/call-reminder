package ro.raduarin.callreminder.activities;

import java.util.ArrayList;

import ro.raduarin.callreminder.R;
import ro.raduarin.callreminder.contactsQuery.Contact;
import ro.raduarin.callreminder.dao.Reminder;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotificationActivity extends Activity
{
	public static final String KEY_CONTACT = "contact";
	public static final String KEY_REMINDERS = "reminders";
	protected Contact contact;
	protected ArrayList<Reminder> reminders;
	
	protected View lay_notification;
	protected TextView txt_notification_message;
	protected ViewGroup lay_reminders;
	
    @SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	contact = (Contact) getIntent().getSerializableExtra(KEY_CONTACT);
    	reminders = (ArrayList<Reminder>) getIntent().getSerializableExtra(KEY_REMINDERS);

    	setContentView(R.layout.notification);
    	linkUI();
    	populateUI();
   		setListeners();
    }
    
    protected void linkUI()
    {
    	lay_notification = findViewById(R.id.lay_notification);
    	txt_notification_message = (TextView) findViewById(R.id.txt_notification_message);
    	lay_reminders = (ViewGroup) findViewById(R.id.lay_reminders);
    }
    
    protected void populateUI()
    {
    	txt_notification_message.setText(getString(R.string.notification_message_call_start).replace("`", contact.name));
    	for (Reminder reminder : reminders)
    	{
    		ViewGroup reminderItem = (ViewGroup) getLayoutInflater().inflate(R.layout.reminder_notification_item, null);
    		((TextView)reminderItem.findViewById(R.id.txt_reminder_message)).setText(reminder.message);
    		lay_reminders.addView(reminderItem);
    	}
    }
    
    protected void setListeners()
    {
    	lay_notification.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				finish();
			}
		});
    }
}