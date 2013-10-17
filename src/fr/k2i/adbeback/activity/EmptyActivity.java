package fr.k2i.adbeback.activity;

import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.string;
import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;

public class EmptyActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NotificationManager mNM =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mNM.cancel(R.string.local_service_started);
		finish();
	}
}
