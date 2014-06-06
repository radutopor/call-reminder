package ro.raduarin.callreminder.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Reminder implements Serializable
{
	private static final long serialVersionUID = -7649028467954405145L;
	
	private String id;
	public ArrayList<Integer> contactIds;
	public String message;
	public ReminderColor color;
	public Calendar startDate;
	public boolean isEnabled;
	
	public Reminder()
	{
		id = "" + Math.random();
		contactIds = new ArrayList<Integer>();
		color = ReminderColor.YELLOW;
		isEnabled = true;
	}
	
	public String getId()
	{
		return id;
	}
	
	public boolean equals(Object other)
	{
		return id.equals(((Reminder)other).id);
	}
}