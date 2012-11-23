package com.ndrue.gathereroffline;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DecklistCounter extends Activity {

	private static ArrayList<Integer> steps;
	private static int totalCards = 0;
	private static TextView dispCards;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.decklist_counter);
		steps = new ArrayList<Integer>();
		dispCards = (TextView)findViewById(R.id.deckCount);
		dispCards.setText("Count: 0");
		setEvents();
	}
	
	private void setEvents() {
		Button btn;
		int[] btn_onetofour = new int[4];
		btn_onetofour[0] = R.id.addonebutton;
		btn_onetofour[1] = R.id.addtwobutton;
		btn_onetofour[2] = R.id.addthreebutton;
		btn_onetofour[3] = R.id.addfourbutton;
		for(int i=0;i<4;++i) {
			btn = (Button)findViewById(btn_onetofour[i]);
			btn.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					addCount(Integer.parseInt(((Button)arg0).getText().toString()));
				}
				
			});
		}
		btn = (Button)findViewById(R.id.undobutton);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				undoAction();
			}
		});
		btn = (Button)findViewById(R.id.resetbutton);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetCount();
			}
		});
	}

	private void resetCount() {
		steps = new ArrayList<Integer>();
		totalCards = 0;
		dispCards.setText("Count: 0");
	}
	
	private void undoAction() {
		if(!steps.isEmpty()) {
			int lastAdded = steps.get(steps.size() - 1);
			steps.remove(steps.size() - 1);
			totalCards = totalCards - lastAdded;
			dispCards.setText("Count: " + totalCards);
		}
	}
	
	private void addCount(int toAdd) {
		steps.add(toAdd);
		totalCards = totalCards + toAdd;
		dispCards.setText("Count: " + totalCards + " (+" + toAdd + ")");
	}
	
}
