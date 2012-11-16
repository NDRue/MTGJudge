package com.ndrue.gathereroffline;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCardDetails extends Activity{
	
	private String cname = "";
	private final String nl = System.getProperty ("line.separator");
	private final String pid = "ShowCardDetails";
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.card_details);
		Button btn = (Button)findViewById(R.id.backbutton1);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				exitDetails();
			}
			
		});
		btn = (Button)findViewById(R.id.backbutton2);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				exitDetails();
			}
			
		});
		cname = getIntent().getExtras().getString("searchquery");
		Log.w(pid, "Got card name: " + cname);
		if(cname.length()<3) {
			Toast.makeText(this, "An error has occurred retrieving the card's details. Please try again.", Toast.LENGTH_SHORT).show();
			finish();
		}
		DBAdapter dba = new DBAdapter(this, "GathererCards",
				"cname,ccost,ctype,cpowert,crules,csetrare",
				"text,text,text,text,text,text");
		dba.open();
		Cursor c = dba.query("GathererCards", new String[] { "cname","ccost","ctype","cpowert","crules","csetrare" }, "cname = ?", new String[] { cname } );
		startManagingCursor(c);
		if(c.moveToFirst()) {
			if(c.getCount()!=1) {
				Toast.makeText(this, "An error has occurred retrieving the card's details. Please try again.", Toast.LENGTH_SHORT).show();
				dba.close();
				finish();
			} else {
				String toDisp = "";
				toDisp = "Card name:" + nl;
				toDisp = toDisp + c.getString(c.getColumnIndex("cname")) + nl + nl;
				toDisp = toDisp + "Casting cost:" + nl;
				toDisp = toDisp + c.getString(c.getColumnIndex("ccost")) + nl + nl;
				toDisp = toDisp + "Card type:" + nl;
				toDisp = toDisp + c.getString(c.getColumnIndex("ctype")) + nl + nl;
				toDisp = toDisp + "Card text:" + nl;
				toDisp = toDisp + c.getString(c.getColumnIndex("crules")).replace("\n", nl) + nl + nl;
				String powert = c.getString(c.getColumnIndex("cpowert"));
				if(!powert.equals("N/A")) {
					toDisp = toDisp + powert + nl + nl;
				}
				toDisp = toDisp + "Set(s) / Rarity:" + nl;
				toDisp = toDisp + c.getString(c.getColumnIndex("csetrare"));
				((TextView)findViewById(R.id.carddetails)).setText(toDisp);
				dba.close();
			}
		}
		try {
			c.close();
		} catch (Exception e) {
			Log.w(pid, e.toString());
		}
		dba.close();
	}
	
	private void exitDetails() {
		finish();
	}
}
