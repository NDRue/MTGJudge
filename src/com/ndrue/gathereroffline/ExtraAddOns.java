package com.ndrue.gathereroffline;

import java.util.ArrayList;

import com.ndrue.gathereroffline.MiscOptions.startDownload;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExtraAddOns extends Activity {

	Context ct;
	private ArrayList<String> auid;
	private ArrayList<String> aname;
	private ArrayList<String> apackage;
	private ArrayList<String> amarket;
	private ArrayList<String> awebsite;
	private ArrayList<String> hsItems;
	private ArrayAdapter<String> hsAdapter;
	private String mLink = "";
	private String wLink = "";

	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.homescreen);
		ct = this;
		initVar();
	}

	private void initVar() {
		auid = new ArrayList<String>();
		aname = new ArrayList<String>();
		apackage = new ArrayList<String>();
		amarket = new ArrayList<String>();
		awebsite = new ArrayList<String>();
		TextView tV;
		tV = (TextView) findViewById(R.id.textView1);
		tV.setText("Add Ons");
		DBAdapter dbadapter;
		dbadapter = new DBAdapter(ct, "GathererCards",
				"cname,ccost,ctype,cpowert,crules,csetrare",
				"text,text,text,text,text,text");
		dbadapter.open();
		Cursor c = null;
		try {
		c = dbadapter.query("AddOnList",
				"addonname, addonpackage, addonmarket, addonwebsite, uid, addonauthor");
		if (c.moveToFirst()) {
			while (true) {
				auid.add(c.getString(4));
				aname.add(c.getString(0) + " by " + c.getString(5));
				apackage.add(c.getString(1));
				amarket.add(c.getString(2));
				awebsite.add(c.getString(3));
				if (!c.moveToNext()) {
					break;
				}
			}
		}
		c.close();
		dbadapter.close();
		hsItems = new ArrayList<String>();
		for (int i = 0; i < aname.size(); ++i) {
			hsItems.add(aname.get(i));
		}
		hsAdapter = new ArrayAdapter<String>(this, R.layout.listview_item,
				hsItems);
		ListView lV = (ListView) findViewById(R.id.homescreenoptions);
		lV.setAdapter(hsAdapter);
		hsAdapter.notifyDataSetChanged();
		lV.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				navigateLaunch(arg2);
			}

		});
		} catch (Exception e) {
			if(c!=null) {
				c.close();
			}
			dbadapter.close();
			Toast.makeText(ct, "Your database is too old. Please update it and try again.", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void navigateLaunch(int s) {
		mLink = amarket.get(s);
		wLink = awebsite.get(s);
		String pname = apackage.get(s);
		boolean exists = false;
		android.content.pm.PackageManager mPm = getPackageManager(); // 1
		PackageInfo info;
		try {
			info = mPm.getPackageInfo(pname, 0);
			Boolean installed = info != null;
			exists = installed;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			exists = false;
			e.printStackTrace();
		} // 2,3
		if (exists) {
			Intent LaunchIntent = getPackageManager()
					.getLaunchIntentForPackage(pname);
			startActivity(LaunchIntent);
		} else {
			Intent itn = new Intent(ct, AddonDescription.class);
			itn.putExtra("addonid", auid.get(s));
			startActivity(itn);
/*			AlertDialog.Builder aD = new AlertDialog.Builder(ct);
			aD.setTitle("Add On Notice");
			aD.setMessage("The add-on that you have selected is not installed on your device.\nPlease note that all Add-Ons are bounded by their individual usage terms.\nThis app and its creator does not hold responsibility over what the other apps do.\n\nPlease choose where you would like to obtain it from:");

			aD.setPositiveButton("Google Play Store",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.dismiss();
							if (!mLink.equals("")) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse(mLink));
								startActivity(intent);
								mLink = "";
								wLink = "";
							}
						}
					});

			aD.setNeutralButton("Developer's Website",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int arg1) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							if (!wLink.equals("")) {
								Intent browserIntent = new Intent(
										Intent.ACTION_VIEW, Uri.parse(wLink));
								startActivity(browserIntent);
								mLink = "";
								wLink = "";
							}
						}
					});
			aD.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int arg1) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});

			aD.show();
			*/
		}
	}
}
