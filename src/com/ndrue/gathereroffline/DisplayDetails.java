package com.ndrue.gathereroffline;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayDetails extends Activity {

	private int levelDepth = 0;
	private ArrayList<String> dataLines;
	private ArrayList<String> corresponding;
	private ArrayList<String> idname;
	private boolean lowestLevel = false;
	private final String nl = System.getProperty("line.separator");
	private String tblName = "";
	private String pid = "";
	private String isSearch = null;
	private String searchStr = "";

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		Bundle getBundle = getIntent().getExtras();
		determineType(getBundle);
		initVar();
	}

	private void initVar() {
		if (!lowestLevel) {
			setContentView(R.layout.compr_main);
			EditText eTVVV = (EditText) findViewById(R.id.editText1);
			eTVVV.setVisibility(View.GONE);
			populateList();
			setEvents();
		} else {
			setContentView(R.layout.text_only);
			displayData();
		}
	}

	private class DisplayData extends AsyncTask<String, String, String> {

		TextView tV = (TextView) findViewById(R.id.textData);
		String tVData = "";
		boolean blocked = false;

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			// if is search
			Log.d(pid, "Entered displaydata: " + (isSearch == null));
			if (isSearch != null) {
				Log.d(pid, "Searching for: " + searchStr);
				String para = "";
				boolean noSearch = true;
				for (int i = 0; i < dataLines.size(); ++i) {
					if (dataLines.get(i).contains(searchStr)) {
						noSearch = false;
						tVData = tVData + (!tVData.equals("") ? (nl + nl) : "")
								+ dataLines.get(i).replace("\n", nl);
						publishProgress("");
					}
					// Log.d(pid, "Get dataline: (" + i + "): " +
					// dataLines.get(i));
					// Log.d(pid, "Get dataline: (" + i + "): Is Blank: "
					// + dataLines.get(i).trim().equals(""));
					/*
					 * if (dataLines.get(i).trim().equals("") || i == 0) { if
					 * (para.equals("")) { int startInt = 0; if (i > 0) {
					 * startInt = i + 1; } Log.d(pid, "Para is blank. Adding:");
					 * for (int j = startInt; j < dataLines.size(); ++j) { if
					 * (dataLines.get(j).trim().equals("")) { i = j + 1; j =
					 * dataLines.size(); } else { para = para +
					 * (!para.equals("") ? nl : "") + dataLines.get(j)
					 * .replace("\n", nl); Log.d(pid, dataLines.get(j)); } } if
					 * (!para.equals("")) { Log.d(pid, "Para contains search? "
					 * + para.contains(searchStr)); if
					 * (para.contains(searchStr)) { tVData = tVData +
					 * (!tVData.equals("") ? (nl + nl) : "") +
					 * para.replace("\n", nl); publishProgress(""); } para = "";
					 * } } }
					 */
				}
				if (noSearch) {
					for (int i = 0; i < dataLines.size(); ++i) {
						noSearch = false;
						tVData = tVData + (!tVData.equals("") ? (nl + nl) : "")
								+ dataLines.get(i).replace("\n", nl);
						publishProgress("");
					}
				}
			} else {
				for (int i = 0; i < dataLines.size(); ++i) {
					tVData = tVData + (!tVData.equals("") ? (nl + nl) : "")
							+ dataLines.get(i).replace("\n", nl);
					while (blocked) {
						// do nothing
					}
					blocked = true;
					publishProgress("");
				}
			}
			return null;
		}

		protected void onProgressUpdate(String... arg0) {
			super.onProgressUpdate(arg0);
			tV.setText(tVData);
			blocked = false;
		}

	}

	private void displayData() {
		new DisplayData().execute("", "", "");
	}

	private void determineType(Bundle b) {
		try {
			String connectorHead = "parentid";
			String connector = " = ";
			String connectorVar = "?";
			if (b.containsKey("isSearch")) {
				isSearch = "Y";
				searchStr = b.getString("searchStr");
			}
			dataLines = new ArrayList<String>();
			corresponding = new ArrayList<String>();
			tblName = b.getString("from");
			pid = "DisplayDetails-" + tblName;
			String parentID = b.getString("parent");
			String[] toSearchVal;
			if (b.containsKey("searches")) {
				int arrSize = b.getStringArray("searches").length;
				toSearchVal = new String[arrSize];
				toSearchVal = b.getStringArray("searches");
				connectorHead = "idname";
				connector = " IN ";
				connectorVar = "(";
				for (int i = 0; i < arrSize; ++i) {
					connectorVar = connectorVar + "?,";
				}
				connectorVar = connectorVar.substring(0,
						connectorVar.length() - 1) + ")";
			} else {
				toSearchVal = new String[1];
				toSearchVal[0] = parentID;
			}
			DBAdapter dba = new DBAdapter(this, tblName,
					"idname,explaintext,parentid", "text,text,text");
			dba.open();
			Log.w(pid, connector + connectorVar);
			Cursor c = dba.query(tblName, new String[] { "idname",
					"explaintext", "id" }, connectorHead + connector
					+ connectorVar, toSearchVal, null, null, "id", -1);
			Log.d(pid, "Connectors: " + connectorHead + connector
					+ connectorVar);
			for (int i = 0; i < toSearchVal.length; ++i) {
				Log.d(pid, "ToSearchVal (" + i + "): " + toSearchVal[i]);
			}
			Log.d(pid, "Lowest level now: " + lowestLevel);
			if (c.moveToFirst()) {
				do {
					dataLines.add(c.getString(1));
					corresponding.add(c.getString(0));
					Log.d(pid, "Lowest Level check: " + c.getString(1) + ", "
							+ c.getString(0) + ", " + c.getString(0).length());
					if (c.getString(0) == null) {
						Log.d(pid, "Lowest level because getString is null");
						lowestLevel = true;
					} else {
						if (c.getString(0).length() <= 0) {
							Log.d(pid,
									"Lowest level because getString length is 0");
							lowestLevel = true;
						}
					}
				} while (c.moveToNext());
			}
			Log.d(pid, "Lowest level after: " + lowestLevel);
			c.close();
			dba.close();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "An error has occurred. Please try again",
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private void setEvents() {
		ListView lV = (ListView) findViewById(R.id.listView1);
		lV.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				loadNext(((TextView) arg1).getText().toString());
			}

		});
	}

	private void loadNext(String header) {
		int pos = dataLines.indexOf(header);
		Bundle b = new Bundle();
		b.putString("from", tblName);
		b.putString("parent", corresponding.get(pos));
		if (isSearch != null) {
			b.putString("isSearch", "Y");
			b.putString("searchStr", searchStr);
		}
		Log.w(pid, "Sending: " + corresponding.get(pos));
		Intent itn = new Intent(this, DisplayDetails.class);
		itn.putExtras(b);
		startActivity(itn);
	}

	private void populateList() {
		ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(this,
				R.layout.listview_item, dataLines);
		((ListView) findViewById(R.id.listView1)).setAdapter(arrAdapter);
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)
				|| (keyCode == KeyEvent.KEYCODE_HOME)
				|| (keyCode == KeyEvent.KEYCODE_CALL)) {
			finish();
			return false;
		} else {
			return true;
		}
	}
}
