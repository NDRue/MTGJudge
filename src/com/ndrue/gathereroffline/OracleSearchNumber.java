package com.ndrue.gathereroffline;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OracleSearchNumber extends Activity {

	ArrayList<String> hsItems;
	ArrayAdapter<String> hsAdapter;
	Context ct;
	private final String pid = "OracleSearchNumber";
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.oracle_number);
		ct = this;
		initVar();
	}
	
	private void initVar() {
		DBAdapter dba = new DBAdapter(this, "GathererCards",
				"cname,ccost,ctype,cpowert,crules,csetrare",
				"text,text,text,text,text,text");
		hsItems = new ArrayList<String>();
		hsAdapter = new ArrayAdapter<String>(this, R.layout.listview_item,
				hsItems);
		dba.open();
		Cursor c = dba.query("ExpansionList", "expname", null, null, null, null, "expname", -1);
		if(c.moveToFirst()) {
			String valName = c.getString(0);
			while(valName!=null) {
				if(valName!=null) {
					hsItems.add(valName);
				}
				valName = null;
				try {
					c.moveToNext();
					valName = c.getString(0);
				} catch (Exception e) {
					valName = null;
				}
			}
			ListView lV = (ListView)findViewById(R.id.expansionLists);
			lV.setAdapter(hsAdapter);
			hsAdapter.notifyDataSetChanged();
			try {
				c.close();
			} catch (Exception e) {
			}
			try {
				dba.close();
			} catch (Exception e) {
			}
			setEvents();
		} else {
			try {
				c.close();
			} catch (Exception e) {
			}
			try {
				dba.close();
			} catch (Exception e) {
			}
			Toast.makeText(this, "An error has occurred loading Expansions list", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setEvents() {
		ListView lV = (ListView)findViewById(R.id.expansionLists);
		lV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				EditText eT = (EditText)findViewById(R.id.searchValue);
				String expName = null;
				try {
					expName = ((TextView)arg1).getText().toString();
					if(expName.trim().equals("")) {
						throw new Exception();
					}
					int tryGet = Integer.parseInt(eT.getText().toString());
					if(tryGet>0) {
						getSearch(tryGet, expName);
					} else {
						throw new Exception();
					}
				} catch (Exception e) {
					if(expName!=null) {
						getSearchAll(expName);
					} else {
						Toast.makeText(ct, "Please enter a valid collector's number, or leave blank to list all cards from that expansion set", Toast.LENGTH_LONG).show();
					}
				}
			}
			
		});
	}
	
	private void getSearchAll(String ename) {
		Intent itn = new Intent(ct, MainActivity.class);
		Bundle b = new Bundle();
		b.putString("setnamerestrict", ename);
		itn.putExtras(b);
		startActivity(itn);
	}
	
	private void getSearch(int cnum, String ename) {
		DBAdapter dba = new DBAdapter(this, "GathererCards",
				"cname,ccost,ctype,cpowert,crules,csetrare",
				"text,text,text,text,text,text");
		dba.open();
		//dba.query(tbname, cols, sel, selArgs, groupBy, having, orderBy, limit)
		Cursor c = dba.query("GathererCards", "cname, csetnumber, uid", "csetnumber LIKE ?", new String[] { "%" + ename + " " + cnum + "%" }, null, null, "cname", -1);
		String csetNum = "";
		String cname = "";
		String cuid = "";
		boolean hasHit = false;
		if(c.moveToFirst()) {
			if(c.getCount()==1) {
				csetNum = c.getString(1);
				cname = c.getString(0);
				cuid = c.getString(2);
				String[] toSplit = csetNum.split("//");
				for(int i=0;i<toSplit.length;++i) {
					Log.d(pid, "Split (" + i + "): " + cname + ", " + csetNum + ", " + toSplit[i]);
					if(toSplit[i].equals(ename + " " + cnum)) {
						Log.d(pid, "Hit");
						hasHit = true;
						i = toSplit.length + 1;
					}
				}
			} else {
				while(!hasHit) {
					csetNum = c.getString(1);
					cname = c.getString(0);
					cuid = c.getString(2);
					String[] toSplit = csetNum.split("//");
					for(int i=0;i<toSplit.length;++i) {
						Log.d(pid, "Split (" + i + "): " + cname + ", " + csetNum + ", " + toSplit[i]);
						if(toSplit[i].equals(ename + " " + cnum)) {
							Log.d(pid, "Hit");
							hasHit = true;
							i = toSplit.length + 1;
						}
					}
					if(!c.moveToNext()) {
						Toast.makeText(ct, "No such collector's number that corresponds to the expansion set", Toast.LENGTH_SHORT).show();
						break;
					}
				}
			}
		} else {
			Toast.makeText(ct, "No such collector's number that corresponds to the expansion set", Toast.LENGTH_SHORT).show();
		}
		c.close();
		dba.close();
		if(hasHit) {
			Intent itn = new Intent(ct, ShowCardDetails.class);
			Bundle b = new Bundle();
			b.putString("searchquery", cname);
			itn.putExtras(b);
			startActivity(itn);
		}
	}
}
