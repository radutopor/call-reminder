package ro.raduarin.callreminder.activities;

import ro.raduarin.callreminder.R;
import ro.raduarin.callreminder.adapters.RemindersAdapter;
import ro.raduarin.callreminder.dao.Dao;
import ro.raduarin.callreminder.dao.Reminder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends BaseActivity
{
	private Dao dao;
	
    private Button btn_info, btn_add_reminder, btn_clear_list;
    private FrameLayout lay_reminders_controls;
    private ToggleButton btn_delete_reminder, btn_toggle_enabled, btn_disable_enable_all;
    private ListView lv_reminders;
	private SlidingDrawer drawer_options;
	private TextView txt_add_reminder;
	
	private RemindersAdapter remindersAdapter;

	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        dao = new Dao(this);

        setContentView(R.layout.main);
        linkUI();
        populateUI();
        setListeners();
    }
	
	private void linkUI()
	{
    	btn_info = (Button) findViewById(R.id.btn_info);
    	lay_reminders_controls = (FrameLayout) findViewById(R.id.lay_reminders_controls);
    	btn_add_reminder = (Button) findViewById(R.id.btn_add_reminder);
    	btn_delete_reminder = (ToggleButton) findViewById(R.id.btn_delete_reminder);
    	lv_reminders = (ListView) findViewById(R.id.lv_reminders);
    	drawer_options = (SlidingDrawer) findViewById(R.id.drawer_options);
    	btn_toggle_enabled = (ToggleButton) findViewById(R.id.btn_toggle_enabled);
    	btn_disable_enable_all = (ToggleButton) findViewById(R.id.btn_disable_enable_all);
    	btn_clear_list = (Button) findViewById(R.id.btn_clear_list);
    	txt_add_reminder = (TextView) findViewById(R.id.txt_add_reminder);
	}
	
	private void populateUI()
	{
		TextView lv_top_spacing = new TextView(this);
		lv_top_spacing.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		lv_reminders.addHeaderView(lv_top_spacing);
		TextView lv_bottom_spacing = new TextView(this);
		lv_bottom_spacing.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36);
		lv_reminders.addFooterView(lv_bottom_spacing);
		
		remindersAdapter = new RemindersAdapter(this, dao.getAllReminders());
		lv_reminders.setAdapter(remindersAdapter);
		
		checkNoReminders();
		checkAllRemindersDisabled();
	}
	
	public void onWindowFocusChanged(boolean hasFocus)		// set options SlidingDrawer height
	{
		LayoutParams lp = drawer_options.getLayoutParams();
		if (lp.height == LayoutParams.WRAP_CONTENT)
		{
			lp.height = findViewById(R.id.drawer_options_handle).getHeight();
			lp.height += findViewById(R.id.drawer_options_content).getHeight();
			drawer_options.setLayoutParams(lp);
		}
	}
	
	private void setListeners()
	{
		btn_info.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0)
			{
			}
		});
		
		txt_add_reminder.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				btn_add_reminder.performClick();
			}
		});
		
		btn_add_reminder.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0)
			{
				startActivityForResult(new Intent(MainActivity.this, AddEditReminderActivity.class)
					.putExtra(AddEditReminderActivity.KEY_REMINDER, new Reminder()), 0);
			}
		});
		
		btn_delete_reminder.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				btn_toggle_enabled.setChecked(false);
				remindersAdapter.state = btn_delete_reminder.isChecked() ? RemindersAdapter.DELETING : RemindersAdapter.SELECTING;
				remindersAdapter.notifyDataSetChanged();
			}
		});

		btn_toggle_enabled.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				btn_delete_reminder.setChecked(false);
				remindersAdapter.state = btn_toggle_enabled.isChecked() ? RemindersAdapter.EDITING_ENABLED : RemindersAdapter.SELECTING;
				remindersAdapter.notifyDataSetChanged();
			}
		});
		
		btn_disable_enable_all.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				if (!btn_disable_enable_all.isChecked())
					applyDisableEnableAll();
				else
					new AlertDialog.Builder(MainActivity.this).setMessage(R.string.disable_all_reminders)
					.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							btn_disable_enable_all.setChecked(false);
						}})
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							applyDisableEnableAll();
						}})
					.show();
			}
		});
		
		btn_clear_list.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				new AlertDialog.Builder(MainActivity.this).setMessage(R.string.clear_the_reminder_list)
				.setNegativeButton(R.string.no, null)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which)
					{
						dao.deleteAllReminders();
						remindersAdapter.clear();
						drawer_options.close();
						checkNoReminders();
					}})
				.show();
			}
		});
		
		lv_reminders.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
			{
				Reminder reminder = remindersAdapter.getItem(pos - 1);	// one header view in ListView
				switch(remindersAdapter.state)
				{
					case RemindersAdapter.SELECTING:
						startActivityForResult(new Intent(MainActivity.this, AddEditReminderActivity.class)
							.putExtra(AddEditReminderActivity.KEY_REMINDER, reminder), 0);
						break;
					case RemindersAdapter.DELETING:
						dao.deleteReminder(reminder);
						remindersAdapter.delete(reminder);
						remindersAdapter.state = RemindersAdapter.SELECTING;
						btn_delete_reminder.setChecked(false);
						checkNoReminders();
						break;
					case RemindersAdapter.EDITING_ENABLED:
						reminder.isEnabled = !reminder.isEnabled;
						dao.putReminder(reminder);
						remindersAdapter.notifyDataSetChanged();
						checkAllRemindersDisabled();
				}
			}
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent returnIntent)
    {
		if (resultCode == RESULT_CANCELED)
			return;
		
		Reminder reminder = (Reminder) returnIntent.getSerializableExtra(AddEditReminderActivity.KEY_REMINDER);
		if (resultCode == RESULT_OK)
		{
			dao.putReminder(reminder);
			remindersAdapter.update(reminder);
		}
		else if (resultCode == AddEditReminderActivity.RESULT_DELETE)
		{
			dao.deleteReminder(reminder);
			remindersAdapter.delete(reminder);
		}
		checkNoReminders();
    }
	
	private void checkNoReminders()
	{
		lay_reminders_controls.setVisibility(remindersAdapter.isEmpty() ? View.GONE : View.VISIBLE);
		txt_add_reminder.setVisibility(remindersAdapter.isEmpty() ? View.VISIBLE : View.GONE);	
	}
	
	private void checkAllRemindersDisabled()
	{
		for (int i = 0; i < remindersAdapter.getCount(); i++)
			if (remindersAdapter.getItem(i).isEnabled)
			{
				btn_disable_enable_all.setChecked(false);
				return;
			}
		btn_disable_enable_all.setChecked(true);
	}
	
	private void applyDisableEnableAll()
	{
		for (int i = 0; i < remindersAdapter.getCount(); i++)
		{
			remindersAdapter.getItem(i).isEnabled = !btn_disable_enable_all.isChecked();
			dao.putReminder(remindersAdapter.getItem(i));
		}
		remindersAdapter.notifyDataSetChanged();
	}
}