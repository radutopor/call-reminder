package ro.raduarin.callreminder.dao;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;

public class Dao
{
	private Context context;
	
	public Dao(Context context)
	{
		this.context = context;
	}
	
	public ArrayList<Reminder> getAllReminders()
	{
		ArrayList<Reminder> reminders = new ArrayList<Reminder>();
		try {
			for (String reminderId : context.getFilesDir().list())
			{
				ObjectInputStream in = new ObjectInputStream(context.openFileInput(reminderId));
				reminders.add((Reminder) in.readObject());
				in.close();
			}
		} catch (Exception e) {}
		return reminders;
	}
	
	public void putReminder(Reminder reminder)
	{
		try {
			ObjectOutputStream out = new ObjectOutputStream(context.openFileOutput(reminder.getId(), Context.MODE_PRIVATE));
			out.writeObject(reminder);
			out.close();
		} catch (Exception e) {}
	}
	
	public void deleteReminder(Reminder reminder)
	{
		new File(context.getFilesDir(), reminder.getId()).delete();
	}
	
	public void deleteAllReminders()
	{
		for (File reminder : context.getFilesDir().listFiles())
			reminder.delete();
	}
}