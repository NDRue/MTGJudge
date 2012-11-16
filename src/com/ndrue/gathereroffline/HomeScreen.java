package com.ndrue.gathereroffline;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HomeScreen extends Activity {
	
	private ArrayList<String> hsItems;
	private ArrayAdapter<String> hsAdapter;
	private final String[] hsItemsInArray = new String[] {
		"Penalties",
		"Layers / Casting Spells",
		"Resolving Spells / Copiable Characteristics",
		"Types of Information",
		"Head Judge Announcement",
		"Reviews / Feedback",
		"Oracle",
		"Decklist Counter",
		"Comprehensive Rules",
		"Infraction Penalty Guide",
		"Magic Tournament Rules",
		"Miscellaneous Options"
	};
	private Context ct;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.homescreen);
		this.ct = this;
		hsItems = new ArrayList<String>();
		hsAdapter = new ArrayAdapter<String>(this, R.layout.listview_item, hsItems);
		for(int i=0;i<hsItemsInArray.length;++i) {
			hsItems.add(hsItemsInArray[i]);
		}
		ListView lv = (ListView)findViewById(R.id.homescreenoptions);
		lv.setAdapter(hsAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				navigate(((TextView) arg1).getText().toString());
			}
			
		});
		hsAdapter.notifyDataSetChanged();
		Intent itnt = new Intent(this, InitCardData.class);
		startActivityForResult(itnt, 1);
	}
	
	private void navigate(String dir) {
		int sel = -1;
		Intent reUsable;
		Bundle reuseBundle;
		for(int i=0;i<hsItemsInArray.length;++i) {
			if(hsItemsInArray[i].equals(dir)) {
				sel = i;
			}
		}
		switch (sel) {
		case 0:
			reUsable = new Intent(ct, ImageOnly.class);
			reUsable.putExtra("image", R.drawable.penalties_guide);
			startActivity(reUsable);
			break;
		case 1:
			reUsable = new Intent(ct, ImageOnly.class);
			reUsable.putExtra("image", R.drawable.layers_castingspells);
			startActivity(reUsable);
			break;
		case 2:
			reUsable = new Intent(ct, ImageOnly.class);
			reUsable.putExtra("image", R.drawable.resolvespells_copiable);
			startActivity(reUsable);
			break;
		case 3:
			reUsable = new Intent(ct, ImageOnly.class);
			reUsable.putExtra("image", R.drawable.information_details);
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
			reUsable = new Intent(ct, MainActivity.class);
			startActivity(reUsable);
			break;
		case 7:
			reUsable = new Intent(ct, DecklistCounter.class);
			startActivity(reUsable);
			break;
		case 8:
			reUsable = new Intent(ct, ComprehensiveRules.class);
			startActivity(reUsable);
			break;
		case 9:
			reUsable = new Intent(ct, InfractionPenalty.class);
			startActivity(reUsable);
			break;
		case 10:
			reUsable = new Intent(ct, TournamentRules.class);
			startActivity(reUsable);
			break;
		case 11:
			reUsable = new Intent(ct, MiscOptions.class);
			startActivity(reUsable);
			break;
		default:
			break;
		}
	}
}
