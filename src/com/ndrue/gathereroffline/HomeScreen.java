package com.ndrue.gathereroffline;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HomeScreen extends Activity {

	private ArrayList<String> hsItems;
	private ArrayAdapter<String> hsAdapter;
	private ArrayList<String> hsItemsSub;
	private ArrayAdapter<String> hsAdapterSub;
	private final String[] hsItemsHeader = new String[] { "Main Items >",
			"Oracle, Etc. >", "Misc >" };
	private String[][] hsItemsArray = new String[3][10];
	private final String[] hsItemsInArray = new String[] { "Penalties",
			"Layers / Casting Spells",
			"Resolving Spells / Copiable Characteristics",
			"Types of Information", "Head Judge Announcement",
			"Reviews / Feedback", "Oracle", "Decklist Counter",
			"Comprehensive Rules", "Infraction Procedure Guide",
			"Magic Tournament Rules", "Add On", "Miscellaneous Options" };
	private Context ct;
	private LogCatcher logC;
	private int hsLevel = 0;
	private int hsParent = -1;
	private final String pid = "HomeScreen";

	@Override
	public void onCreate(Bundle b) {
		try {
			super.onCreate(b);
			setContentView(R.layout.homescreen);
			this.ct = this;
			logC = new LogCatcher();
			hsItems = new ArrayList<String>();
			hsAdapter = new ArrayAdapter<String>(this, R.layout.listview_item,
					hsItems);
			hsItemsSub = new ArrayList<String>();
			hsAdapterSub = new ArrayAdapter<String>(this,
					R.layout.listview_item, hsItemsSub);
			hsItemsArray[0] = new String[] { "> Penalties",
					"> Layers / Casting Spells",
					"> Resolving Spells / Copiable Characteristics",
					"> Types of Information", "> Head Judge Announcement",
					"> Reviews / Feedback",
					"> Comprehensive Rules",
					"> Infraction Procedure Guide",
					"> Magic Tournament Rules" };
			hsItemsArray[1] = new String[] { "> Oracle", "> Advanced Oracle Search", "> Decklist Counter" };
			hsItemsArray[2] = new String[] { "> Add On Apps", "> Options and Updates", "> About" };
			// for (int i = 0; i < hsItemsInArray.length; ++i) {
			for (int i = 0; i < hsItemsHeader.length; ++i) {
				// hsItems.add(hsItemsInArray[i]);
				hsItems.add(hsItemsHeader[i]);
			}
			ListView lv = (ListView) findViewById(R.id.homescreenoptions);
			lv.setAdapter(hsAdapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					navigate(((TextView) arg1).getText().toString());
				}

			});
			hsAdapter.notifyDataSetChanged();
			ListView lv2 = (ListView) findViewById(R.id.homescreenoptions2);
			lv2.setAdapter(hsAdapterSub);
			lv2.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					navigate2(((TextView) arg1).getText().toString());
				}

			});

			Intent itnt = new Intent(this, InitCardData.class);
			startActivityForResult(itnt, 1);
		} catch (Exception e) {
			logC.write(e.getMessage());
		}
	}

	private void navigate(String dir) {
		try {
			int sel = -1;
			if (hsLevel == 0) {
				for (int i = 0; i < hsItemsHeader.length; ++i) {
					if (hsItemsHeader[i].equals(dir)) {
						sel = i;
					}
				}
				hsItemsSub.clear();
				for (int i = 0; i < hsItemsArray[sel].length; ++i) {
					hsItemsSub.add(hsItemsArray[sel][i]);
				}
				hsParent = sel;
				LinearLayout ll = (LinearLayout)findViewById(R.id.submenulayout);
				ll.setVisibility(View.VISIBLE);
				hsAdapterSub.notifyDataSetChanged();
				++hsLevel;
				Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
			       ll.startAnimation(animationFadeIn);				
			}
		} catch (Exception e) {
			logC.write(e.getMessage());
		}
	}

	private void navigate2(String dir) {
		try {
			int sel = -1;
			if (hsLevel == 1) {
				Intent reUsable;
				// Bundle reuseBundle;
				for (int i = 0; i < hsItemsArray[hsParent].length; ++i) {
					if (hsItemsArray[hsParent][i].equals(dir)) {
						sel = i;
					}
				}
				Log.e(pid, "Clicked: " + dir + ", " + hsParent + ", " + sel);
				switch (hsParent) {
				case 0:
					switch (sel) {
					case 0:
						reUsable = new Intent(ct, ImageOnly.class);
						reUsable.putExtra("image", R.drawable.penalties_guide);
						startActivity(reUsable);
						break;
					case 1:
						reUsable = new Intent(ct, ImageOnly.class);
						reUsable.putExtra("image",
								R.drawable.layers_castingspells);
						startActivity(reUsable);
						break;
					case 2:
						reUsable = new Intent(ct, ImageOnly.class);
						reUsable.putExtra("image",
								R.drawable.resolvespells_copiable);
						startActivity(reUsable);
						break;
					case 3:
						reUsable = new Intent(ct, ImageOnly.class);
						reUsable.putExtra("image",
								R.drawable.information_details);
						startActivity(reUsable);
						break;
					case 4:
						reUsable = new Intent(ct, ImageOnly.class);
						reUsable.putExtra("image", R.drawable.hjannouncement);
						startActivity(reUsable);
						break;
					case 5:
						reUsable = new Intent(ct, ImageOnly.class);
						reUsable.putExtra("image", R.drawable.reviewfeedback);
						startActivity(reUsable);
						break;
					case 6:
						reUsable = new Intent(ct, ComprehensiveRules.class);
						startActivity(reUsable);
						break;
					case 7:
						reUsable = new Intent(ct, InfractionPenalty.class);
						startActivity(reUsable);
						break;
					case 8:
						reUsable = new Intent(ct, TournamentRules.class);
						startActivity(reUsable);
						break;
					}
					break;
				case 1:
					switch (sel) {
					case 0:
						reUsable = new Intent(ct, MainActivity.class);
						startActivity(reUsable);
						break;
					case 1:
						reUsable = new Intent(ct, AdvancedSearchHome.class);
						startActivity(reUsable);
						break;
					case 2:
						reUsable = new Intent(ct, DecklistCounter.class);
						startActivity(reUsable);
						break;
					}
					break;
				case 2:
					switch (sel) {
					case 0:
						reUsable = new Intent(ct, ExtraAddOns.class);
						startActivity(reUsable);
						break;
					case 1:
						reUsable = new Intent(ct, MiscOptions.class);
						startActivity(reUsable);
						break;
					case 2:
						showVersion();
						break;
					}
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			logC.write(e.getMessage());
		}
	}

	private void showVersion() {
		Log.e(pid, "Entered showVersion");
		String lastDT = "";
		String lastVer = "";
		DBAdapter dba = new DBAdapter(ct, "GathererCards",
				"cname,ccost,ctype,cpowert,crules,csetrare",
				"text,text,text,text,text,text");
		Log.e(pid, "Entered showVersion 2");
		dba.open();
		Log.e(pid, "Entered showVersion 3");
		Cursor c = dba.query("MiscOpt", new String[] { "optvalue" }, "optname = ?",
					new String[] { "lastdt" });
		Log.e(pid, "Entered showVersion 4");
		if(c.moveToFirst()) {
			String dtLast = c.getString(0);
			String[] dtSplit = dtLast.split(" ");
			String[] dtSplit2 = dtSplit[0].split("-");
			String[] months = new String[] { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
			lastDT = dtSplit2[2] + " " + months[Integer.parseInt(dtSplit2[1])] + " " + dtSplit2[0];
/*			
			SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss"); 
			Date date;
			try {
				date = dt.parse(c.getString(0));
				SimpleDateFormat dt1 = new SimpleDateFormat("dd MMM yyyy");
				lastDT = dt1.format(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/ 
		}
		Log.e(pid, "Entered showVersion 7");
		c.close();
		Log.e(pid, "Got last DT: " + lastDT);
		c = dba.query("MiscOpt", new String[] { "optvalue" }, "optname = ?",
				new String[] { "dbversion" });
		if(c.moveToFirst()) {
			lastVer = c.getString(0);
		}
		c.close();
		dba.close();
		Log.e(pid, "Got last ver: " + lastVer);
		Log.e(pid, "Closed DB showVersion");
		AlertDialog.Builder aD = new AlertDialog.Builder(ct);
		aD.setTitle("About");
		String crVer = "";
		try {
			crVer = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e(pid, "Showing:\n" + "Judge Core App\nby Andrew Teo\n\nVersion: " + crVer + "\n\nDatabase last updated:\n" +
		lastDT + "\n\nDatabase version:\n" + lastVer);
		aD.setMessage("Judge Core App\nby Andrew Teo\n\nVersion: " + crVer + "\n\nDatabase last updated:\n" +
		lastDT + "\n\nDatabase version:\n" + lastVer + "\n\nThis app is not endorsed nor produced by Wizards, Hasbro or its subsidiaries. This app is a personal project of Andrew Teo.");
		aD.setPositiveButton("OK", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
			
		});
		aD.show();
	}
	
	@Override
	public void onBackPressed() {
		if (hsLevel == 0) {
			finish();
		} else {
			hsItemsSub.clear();
			hsLevel = 0;
			hsParent = -1;
			LinearLayout ll = (LinearLayout)findViewById(R.id.submenulayout);
			Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
		       ll.startAnimation(animationFadeOut);
		     animationFadeOut.setAnimationListener(new AnimationListener() {

				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					((LinearLayout) findViewById(R.id.submenulayout))
					.setVisibility(View.GONE);
				}

				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}

				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}
		    	 
		     });
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
				&& keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}