package ro.raduarin.callreminder.contactsQuery;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class ContactsQuery
{
	private Context context;

	public ContactsQuery(Context context)
	{
		this.context = context;
	}
	
	public ArrayList<Contact> getContactsByIds(ArrayList<Integer> contactIds)
	{
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		
		String contactSelection = "(";
		for (int contactId : contactIds)
			contactSelection += contactId + ",";
		contactSelection = contactSelection.substring(0, contactSelection.length()-1) + ")";
		
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
			new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
			ContactsContract.Contacts._ID + " IN " + contactSelection, null, null);
		
		while (cursor.moveToNext())
			contacts.add(new Contact(cursor.getInt(0), cursor.getString(1)));
		cursor.close();

		return contacts;
	}
	
	public Contact getContactByPhoneNo(String phoneNumber)
	{
		Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)),
			new String[]{PhoneLookup._ID, PhoneLookup.DISPLAY_NAME}, null, null, null);
		
		Contact contact = cursor.moveToFirst() ? new Contact(cursor.getInt(0), cursor.getString(1)) : new Contact(-1, phoneNumber);
		cursor.close();
		return contact;
	}
}