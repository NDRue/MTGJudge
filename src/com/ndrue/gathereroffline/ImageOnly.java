package com.ndrue.gathereroffline;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ZoomButton;

public class ImageOnly extends Activity {

	private static final String pid = "ImageOnly";
	private boolean zoomed = false;
	private Bitmap bmp = null;
	private Context ct;
	private ZoomImageView imageView = null;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		ct = this;
		Bundle bname = getIntent().getExtras();
		if (bname.containsKey("image")) {
			int imgRes = bname.getInt("image");
			bmp = Bitmap.createBitmap(BitmapFactory.decodeResource(
					this.getResources(), imgRes));
		} else {
			String fname = bname.getString("imagefile");
			bmp = BitmapFactory.decodeFile(fname);
		}
		try {
			imageView = new ZoomImageView(this, getWindow()
					.getWindowManager().getDefaultDisplay().getOrientation());
			imageView.setImage(bmp, this);
			/*
			 * ScrollView scv; scv = new ScrollView(this);
			 * scv.setLayoutParams(new
			 * LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams
			 * .MATCH_PARENT)); scv.addView(imageView);
			 */
			// ((LinearLayout)findViewById(R.id.imageLayout)).addView(imageView);
			// setContentView(scv);
			setContentView(imageView);
			Toast.makeText(this, "Double tap on the image to zoom in or zoom out.\nDrag the image around when zoomed in to view other areas.", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			finish();
		}
		/*
		 * ZoomButton zoombtn = (ZoomButton) findViewById(R.id.zoomButton1);
		 * zoombtn.setOnClickListener(new zoomClicked());
		 */
	}

	private class zoomClicked implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*
			 * bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth(),
			 * bmp.getHeight(), true); ImageView iV = (ImageView)
			 * findViewById(R.id.imageViewHolder); iV.setImageBitmap(bmp);
			 */
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if(imageView!=null && bmp!=null) {
				imageView.setImage(bmp, this);
			}
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			if(imageView!=null && bmp!=null) {
				imageView.setImage(bmp, this);
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) {
			//imageView.setImage(bmp, (Activity)ct);
		}
	}
}
