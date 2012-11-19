package com.ndrue.gathereroffline;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class InitCardData extends Activity {

	private final String pid = "InitCardData";
	private DataBaseHelper myDbHelper;
	public static DBAdapter dbadapter;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.initcardlayout);
		createDB();
		Intent retIt = new Intent();
		setResult(RESULT_CANCELED, retIt);
		finish();
	}

	private void createDB() {
		myDbHelper = new DataBaseHelper(this);
		//myDbHelper = new DataBaseHelper(this);
		try {
			myDbHelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		try {
			myDbHelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (myDbHelper != null) {
	        myDbHelper.close();
	    }
	}	
}
