package com.ndrue.gathereroffline;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ExtraAddOns extends Activity {

	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.homescreen);
		initVar();
	}
	
	private void initVar() {
		TextView tV;
		tV = (TextView)findViewById(R.id.textView1);
		tV.setText("Add Ons");
	}
}
