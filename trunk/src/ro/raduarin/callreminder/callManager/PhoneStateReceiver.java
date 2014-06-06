package ro.raduarin.callreminder.callManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneStateReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
    	String callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		if (callState.equals(TelephonyManager.EXTRA_STATE_RINGING))
			CallManager.onNewCall(context, intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
		else if (callState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
			CallManager.onCallReceived();
		else if (callState.equals(TelephonyManager.EXTRA_STATE_IDLE))
			CallManager.onCallEnd(context);
    }
}