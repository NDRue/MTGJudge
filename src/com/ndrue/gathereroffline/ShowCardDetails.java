package com.ndrue.gathereroffline;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCardDetails extends Activity{
	
	private String cname = "";
	private final String nl = System.getProperty ("line.separator");
	private final String pid = "ShowCardDetails";
	private String cardID = "";
	private String cardName = "";
	private String castCost = "";
	private Context ct;
	private String responseString = "";
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.card_details);
		ct = this;
		Button btn = (Button)findViewById(R.id.backbutton1);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				exitDetails();
			}
			
		});
		btn = (Button)findViewById(R.id.backbutton2);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				exitDetails();
			}
			
		});
		cname = getIntent().getExtras().getString("searchquery");
		Log.w(pid, "Got card name: " + cname);
		if(cname.length()<3) {
			Toast.makeText(this, "An error has occurred retrieving the card's details. Please try again.", Toast.LENGTH_SHORT).show();
			finish();
		}
		DBAdapter dba = new DBAdapter(this, "GathererCards",
				"cname,ccost,ctype,cpowert,crules,csetrare",
				"text,text,text,text,text,text");
		dba.open();
		Cursor c = dba.query("GathererCards", new String[] { "cname","ccost","ctype","cpowert","crules","csetrare","uid" }, "cname = ?", new String[] { cname } );
		//startManagingCursor(c);
		if(c.moveToFirst()) {
			if(c.getCount()!=1) {
				try {
					c.close();
				} catch (Exception e) {
					Log.w(pid, e.getMessage());
				}
				Toast.makeText(this, "An error has occurred retrieving the card's details. Please try again.", Toast.LENGTH_SHORT).show();
				dba.close();
				finish();
			} else {
				cardID = c.getString(6);
				String toDisp = "";
				cardName = c.getString(c.getColumnIndex("cname"));
				castCost = c.getString(c.getColumnIndex("ccost"));
				toDisp = "Card name:" + nl;
				toDisp = toDisp + c.getString(c.getColumnIndex("cname")) + nl + nl;
				toDisp = toDisp + "Casting cost:" + nl;
				toDisp = toDisp + c.getString(c.getColumnIndex("ccost")) + nl + nl;
				toDisp = toDisp + "Card type:" + nl;
				toDisp = toDisp + c.getString(c.getColumnIndex("ctype")) + nl + nl;
				toDisp = toDisp + "Card text:" + nl;
				toDisp = toDisp + c.getString(c.getColumnIndex("crules")).replace("\n", nl) + nl + nl;
				String powert = c.getString(c.getColumnIndex("cpowert"));
				if(!powert.equals("N/A")) {
					toDisp = toDisp + powert + nl + nl;
				}
				toDisp = toDisp + "Set(s) / Rarity:" + nl;
				toDisp = toDisp + c.getString(c.getColumnIndex("csetrare"));
				((TextView)findViewById(R.id.carddetails)).setText(toDisp);
				dba.close();
				getCardImage();
			}
		}
		try {
			c.close();
		} catch (Exception e) {
			Log.w(pid, e.toString());
		}
		dba.close();
		btn = (Button)findViewById(R.id.downloadImage);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				downloadImage();
			}
			
		});
	}
	
	private void getCardImage() {
		/*File toCreateDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mtgjudgelog/");
		// have the object build the directory structure, if needed.
		toCreateDir.mkdir();
		if(toCreateDir.canWrite()) {
			try {
				OutputStream oS = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mtgjudgelog/logfile.txt");
				oS.write(s.getBytes());
				oS.flush();
				oS.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		File toCreateDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mtgjudge/images/");
		if(toCreateDir.exists()) {
			File toGetFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mtgjudge/images/" + cardID + ".jpg");
			if(toGetFile.exists()) {
				((Button)findViewById(R.id.downloadImage)).setVisibility(View.GONE);
				ImageView iV;
				iV = (ImageView)findViewById(R.id.cardImage);
				iV.setVisibility(View.VISIBLE);
				iV.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mtgjudge/images/" + cardID + ".jpg"));
				iV.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent imageItn = new Intent(ct, ImageOnly.class);
						imageItn.putExtra("imagefile", Environment.getExternalStorageDirectory().getAbsolutePath() + "/mtgjudge/images/" + cardID + ".jpg");
						startActivity(imageItn);
					}
					
				});
			}
		}
	}
	
	private void downloadImage() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}	
		
		if(!mExternalStorageWriteable) {
			Toast.makeText(ct, "Error, external storage not present", Toast.LENGTH_LONG).show();
		} else {
			String gathererQuery = "http://gatherer.wizards.com/Pages/Search/Default.aspx?action=advanced&name=+[";
			gathererQuery = gathererQuery + Uri.encode("m/^" + cardName.replace(" ", "\\s") + "$/") + "]";
			if(!castCost.equals("N/A")) {
				gathererQuery = gathererQuery + "&mana=+=[" + Uri.encode("m/" + castCost + "/") + "]";
			}
			new getHTTPAsync().execute(gathererQuery, "", "");//getHTTPResponse(gathererQuery);
		}
	}
	
	protected class getHTTPAsync extends AsyncTask<String, String, String> {

		ProgressDialog pD;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pD = new ProgressDialog(ct);
			pD.setCancelable(false);
			pD.setTitle("Checking...");
			pD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pD.setMessage("Checking for card on Gatherer...");
			pD.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getHTTPResponse(params[0]);
			} catch (Exception e) {
				return e.getMessage();
			}
			return "";
		}
		
		@Override
		protected void onPostExecute(String res) {
			pD.dismiss();
			if(!res.equals("")) {
				Toast.makeText(ct, res, Toast.LENGTH_LONG).show();
			} else {
				new saveImageAsync().execute(responseString, "", "");
			}
		}
		
	}
	
	private void getHTTPResponse(String q) throws Exception {
		HttpClient htc = new DefaultHttpClient();
		boolean success = false;
		responseString = "";
		try {
			Log.w(pid, "Accessing URL: " + q);
			HttpResponse htr = htc.execute(new HttpGet(q));
			StatusLine statusLine = htr.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				InputStream iSInput = htr.getEntity().getContent();
				BufferedReader bR = new BufferedReader(new InputStreamReader(iSInput));//BuffererdInputStream bIS = new BufferedInputStream
				String toRead = "";
				while((toRead = bR.readLine())!=null) {
					Log.w(pid, "Reading URL: " + toRead);
					if(toRead.contains("id=\"ctl00_ctl00_ctl00_MainContent_SubContent_SubContent_cardImage\"")) {
						responseString = toRead;
						int startPoint = responseString.indexOf("src=\"");
						if(startPoint>0) {
							responseString = responseString.substring(startPoint + 5);
							startPoint = responseString.indexOf("\"");
							responseString = responseString.substring(0, startPoint);
							Log.w(pid, "Got: " + responseString);
							success = true;
						}
						break;
					}
				}
				bR.close();
				iSInput.close();
				try {
					htr.getEntity().getContent().close();
				} catch (Exception e) {
					//do nothing
				}
				// ..more logic
			} else {
				// Closes the connection.
				htr.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e.getMessage());
			//publishProgress("There was an error retrieving data.\nPlease try again later.");
		}
		if(!success) {
			throw new Exception("There was an error retrieving the card details.");
		}
		
	}
	
	protected class saveImageAsync extends AsyncTask<String, String, String> {

		ProgressDialog pD;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pD = new ProgressDialog(ct);
			pD.setCancelable(false);
			pD.setTitle("Downloading...");
			pD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pD.setMessage("Getting image from Gatherer...");
			pD.show();
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				saveImage(arg0[0]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			}
			return "";
		}
		
		@Override
		protected void onPostExecute(String res) {
			if(!res.equals("")) {
				Toast.makeText(ct, "An error has occurred downloading the image. Please try again later", Toast.LENGTH_LONG).show();
			}
			pD.dismiss();
			getCardImage();
		}
		
	}
	
	private void saveImage(String q) throws Exception {
		while(q.contains("../")) {
			int ind = q.indexOf("../");
			q = q.substring(ind + 3);
		}
		q = q.replace("&amp;", "&");
		Log.w(pid, "Trying to save from: http://gatherer.wizards.com/" + q);
		URL url = new URL("http://gatherer.wizards.com/" + q);
		URLConnection connection = url.openConnection();
		connection.connect();
		// this will be useful so that you can show a typical 0-100%
		// progress bar
//		int fileLength = connection.getContentLength();

		// download the file
		File toCreateDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mtgjudge/images/");
		// have the object build the directory structure, if needed.
		toCreateDir.mkdirs();
		InputStream input = new BufferedInputStream(
				url.openStream());
		OutputStream output = new FileOutputStream(
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/mtgjudge/images/" + cardID + ".jpg");

		
		byte data[] = new byte[1024];
		long total = 0;
		int count;
		while ((count = input.read(data)) != -1) {
//			total += count;
			// publishing the progress....
//			publishProgress((int) (total));
			output.write(data, 0, count);
		}

		output.flush();
		output.close();
		input.close();
		
		File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mtgjudge/images/" + cardID + ".jpg");
		if(f.length()<=0) {
			f.delete();
			throw new Exception("File is zero bytes");
		}
	}
	
	private void exitDetails() {
		finish();
	}
}
