package com.ndrue.gathereroffline;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageOnly extends Activity {

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.image_only_layout);
		Bundle bname = getIntent().getExtras();
		try {
			int imgRes = bname.getInt("image");
			ImageView iV = (ImageView) findViewById(R.id.imageViewHolder);
			iV.setImageResource(imgRes);
		} catch (Exception e) {
			finish();
		}
	}
}
