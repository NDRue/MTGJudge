package com.ndrue.gathereroffline;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	private final String[] hsItemsHeader = new String[] { "Quick reference >",
			"Rules >", "Misc >" };
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
					"> Reviews / Feedback" };
			hsItemsArray[1] = new String[] { "> Comprehensive Rules",
					"> Infraction Procedure Guide", "> Magic Tournament Rules" };
			hsItemsArray[2] = new String[] { "> Oracle", "> Decklist Counter",
					"> Add On", "> Miscellaneous Options" };
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
					}
					break;
				case 1:
					switch (sel) {
					case 0:
						reUsable = new Intent(ct, ComprehensiveRules.class);
						startActivity(reUsable);
						break;
					case 1:
						reUsable = new Intent(ct, InfractionPenalty.class);
						startActivity(reUsable);
						break;
					case 2:
						reUsable = new Intent(ct, TournamentRules.class);
						startActivity(reUsable);
						break;
					}
					break;
				case 2:
					switch (sel) {
					case 0:
						reUsable = new Intent(ct, MainActivity.class);
						startActivity(reUsable);
						break;
					case 1:
						reUsable = new Intent(ct, DecklistCounter.class);
						startActivity(reUsable);
						break;
					case 2:
						reUsable = new Intent(ct, ExtraAddOns.class);
						startActivity(reUsable);
						break;
					case 3:
						reUsable = new Intent(ct, MiscOptions.class);
						startActivity(reUsable);
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