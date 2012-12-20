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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AdvancedSearchHome extends Activity {

	private ArrayList<String> hsItems;
	private ArrayAdapter<String> hsAdapter;
	private ArrayList<Boolean> hsAdapterSubSel;
	private ArrayList<String> hsItemsSub;
	private ArrayAdapter<String> hsAdapterSub;
	private final String[] hsItemsHeader = new String[] { "Card Title >",
			"Oracle Text >", "Card Type >", "Expansion >",
			"Collector's Number >", ">> Search <<" };
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
			setContentView(R.layout.advancesearchhome);
			((TextView) findViewById(R.id.textView1))
					.setText("Advanced Search");
			this.ct = this;
			logC = new LogCatcher();
			setLists();
		} catch (Exception e) {
			logC.write(e.getMessage());
		}
	}

	private void setLists() {
		hsItems = new ArrayList<String>();
		hsAdapter = new ArrayAdapter<String>(this, R.layout.listview_item,
				hsItems);
		hsItemsSub = new ArrayList<String>();
		hsAdapterSub = new ArrayAdapter<String>(this, R.layout.listview_item,
				hsItemsSub);
		for (int i = 0; i < hsItemsHeader.length; ++i) {
			// hsItems.add(hsItemsInArray[i]);
			hsItems.add(hsItemsHeader[i]);
		}
		ListView lv = (ListView) findViewById(R.id.homescreenoptions);
		lv.setAdapter(hsAdapter);
		hsAdapter.notifyDataSetChanged();

		DBAdapter dba = new DBAdapter(this, "GathererCards",
				"cname,ccost,ctype,cpowert,crules,csetrare",
				"text,text,text,text,text,text");
		hsItemsSub = new ArrayList<String>();
		hsAdapterSubSel = new ArrayList<Boolean>();
		hsAdapterSub = new ArrayAdapter<String>(this, R.layout.checkeditem,
				hsItemsSub);
		dba.open();
		Cursor c = dba.query("ExpansionList", "expname", null, null, null,
				null, "expname", -1);
		if (c.moveToFirst()) {
			String valName = c.getString(0);
			while (valName != null) {
				if (valName != null) {
					hsItemsSub.add(valName);
					hsAdapterSubSel.add(false);
					Log.d(pid, "Added: " + valName);
				}
				valName = null;
				try {
					c.moveToNext();
					valName = c.getString(0);
				} catch (Exception e) {
					valName = null;
				}
			}
			ListView lV = (ListView) findViewById(R.id.homescreenoptions2);
			lV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			lV.setAdapter(hsAdapterSub);
			hsAdapterSub.notifyDataSetChanged();
			try {
				c.close();
			} catch (Exception e) {
			}
			try {
				dba.close();
			} catch (Exception e) {
			}
		} else {
			try {
				c.close();
			} catch (Exception e) {
			}
			try {
				dba.close();
			} catch (Exception e) {
			}
			Toast.makeText(this,
					"An error has occurred loading Expansions list",
					Toast.LENGTH_SHORT).show();
		}
		setEvents();
	}

	private void setEvents() {
		ListView lv = (ListView) findViewById(R.id.homescreenoptions);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				navigate(((TextView) arg1).getText().toString(), arg2);
			}

		});
		ListView lV = (ListView) findViewById(R.id.homescreenoptions2);
		lV.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				clicked(arg1, ((TextView) arg1).getText().toString(), arg2);
			}

		});
	}

	private void clicked(View v, String s, int o) {
		boolean isSel = false;
		isSel = hsAdapterSubSel.get(o);
		hsAdapterSubSel.set(o, (isSel ? false : true));
	}

	private void navigate(String dir, int sel) {
		if (sel < 5) {
			LinearLayout ll = (LinearLayout) findViewById(R.id.submenulayout);
			ll.setVisibility(View.VISIBLE);
			Animation animationFadeIn = AnimationUtils.loadAnimation(this,
					R.anim.fadein);
			ll.startAnimation(animationFadeIn);
			++hsLevel;
		}
		EditText eT;
		eT = (EditText) findViewById(R.id.cardtitletext);
		eT.setVisibility(View.GONE);
		eT = (EditText) findViewById(R.id.cardoracletext);
		eT.setVisibility(View.GONE);
		eT = (EditText) findViewById(R.id.cardtypetext);
		eT.setVisibility(View.GONE);
		eT = (EditText) findViewById(R.id.cardnumbertext);
		eT.setVisibility(View.GONE);
		((ListView) findViewById(R.id.homescreenoptions2))
				.setVisibility(View.GONE);
		switch (sel) {
		case 0:
			eT = (EditText) findViewById(R.id.cardtitletext);
			eT.setVisibility(View.VISIBLE);
			break;
		case 1:
			eT = (EditText) findViewById(R.id.cardoracletext);
			eT.setVisibility(View.VISIBLE);
			break;
		case 2:
			eT = (EditText) findViewById(R.id.cardtypetext);
			eT.setVisibility(View.VISIBLE);
			break;
		case 3:
			((ListView) findViewById(R.id.homescreenoptions2))
					.setVisibility(View.VISIBLE);
			break;
		case 4:
			eT = (EditText) findViewById(R.id.cardnumbertext);
			eT.setVisibility(View.VISIBLE);
			break;
		case 5:
			goSearch();
			break;
		}
		/*
		 * try { int sel = -1; if (hsLevel == 0) { for (int i = 0; i <
		 * hsItemsHeader.length; ++i) { if (hsItemsHeader[i].equals(dir)) { sel
		 * = i; } } hsItemsSub.clear(); for (int i = 0; i <
		 * hsItemsArray[sel].length; ++i) {
		 * hsItemsSub.add(hsItemsArray[sel][i]); } hsParent = sel; LinearLayout
		 * ll = (LinearLayout)findViewById(R.id.submenulayout);
		 * ll.setVisibility(View.VISIBLE); hsAdapterSub.notifyDataSetChanged();
		 * ++hsLevel; Animation animationFadeIn =
		 * AnimationUtils.loadAnimation(this, R.anim.fadein);
		 * ll.startAnimation(animationFadeIn); } } catch (Exception e) {
		 * logC.write(e.getMessage()); }
		 */
	}

	private void goSearch() {
		String ctitle = "";
		String coracle = "";
		String ctype = "";
		String cnumber = "";
		EditText eT;
		eT = (EditText) findViewById(R.id.cardtitletext);
		ctitle = eT.getText().toString();
		eT = (EditText) findViewById(R.id.cardoracletext);
		coracle = eT.getText().toString();
		eT = (EditText) findViewById(R.id.cardtypetext);
		ctype = eT.getText().toString();
		eT = (EditText) findViewById(R.id.cardnumbertext);
		cnumber = eT.getText().toString();
		ArrayList<String> expSel = new ArrayList<String>();
		int ttlSel = 0;
		for (int i = 0; i < hsAdapterSubSel.size(); ++i) {
			if (hsAdapterSubSel.get(i)) {
				expSel.add(hsItemsSub.get(i));
			}
		}
		String[] expArr = new String[1];
		if (expSel.size() > 0) {
			expArr = new String[expSel.size()];
			for (int i = 0; i < expSel.size(); ++i) {
				expArr[i] = expSel.get(i);
			}
		}
		Intent tn = new Intent(ct, AdvancedSearch.class);
		Bundle bn = new Bundle();
		bn.putString("ctitle", ctitle);
		bn.putString("coracle", coracle);
		bn.putString("ctype", ctype);
		bn.putString("cnumber", cnumber);
		if (expArr.length > 0) {
			bn.putStringArray("expansions", expArr);
		}
		tn.putExtras(bn);
		startActivity(tn);
	}

	private void navigate2(String dir) {
		/*
		 * try { int sel = -1; if (hsLevel == 1) { Intent reUsable; // Bundle
		 * reuseBundle; for (int i = 0; i < hsItemsArray[hsParent].length; ++i)
		 * { if (hsItemsArray[hsParent][i].equals(dir)) { sel = i; } } switch
		 * (hsParent) { case 0: switch (sel) { case 0: reUsable = new Intent(ct,
		 * ImageOnly.class); reUsable.putExtra("image",
		 * R.drawable.penalties_guide); startActivity(reUsable); break; case 1:
		 * reUsable = new Intent(ct, ImageOnly.class);
		 * reUsable.putExtra("image", R.drawable.layers_castingspells);
		 * startActivity(reUsable); break; case 2: reUsable = new Intent(ct,
		 * ImageOnly.class); reUsable.putExtra("image",
		 * R.drawable.resolvespells_copiable); startActivity(reUsable); break;
		 * case 3: reUsable = new Intent(ct, ImageOnly.class);
		 * reUsable.putExtra("image", R.drawable.information_details);
		 * startActivity(reUsable); break; case 4: reUsable = new Intent(ct,
		 * ImageOnly.class); reUsable.putExtra("image",
		 * R.drawable.hjannouncement); startActivity(reUsable); break; case 5:
		 * reUsable = new Intent(ct, ImageOnly.class);
		 * reUsable.putExtra("image", R.drawable.reviewfeedback);
		 * startActivity(reUsable); break; case 6: reUsable = new Intent(ct,
		 * ComprehensiveRules.class); startActivity(reUsable); break; case 7:
		 * reUsable = new Intent(ct, InfractionPenalty.class);
		 * startActivity(reUsable); break; case 8: reUsable = new Intent(ct,
		 * TournamentRules.class); startActivity(reUsable); break; } break; case
		 * 1: switch (sel) { case 0: reUsable = new Intent(ct,
		 * MainActivity.class); startActivity(reUsable); break; case 1: reUsable
		 * = new Intent(ct, OracleSearchNumber.class); startActivity(reUsable);
		 * break; case 2: reUsable = new Intent(ct, DecklistCounter.class);
		 * startActivity(reUsable); break; } break; case 2: switch (sel) { case
		 * 0: reUsable = new Intent(ct, ExtraAddOns.class);
		 * startActivity(reUsable); break; case 1: reUsable = new Intent(ct,
		 * MiscOptions.class); startActivity(reUsable); break; } break; default:
		 * break; } } } catch (Exception e) { logC.write(e.getMessage()); }
		 */
	}

	@Override
	public void onBackPressed() {
		if (hsLevel == 0) {
			finish();
		} else {
			hsLevel = 0;
			hsParent = -1;
			LinearLayout ll = (LinearLayout) findViewById(R.id.submenulayout);
			Animation animationFadeOut = AnimationUtils.loadAnimation(this,
					R.anim.fadeout);
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