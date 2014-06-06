package ro.raduarin.callreminder.contactsQuery;

import java.io.Serializable;

public class Contact implements Serializable
{
	private static final long serialVersionUID = -8124186552681132348L;
	
	public int id;
	public String name;
	
	public Contact(int id, String name)
	{
		this.id = id;
		this.name = name;
	}
}