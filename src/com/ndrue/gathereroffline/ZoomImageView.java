package com.ndrue.gathereroffline;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

/**
 * Custom class to zoom image in android
 * 
 * @author Yash@iotasol.com
 * 
 * @modified for personal use: lostkyo@gmail.com
 */
public class ZoomImageView extends ImageView implements OnGestureListener {

	private static final int SCALING_FACTOR = 100;
	private final int LANDSCAPE = 1;
	private GestureDetector gestureDetector;
	private Drawable image = null;
	private int scalefactor = 0;
	private int orientation;
	private int zoomCtr = 0;
	private long lastTouchTime = 0;
	private int winX, winY, imageX, imageY, scrollX = 0, scrollY = 0, left,
			top, bottom, right;
	private Context ct;

	public ZoomImageView(Context context, int orientation) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		ct = context;
		this.orientation = orientation;
		gestureDetector = new GestureDetector(this);
	}

	public void setImage(Drawable bitmap, Activity activity) {
		image = bitmap;
		imageSetting(activity, bitmap);
	}

	public void setImage(Bitmap bitmap, Activity activity) {
		image = new BitmapDrawable(bitmap).getCurrent();
		imageSetting(activity, bitmap);
	}

	private void imageSetting(Activity a, Drawable b) {
		Bitmap bmp = ((BitmapDrawable) b).getBitmap();
		imageSetting(a, bmp);
	}

	/**
	 * Works in both landscape and potrait mode.
	 */
	private void imageSetting(Activity activity, Bitmap bmp) {
		scrollX = scrollY = 0;
		scalefactor = 0;
		float scaleDown = 1.0f;
		int bmpHt = bmp.getHeight();
		int bmpWd = bmp.getWidth();
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int wwidth = displaymetrics.widthPixels;
		if(bmpWd>wwidth) {
			scaleDown = Float.valueOf(wwidth) / bmpWd;
		}
		if(bmpHt>height && (scaleDown*bmpHt)>height) {
			scaleDown = Float.valueOf(height) / bmpHt;
		}
		imageX = winX = (int)(bmpWd * scaleDown);
		imageY = winY = (int)(bmpHt * scaleDown);
/*		imageX = winX = activity.getWindow().getWindowManager()
				.getDefaultDisplay().getWidth();
		imageY = winY = activity.getWindow().getWindowManager()
				.getDefaultDisplay().getHeight();*/
		if (orientation == LANDSCAPE) {
			imageX = 3 * imageY / 4;
		}
		calculatePos();
	}

	public void calculatePos() {
		int tempx, tempy;
		tempx = imageX + imageX * scalefactor / 100;
		tempy = imageY + imageY * scalefactor / 100;
		left = (winX - tempx) / 2;
		top = (winY - tempy) / 2;
		right = (winX + tempx) / 2;
		bottom = (winY + tempy) / 2;
		invalidate();
	}

	/**
	 * Redraws the bitmap when zoomed or scrolled.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (image == null)
			return;

		image.setBounds(left + scrollX, top + scrollY, right + scrollX, bottom
				+ scrollY);
		image.draw(canvas);
	}

	public void zoomIn() {
		scalefactor += SCALING_FACTOR;
		calculatePos();
	}

	public void zoomOut() {
		if (scalefactor == 0)
			return;
		scrollX = scrollY = 0;
		scalefactor -= SCALING_FACTOR;
		calculatePos();
	}

	public void scroll(int x, int y) {
		scrollX += x / 5;
		scrollY += y / 5;
		if (scrollX + left > 0) {
			scrollX = 0 - left;
		} else if (scrollX + right < winX) {
			scrollX = winX - right;
		}
		if (scrollY + top > 0) {
			scrollY = 0 - top;
		} else if (scrollY + bottom < winY) {
			scrollY = winY - bottom;
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		boolean onTouchEvent = gestureDetector.onTouchEvent(me);
		return onTouchEvent;
	}

	public boolean onDown(MotionEvent arg0) {
		long thisTime = arg0.getEventTime();
		if (thisTime - lastTouchTime < 250) {
			lastTouchTime = -1;
			onDoubleTap();
			return true;
		}
		lastTouchTime = thisTime;
		return true;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (zoomCtr == 0)
			return false;
		scroll((int) (e2.getX() - e1.getX()), (int) (e2.getY() - e1.getY()));
		return true;
	}

	private void onDoubleTap() {
		if (zoomCtr == 0) {
			zoomCtr++;
			zoomIn();
			return;
		}
		zoomCtr--;
		zoomOut();
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}

	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}
}