package ro.raduarin.callreminder.callManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class OutgoingCallReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, final Intent intent) 
    {
       	CallManager.onNewCall(context, intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));				
    	new Handler().postDelayed(new Runnable(){ public void run()
    	{
        	CallManager.onCallReceived();				
    	}}, 45000);
    }
}