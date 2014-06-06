package ro.raduarin.callreminder.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ro.raduarin.callreminder.R;
import ro.raduarin.callreminder.contactsQuery.Contact;
import ro.raduarin.callreminder.contactsQuery.ContactsQuery;
import ro.raduarin.callreminder.dao.Reminder;
import ro.raduarin.callreminder.dao.ReminderColor;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class AddEditReminderActivity extends BaseActivity
{
	public static final int RESULT_DELETE = 1;
	public static final String KEY_REMINDER = "reminder";
	private Reminder reminder;
	
	private LinearLayout lay_contacts;
	private Button btn_add_contact, btn_color, btn_start_date, btn_cancel, btn_delete, btn_save_reminder;
	private EditText edt_message;
	private ToggleButton cb_enabled;
	
	private static final SimpleDateFormat alarmDateFormat = new SimpleDateFormat("EEE, d MMM\nh:mm a");
	
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        reminder = (Reminder) getIntent().getSerializableExtra(KEY_REMINDER);

        setContentView(R.layout.add_edit_reminder);
        linkUI();
        populateUI();
        setListeners();
    }
	
	private void linkUI()
	{
		lay_contacts = (LinearLayout) findViewById(R.id.lay_contacts);
		btn_add_contact = (Button) findViewById(R.id.btn_add_contact);
		edt_message = (EditText) findViewById(R.id.edt_message);
		cb_enabled = (ToggleButton) findViewById(R.id.cb_enabled);
		btn_color = (Button) findViewById(R.id.btn_color);
		btn_start_date = (Button) findViewById(R.id.btn_start_date);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_delete = (Button) findViewById(R.id.btn_delete);
		btn_save_reminder = (Button) findViewById(R.id.btn_save_reminder);
	}
	
	private void populateUI()
	{
		if (!reminder.contactIds.isEmpty())
		{
			for (Contact contact : new ContactsQuery(this).getContactsByIds(reminder.contactIds))
				addContactToUI(contact);
			btn_delete.setVisibility(View.VISIBLE);
			btn_save_reminder.setText(R.string.save);
		}
		edt_message.setBackgroundResource(reminder.color.noteResId);
		edt_message.setText(reminder.message);
		cb_enabled.setChecked(reminder.isEnabled);
		btn_color.setBackgroundResource(reminder.color.iconResId);
		if (reminder.startDate != null)
			btn_start_date.setText(alarmDateFormat.format(reminder.startDate.getTime()));
	}
	
	private void addContactToUI(final Contact contact)
	{
		final FrameLayout contactItem = (FrameLayout) getLayoutInflater().inflate(R.layout.contact_item, null);
		Button contactButton = (Button) contactItem.findViewById(R.id.btn_contact_button);
		contactButton.setText(contact.name);
		contactButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				reminder.contactIds.remove((Integer)contact.id);
				lay_contacts.removeView(contactItem);
				if (reminder.contactIds.isEmpty())
					btn_add_contact.setText(getString(R.string.add_contact));
			}
		});
		lay_contacts.addView(contactItem, lay_contacts.getChildCount()-1);
		
		btn_add_contact.setText(null);
	}
	
	private void setListeners()
	{
		btn_add_contact.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
			}
		});

		setIconPicker();
		
		setStartDatePicker();
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				finish();
			}
		});
		
		btn_delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				new AlertDialog.Builder(AddEditReminderActivity.this).setMessage(R.string.delete_this_reminder)
				.setNegativeButton(R.string.no, null)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which)
					{
						setResult(RESULT_DELETE, new Intent().putExtra(KEY_REMINDER, reminder));
						finish();
					}})
				.show();
			}
		});
		
		btn_save_reminder.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				reminder.message = edt_message.getText().toString();
				reminder.isEnabled = cb_enabled.isChecked();
				
				if (reminder.contactIds.isEmpty())
				{
					new AlertDialog.Builder(AddEditReminderActivity.this).setMessage(R.string.no_contacts)
					.setPositiveButton(R.string.ok, null)
					.show();
					return;
				}
				
				setResult(RESULT_OK, new Intent().putExtra(KEY_REMINDER, reminder));
				finish();
			}
		});
	}
	
	private void setIconPicker()
	{
		btn_color.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				View pickColorView = getLayoutInflater().inflate(R.layout.pick_reminder_color, null);
				final AlertDialog iconPicker = new AlertDialog.Builder(AddEditReminderActivity.this).setMessage(R.string.reminder_color)
						.setView(pickColorView).create();
				
				for(ReminderColor color : ReminderColor.values())
					pickColorView.findViewById(color.buttonResId).setOnClickListener(new OnClickListener() {
						public void onClick(View colorButton)
						{
							reminder.color = ReminderColor.getButtonColor(colorButton);
							btn_color.setBackgroundResource(reminder.color.iconResId);
							iconPicker.dismiss();
						}
					});
				
				iconPicker.show();
			}
		});
	}
	
	private void setStartDatePicker()
	{
		btn_start_date.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				final Calendar calendar = Calendar.getInstance();

				final DatePickerDialog datePicker = new DatePickerDialog(AddEditReminderActivity.this, new OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
					{
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, monthOfYear);
						calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						new TimePickerDialog(AddEditReminderActivity.this, new OnTimeSetListener() {
							public void onTimeSet(TimePicker view, int hourOfDay, int minute)
							{
								calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
								calendar.set(Calendar.MINUTE, minute);
								reminder.startDate = calendar;
								btn_start_date.setText(alarmDateFormat.format(reminder.startDate.getTime()));
							}
						}, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
						.show();
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				
				if (reminder.startDate != null)
					datePicker.setButton3(getString(R.string.remove), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							reminder.startDate = null;
							btn_start_date.setText(R.string.starting_from);
						}});
				datePicker.show();
			}
		});
	}
	
	public void onActivityResult(int reqCode, int resultCode, Intent returnIntent) 
	{
		if (resultCode != RESULT_OK)
			return;

	    Cursor cursor = managedQuery(returnIntent.getData(), new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
	    cursor.moveToFirst();
    	Contact contact = new Contact(cursor.getInt(0), cursor.getString(1));
    	cursor.close();
    	reminder.contactIds.add(contact.id);
    	addContactToUI(contact);
	}
}