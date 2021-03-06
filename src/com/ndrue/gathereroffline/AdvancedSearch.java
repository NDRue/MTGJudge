package com.ndrue.gathereroffline;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AdvancedSearch extends Activity {

	public static DBAdapter dbadapter;
	private static final String pid = "MainActivity";
	private Context ct;
	private ArrayAdapter<String> arrAdapter;
	private ArrayList<String> arrList;
	private ArrayList<String> arrMultiverseID;
	private String queryStr = "";
	private LoadCards lC;
	private boolean isChanged = false;
	private int lastLength = 0;
	private boolean updateLocked = false;
	private boolean timeoutTrue = false;
	private boolean searchwaitRunning = false;
	private LogCatcher logC;
	private String setName = "";
	private Bundle incomingBundle;
	private boolean firstRun = false;
	private String ctitle = "";
	private String coracle = "";
	private String ctype = "";
	private String cnumber = "";
	private String[] expList;
	private NVPair nvPair;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ct = this;
		((EditText) findViewById(R.id.editText1)).setVisibility(View.GONE);
		initVar();
	}

	protected class searchWaitAsync extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// While it's true, do nothing
			while (searchwaitRunning) {
				timeoutTrue = true;
				searchwaitRunning = false;
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return searchwaitRunning ? "Y" : "N";
		}

		@Override
		protected void onPostExecute(String res) {
			timeoutTrue = false;
			searchwaitRunning = false;
			searchText();
		}

	}

	private void searchWait() {
		Log.w(pid, "searchWait: " + searchwaitRunning + ", " + timeoutTrue);
		searchwaitRunning = true;
		if (!timeoutTrue) {
			new searchWaitAsync().execute();
		}
	}

	private void searchText() {
		EditText et1 = (EditText) findViewById(R.id.editText1);
		queryStr = et1.getText().toString();
		if (queryStr.length() >= 3 || !setName.equals("")) {
			isChanged = true;
			if (lC == null) {
				LinearLayout infobar = (LinearLayout) findViewById(R.id.infoviewbar);
				infobar.setVisibility(View.VISIBLE);
				TextView infoTV = (TextView) findViewById(R.id.infotextview);
				infoTV.setText("Searching...");
				ProgressBar infoBar = (ProgressBar) findViewById(R.id.infoprogressbar);
				infoBar.setVisibility(View.VISIBLE);
				lC = new LoadCards();
				lC.execute("", "", "");
			} else {
				Log.w(pid, "Getting status: " + lC.getStatus() + ", "
						+ AsyncTask.Status.FINISHED);
				if (lC.getStatus() == AsyncTask.Status.FINISHED) {
					LinearLayout infobar = (LinearLayout) findViewById(R.id.infoviewbar);
					infobar.setVisibility(View.VISIBLE);
					TextView infoTV = (TextView) findViewById(R.id.infotextview);
					infoTV.setText("Searching...");
					ProgressBar infoBar = (ProgressBar) findViewById(R.id.infoprogressbar);
					infoBar.setVisibility(View.VISIBLE);
					lC = new LoadCards();
					lC.execute("", "", "");
				}
			}
		} else {
			if (queryStr.length() > 0 && lastLength >= 3) {
				// Toast.makeText(
				// ct,
				// "You have entered too few characters to search with.\nPlease enter at least 3 characters to begin the search.",
				// Toast.LENGTH_LONG).show();
			}
			Log.w(pid, "Clearing arrList not AsyncTask");
			arrList.clear();
			arrMultiverseID.clear();
			nvPair = new NVPair();
			arrAdapter.notifyDataSetChanged();
		}
		lastLength = queryStr.length();
		searchwaitRunning = false;
		timeoutTrue = false;
	}

	private void initVar() {
		try {
			Bundle bn = getIntent().getExtras();
			if (bn.containsKey("ctitle")) {
				ctitle = bn.getString("ctitle");
			}
			if (bn.containsKey("coracle")) {
				coracle = bn.getString("coracle");
			}
			if (bn.containsKey("ctype")) {
				ctype = bn.getString("ctype");
				ctype = ctype.replace(" ", "%");
			}
			if (bn.containsKey("cnumber")) {
				cnumber = bn.getString("cnumber");
			}
			if (bn.containsKey("expansions")) {
				String[] explistTemp = bn.getStringArray("expansions");
				expList = new String[explistTemp.length];
				for (int i = 0; i < explistTemp.length; ++i) {
					expList[i] = explistTemp[i];
				}
			}
		} catch (Exception e) {
			Toast.makeText(
					this,
					"An unknown error has occurred during search. Please try again.",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		if (ctitle.equals("")
				&& coracle.equals("")
				&& ctype.equals("")
				&& (expList == null || expList.length <= 0 || expList[0] == null)) {
			Toast.makeText(this, "Please enter at least one search option",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		// Add listener for text change
		logC = new LogCatcher();
		arrList = new ArrayList<String>();
		arrMultiverseID =new ArrayList<String>(); 
		arrAdapter = new ArrayAdapter<String>(ct, R.layout.listview_item,
				arrList);
		ListView lV = (ListView) findViewById(R.id.listView1);
		lV.setAdapter(arrAdapter);
		EditText et1 = (EditText) findViewById(R.id.editText1);
		et1.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		et1.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					searchText();
				}
				return false;
			}

		});
		et1.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				searchWait();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

		});
		// et1.setOnKeyListener(new OnKeyListener() {
		//
		// public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
		// // TODO Auto-generated method stub
		// searchText();
		// return false;
		// }
		//
		// });
		lV.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				clickedItem(((TextView) arg1).getText().toString());
			}

		});
		new LoadCards().execute("");
	}

	private void clickedItem(String cx) {
		Intent itn = new Intent(ct, ShowCardDetails.class);
		Bundle b = new Bundle();
		b.putString("searchquery", cx);
		b.putString("multiverseid", nvPair.get(cx));
		itn.putExtras(b);
		startActivity(itn);
	}

	private class LoadCards extends AsyncTask<String, String, String> {

		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			while (updateLocked) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Log.w(pid, "Thread sleep interrupted");
				}
			}
			updateLocked = true;
			String toRet = "N/A";
			ArrayList<String> condVar = new ArrayList<String>();
			String condLine = "";
			if (!ctitle.equals("")) {
				condLine = condLine + (!condLine.equals("") ? " AND " : "")
						+ "cname LIKE ?";
				condVar.add("%" + ctitle + "%");
			}
			if (!coracle.equals("")) {
				condLine = condLine + (!condLine.equals("") ? " AND " : "")
						+ "crules LIKE ?";
				condVar.add("%" + coracle + "%");
			}
			if (!ctype.equals("")) {
				condLine = condLine + (!condLine.equals("") ? " AND " : "")
						+ "ctype LIKE ?";
				condVar.add("%" + ctype + "%");
			}
			if (expList[0] != null) {
				condLine = condLine + (!condLine.equals("") ? " AND " : "")
						+ "(";
				String toAddCLine = "";
				if (!cnumber.equals("")) {
					for (int i = 0; i < expList.length; ++i) {
						toAddCLine = toAddCLine
								+ (!toAddCLine.equals("") ? " OR " : "")
								+ "csetnumber LIKE ? OR csetnumber LIKE ?";
						condVar.add("%" + expList[i] + " " + cnumber);
						condVar.add("%" + expList[i] + " " + cnumber + "//%");
					}
				} else {
					for (int i = 0; i < expList.length; ++i) {
						toAddCLine = toAddCLine
								+ (!toAddCLine.equals("") ? " OR " : "")
								+ "csetrare LIKE ?";
						condVar.add("%" + expList[i] + "%");
					}
				}
				condLine = condLine + toAddCLine;
				condLine = condLine + ")";
			} else {
				if (!cnumber.equals("")) {
					condLine = condLine + (!condLine.equals("") ? " AND " : "")
							+ "(";
					String toAddCLine = "";
					for (int i = 0; i < expList.length; ++i) {
						toAddCLine = toAddCLine
								+ (!toAddCLine.equals("") ? " OR " : "")
								+ "csetnumber LIKE ? OR csetnumber LIKE ?";
						condVar.add("% " + cnumber);
						condVar.add("% " + cnumber + "//%");
					}
					condLine = condLine + toAddCLine;
					condLine = condLine + ")";
				}
			}
			Log.w(pid, "Clearing arrList AsyncTask");
			arrList.clear();
			arrMultiverseID.clear();
			nvPair = new NVPair();
			dbadapter = new DBAdapter(ct, "GathererCards",
					"cname,ccost,ctype,cpowert,crules,csetrare",
					"text,text,text,text,text,text");
			dbadapter.open();
			// Load by batches of 50
			// while (!finished) {
			Log.d(pid, "Searching for: " + queryStr);
			if (isChanged) {
				isChanged = false;
				// arrList.clear();
			}
			Cursor c = null;
			String[] toCondVarArr = new String[condVar.size()];
			for (int i = 0; i < condVar.size(); ++i) {
				toCondVarArr[i] = condVar.get(i);
			}
			c = dbadapter.query("GathererCards", new String[] { "cname","multiverse" },
					condLine, toCondVarArr, null, null, "cname", -1);
			if (c.getCount() > 0) {
				c.moveToFirst();
				while (true) {
					Log.d(pid, "Got: " + c.getString(0));
					arrList.add(c.getString(0));
					arrMultiverseID.add(c.getString(1));
					nvPair.add(c.getString(0), c.getString(1));
					if (!c.moveToNext()) {
						break;
					}
				}
				c.close();
				toRet = "";
			}
			c.close();
			dbadapter.close();
			return toRet;
		}

		protected void onPostExecute(String res) {
			super.onPostExecute(res);
			LinearLayout infobar = (LinearLayout) findViewById(R.id.infoviewbar);
			infobar.setVisibility(View.GONE);
			if (res != null) {
				if (res.equals("N/A") && !firstRun) {
					infobar = (LinearLayout) findViewById(R.id.infoviewbar);
					infobar.setVisibility(View.VISIBLE);
					TextView infoTV = (TextView) findViewById(R.id.infotextview);
					infoTV.setText("No results found");
					ProgressBar infoBar2 = (ProgressBar) findViewById(R.id.infoprogressbar);
					infoBar2.setVisibility(View.INVISIBLE);
					Toast.makeText(
							ct,
							"Your search has returned no results.\nHave you entered correctly?",
							Toast.LENGTH_LONG).show();
				} else {
					firstRun = false;
				}
			}
			Collections.sort(arrList);
			arrAdapter.notifyDataSetChanged();
			updateLocked = false;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == 1) {

				// if (resultCode == RESULT_OK) {

				// Toast.makeText(this, "Returned", Toast.LENGTH_SHORT).show();
				// loadCards();
				// }
				//
				// if (resultCode == RESULT_CANCELED) {
				//
				// // Write your code on no result return
				//
				// }
			}
		} catch (Exception e) {
			String toDebug = e.getMessage();
			logC.write(toDebug);
		}
	}
}
