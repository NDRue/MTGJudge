package com.ndrue.gathereroffline;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

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
