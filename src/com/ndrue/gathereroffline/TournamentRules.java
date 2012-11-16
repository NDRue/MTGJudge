package com.ndrue.gathereroffline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
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

public class TournamentRules extends Activity {

	private ArrayList<String> dataLines;
	private ArrayList<String> listPop;
	private ArrayList<String> corresponding;
	private final String pid = "TournamentRules";
	private final String ptable = "MTRRules";
	private boolean searching = false;
	private Context ct;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.compr_main);
		ct = this;
		initVar();
	}

	private void initVar() {
		// preload all data
		loadData();
		populateList();
		setEvents();
		Log.w(pid, "Done");
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
	}

	private void findSearch(String f) {
		Log.w(pid, "Searching for: " + f);
		DBAdapter dba = new DBAdapter(this, ptable,
				"idname,explaintext,parentid", "text,text,text");
		dba.open();
		Cursor c = dba.query(ptable, new String[] { "idname",
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
			b.putString("from", ptable);
			b.putString("parent", "Search0805466");
			String[] toSearchArr = new String[findResults.size()];
			findResults.toArray(toSearchArr);
			b.putStringArray("searches", toSearchArr);
			Intent itn = new Intent(this, DisplayDetails.class);
			itn.putExtras(b);
			startActivity(itn);
		}
	}
	
	private void loadNext(String header) {
		int pos = dataLines.indexOf(header);
		Bundle b = new Bundle();
		b.putString("from", ptable);
		b.putString("parent", corresponding.get(pos));
		Log.w(pid, "Sending: " + corresponding.get(pos));
		Intent itn = new Intent(this, DisplayDetails.class);
		itn.putExtras(b);
		startActivity(itn);
	}

	private void populateList() {
//		listPop = new ArrayList<String>();
//		for (int i = 0; i < dataLines.size(); ++i) {
//			if (dataLines.get(i).toLowerCase().equals("contents")) {
//				int ctr = i + 1;
//				boolean isHeader = false;
//				while (true) {
//					if (!dataLines.get(ctr).trim().toLowerCase()
//							.equals("glossary")) {
//						if (dataLines.get(ctr).trim().equals("")) {
//							isHeader = false;
//						} else {
//							if (!isHeader) {
//								isHeader = true;
//								listPop.add(dataLines.get(ctr).trim());
//							}
//						}
//					} else {
//						break;
//					}
//					++ctr;
//				}
//			}
//		}
		ListView lV;
		ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(this,
				R.layout.listview_item, dataLines);
		((ListView) findViewById(R.id.listView1)).setAdapter(arrAdapter);
	}

	private void loadData() {
		//isExist();
		try {
			corresponding = new ArrayList<String>();
			dataLines = new ArrayList<String>();
			DBAdapter dba = new DBAdapter(this, ptable,
					"idname,explaintext,parentid", "text,text,text");
			dba.open();
			Cursor c = dba.query(ptable, new String[] { "idname",
					"explaintext", "id" }, "parentid = '' OR parentid IS NULL", null, null, null, "id", -1);
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
			finish();
		}
	}

	private boolean fileExistance(String fname) {
		File file = getBaseContext().getFileStreamPath(fname);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

/*	private void isExist() {
		if (!fileExistance("ComprRules")) {
			writeFileToInternalStorage();
		}
	}*/

/*	private void writeFileToInternalStorage() {
		String eol = System.getProperty("line.separator");
		FileOutputStream writer = null;
		try {
			writer = openFileOutput("ComprRules", MODE_PRIVATE);
			InputStream iS = getAssets().open("MagicCompRules.txt");
			byte[] buffer = new byte[1024];
			int length;
			while ((length = iS.read(buffer)) > 0) {
				writer.write(buffer, 0, length);
			}
			iS.close();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}*/
}
