package com.ndrue.gathereroffline;

import java.io.BufferedReader;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddonDescription extends Activity {

	private Context ct;
	private final String pid = "AddonDescription";
	private String addOnID = "";
	private String mMarket = "";
	private String mSite = "";

	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.addon_description);
		ct = this;
		initVar();
	}

	private void initVar() {
		Bundle b = getIntent().getExtras();
		if (!b.containsKey("addonid")) {
			errorReturn();
		}
		addOnID = b.getString("addonid");
		int getID = 0;
		try {
			getID = Integer.parseInt(addOnID);
		} catch (Exception e) {
			errorReturn();
		}
		Log.e(pid, "GetID: " + getID);
		DBAdapter dba = new DBAdapter(this, "GathererCards",
				"cname,ccost,ctype,cpowert,crules,csetrare",
				"text,text,text,text,text,text");
		dba.open();
		Cursor c = null;
		try {
			c = dba.query(
					"AddOnList",
					"addonauthor, addondescription, addonname, addonmarket, addonwebsite, addonimage",
					"uid = ?", new String[] { (getID + "") });
			if (c.moveToFirst()) {
				TextView tv = (TextView) findViewById(R.id.textView1);
				tv.setText(c.getString(2)
						+ System.getProperty("line.separator") + "by "
						+ c.getString(0));
				tv = (TextView) findViewById(R.id.textView2);
				tv.setText(c.getString(1));
				mMarket = c.getString(3);
				mSite = c.getString(4);
				byte[] imgblob = c.getBlob(5);
				if (imgblob.length > 0) {
					Bitmap imgbmp = BitmapFactory.decodeByteArray(imgblob, 0,
							imgblob.length);
					ImageView iv = (ImageView) findViewById(R.id.addOnImage);
					iv.setImageBitmap(imgbmp);
				}
				Button btn;
				if (mMarket.equals("")) {
					btn = (Button) findViewById(R.id.button2);
					btn.setEnabled(false);
				} else {
					btn = (Button) findViewById(R.id.button2);
					btn.setOnClickListener(new OnClickListener() {

						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							visitMarket();
						}

					});
				}
				if (mSite.equals("")) {
					btn = (Button) findViewById(R.id.button1);
					btn.setEnabled(false);
				} else {
					btn = (Button) findViewById(R.id.button1);
					btn.setOnClickListener(new OnClickListener() {

						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							visitSite();
						}

					});
				}
				c.close();
				dba.close();
			} else {
				c.close();
				dba.close();
				errorReturn();
			}
		} catch (Exception e) {
			if (c != null) {
				c.close();
			}
			dba.close();
			errorReturn("Your database is too old. Please update it and try again.");
		}
	}

	private void visitMarket() {
		AlertDialog.Builder aD = new AlertDialog.Builder(ct);
		aD.setTitle("Add On Notice");
		aD.setMessage("You will be directed to the app's Play Store details page.\n\nPlease note that all Add-Ons are bounded by their individual usage terms.\nThis app (MTG:Judge Core) and its creator does not hold responsibility over what the other apps do.\n\nPlease confirm that would like to visit the page.");

		aD.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(mMarket));
				startActivity(intent);
			}
		});
		aD.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		aD.show();
	}

	private void visitSite() {
		AlertDialog.Builder aD = new AlertDialog.Builder(ct);
		aD.setTitle("Add On Notice");
		aD.setMessage("You will be directed to the app developer's web site for downloading of the app.\n\nPlease note that all Add-Ons are bounded by their individual usage terms.\nThis app (MTG:Judge Core) and its creator does not hold responsibility over what the other apps do.\n\nPlease confirm that would like to visit the website.");

		aD.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(mSite));
				startActivity(intent);
			}
		});
		aD.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		aD.show();
	}

	private void errorReturn() {
		errorReturn("There was an error loading this add-on's details.\nPlease try again.");
	}

	private void errorReturn(String s) {
		Toast.makeText(ct, s, Toast.LENGTH_LONG).show();
		finish();
	}

}
