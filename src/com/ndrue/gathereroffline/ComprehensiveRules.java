package com.ndrue.gathereroffline;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ComprehensiveRules extends Activity {

	private ArrayList<String> dataLines;
	private ArrayList<String> corresponding;
	private final String pid = "ComprehensiveRules";
	private boolean searching = false;
	private Context ct;
	private LogCatcher logC;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.compr_main);
		ct = this;
		initVar();
	}

	private void initVar() {
		// preload all data
		logC = new LogCatcher();
		loadData();
		populateList();
		setEvents();
		Log.w(pid, "Done");
	}

	private void setEvents() {
		try {
		ListView lV = (ListView) findViewById(R.id.listView1);
		lV.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				loadNext(((TextView) arg1).getText().toString());
			}

		});
		EditText eTSearch;
		eTSearch = (EditText) findViewById(R.id.editText1);
		eTSearch.setHint("Press Enter to search");
		eTSearch.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						if (searching) {
							Toast.makeText(getBaseContext(),
									"Searching, please wait.",
									Toast.LENGTH_LONG).show();
						} else {
							String fTxt = ((EditText) v).getText().toString();
							if (fTxt.length() > 3) {
								searching = true;
								findSearch(fTxt);
							} else {
								Toast.makeText(ct, "You've entered too short a search term.\nPlease enter at least 4 characters.", Toast.LENGTH_LONG).show();
							}
						}
					}
				}
				return false;
			}

		});
		} catch (Exception e) {
			logC.write(e.getMessage());
		}
	}

	private void findSearch(String f) {
		try {
		Log.w(pid, "Searching for: " + f);
		DBAdapter dba = new DBAdapter(this, "ComprRules",
				"idname,explaintext,parentid", "text,text,text");
		dba.open();
		Cursor c = dba.query("ComprRules", new String[] { "idname",
				"explaintext", "parentid", "id" },
				"explaintext LIKE ? AND (idname = '' OR idname IS NULL)",
				new String[] { "%" + f + "%" }, null, null, "id", -1);
		startManagingCursor(c);
		ArrayList<String> findResults = new ArrayList<String>();
		if (c.moveToFirst()) {
			do {
				Log.w(pid, "Search returned: " + c.getString(2));
				findResults.add(c.getString(2));
			} while (c.moveToNext());
		}
		c.close();
		dba.close();
		searching = false;
		if (findResults.size() <= 0) {
			Toast.makeText(this, "No such text found.", Toast.LENGTH_SHORT)
					.show();
		} else {
			Bundle b = new Bundle();
			b.putString("from", "ComprRules");
			b.putString("parent", "Search0805466");
			String[] toSearchArr = new String[findResults.size()];
			findResults.toArray(toSearchArr);
			b.putStringArray("searches", toSearchArr);
			Intent itn = new Intent(this, DisplayDetails.class);
			itn.putExtras(b);
			startActivity(itn);
		}
		} catch (Exception e) {
			logC.write(e.getMessage());
		}
	}

	private void loadNext(String header) {
		try {
		int pos = dataLines.indexOf(header);
		Bundle b = new Bundle();
		b.putString("from", "ComprRules");
		b.putString("parent", corresponding.get(pos));
		Intent itn = new Intent(this, DisplayDetails.class);
		itn.putExtras(b);
		startActivity(itn);
		} catch (Exception e) {
			logC.write(e.getMessage());
		}
	}

	private void populateList() {
		ListView lV;
		ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(this,
				R.layout.listview_item, dataLines);
		((ListView) findViewById(R.id.listView1)).setAdapter(arrAdapter);
	}

	private void loadData() {
		try {
			corresponding = new ArrayList<String>();
			dataLines = new ArrayList<String>();
			DBAdapter dba = new DBAdapter(this, "ComprRules",
					"idname,explaintext,parentid", "text,text,text");
			dba.open();
			Cursor c = dba.query("ComprRules", new String[] { "idname",
					"explaintext", "id" }, "parentid = '' OR parentid IS NULL",
					null, null, null, "id", -1);
			startManagingCursor(c);
			if (c.moveToFirst()) {
				do {
					dataLines.add(c.getString(1));
					corresponding.add(c.getString(0));
				} while (c.moveToNext());
			}
			c.close();
			dba.close();
		} catch (Exception e) {
			Toast.makeText(
					this,
					"An error has occurred loading the file. Please try again.",
					Toast.LENGTH_SHORT).show();
			logC.write(e.getMessage());
			finish();
		}
	}
}
