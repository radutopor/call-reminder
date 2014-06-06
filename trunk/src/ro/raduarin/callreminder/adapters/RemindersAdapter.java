package ro.raduarin.callreminder.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import ro.raduarin.callreminder.R;
import ro.raduarin.callreminder.contactsQuery.Contact;
import ro.raduarin.callreminder.contactsQuery.ContactsQuery;
import ro.raduarin.callreminder.dao.Reminder;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RemindersAdapter extends ArrayAdapter<Reminder>
{
	public static final int SELECTING = 0;
	public static final int DELETING = 1;
	public static final int EDITING_ENABLED = 2;
	
	private LayoutInflater layoutInflater;
	private int darkGray, lightGray;
	private ContactsQuery contactsQuery;
	private HashMap<String, String> remindersContactNames;
	public int state = SELECTING;
	
	public RemindersAdapter(Activity activity, ArrayList<Reminder> reminders)
	{
		super(activity, 0, reminders);
		layoutInflater = activity.getLayoutInflater();
		darkGray = activity.getResources().getColor(R.color.dark_gray);
		lightGray = activity.getResources().getColor(R.color.light_gray);
		contactsQuery = new ContactsQuery(activity);
		remindersContactNames = new HashMap<String, String>();
		for (Reminder reminder : reminders)
			putReminderContactNames(reminder);
	}
	
	private void putReminderContactNames(Reminder reminder)
	{
		String contactNames = "";
		for (Contact contact : contactsQuery.getContactsByIds(reminder.contactIds))
			contactNames += contact.name + ", ";
		contactNames = contactNames.substring(0, contactNames.length()-2);
		remindersContactNames.put(reminder.getId(), contactNames);
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = layoutInflater.inflate(R.layout.reminder_item, null);
			convertView.setTag(new ViewHolder(convertView));
		}
		
		Reminder reminder = getItem(position);
		ViewHolder holder = (ViewHolder) convertView.getTag();

		convertView.setBackgroundResource(reminder.color.noteResId);
		holder.txt_message.setText(reminder.message);
		holder.txt_contacts_names.setText(remindersContactNames.get(reminder.getId()));
		holder.txt_message.setTextColor(reminder.isEnabled ? darkGray : lightGray);
		holder.txt_contacts_names.setTextColor(reminder.isEnabled ? darkGray : lightGray);
		holder.img_enabled.setBackgroundResource(reminder.isEnabled ? R.drawable.ic_check_on : R.drawable.ic_check_off);
		holder.img_enabled.setVisibility(state==EDITING_ENABLED ? View.VISIBLE : View.GONE);
		holder.img_delete.setVisibility(state==DELETING ? View.VISIBLE : state==EDITING_ENABLED ? View.GONE : View.INVISIBLE);
		
		return convertView;
	}
	
	public void update(Reminder reminder)
	{
		putReminderContactNames(reminder);
		for (int i = 0; i < getCount(); i++)
			if (getItem(i).getId().equals(reminder.getId()))
			{
				remove(getItem(i));
				insert(reminder, i);
				return;
			}
		insert(reminder, 0);
	}
	
	public void delete(Reminder reminder)
	{
		remindersContactNames.remove(reminder.getId());
		for (int i = 0; i < getCount(); i++)
			if (getItem(i).getId().equals(reminder.getId()))
			{
				remove(getItem(i));
				return;
			}
	}
	
	private class ViewHolder
	{
		public TextView txt_contacts_names, txt_message;
		public ImageView img_enabled, img_delete;
		
		public ViewHolder(View itemView)
		{
			txt_contacts_names = (TextView) itemView.findViewById(R.id.txt_contacts_names);
			txt_message = (TextView) itemView.findViewById(R.id.txt_message);
			img_enabled = (ImageView) itemView.findViewById(R.id.img_enabled);
			img_delete = (ImageView) itemView.findViewById(R.id.img_delete);
		}
	}
}