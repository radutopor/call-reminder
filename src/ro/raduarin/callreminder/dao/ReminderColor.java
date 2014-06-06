package ro.raduarin.callreminder.dao;

import ro.raduarin.callreminder.R;
import android.view.View;

public enum ReminderColor
{
	YELLOW (R.drawable.note_bkg_yellow, R.drawable.ic_color_yellow, R.id.btn_color_yellow),
	RED (R.drawable.note_bkg_red, R.drawable.ic_color_red, R.id.btn_color_red),
	GREEN (R.drawable.note_bkg_green, R.drawable.ic_color_green, R.id.btn_color_green),
	BLUE (R.drawable.note_bkg_blue, R.drawable.ic_color_blue, R.id.btn_color_blue);
	
	public final int noteResId;
	public final int iconResId;
	public final int buttonResId;
    
    ReminderColor(int noteResId, int iconResId, int buttonResId)
    {
        this.noteResId = noteResId;
        this.iconResId = iconResId;
        this.buttonResId = buttonResId;
    }
    
    public static ReminderColor getButtonColor(View colorButton)
    {
		for (ReminderColor color : values())
			if (color.buttonResId == colorButton.getId())
				return color;
		return null;
    }
}