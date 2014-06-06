package ro.raduarin.callreminder.activities;

import ro.raduarin.callreminder.R;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class BaseActivity extends Activity
{
	public void setContentView(int resId)
	{
		super.setContentView(resId);
		
		findViewById(R.id.btn_info).setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				// info screen
			}
		});
	}
}