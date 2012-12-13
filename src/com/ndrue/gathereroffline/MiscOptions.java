package com.ndrue.gathereroffline;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MiscOptions extends Activity {

	private Context ct;
	private static String DB_PATH = "";
	private static String DB_NAME = "GathererOfflineDB.s3db";

	private AlertDialog.Builder alert;
	private final String pid = "MiscOptions";
	private EditText input;
	private ArrayAdapter<String> hsAdapter;
	private final String[] optList = new String[] {
			"Check for database updates", "Set update location",
			"Clear stored card images" };
	private DBAdapter dba;
	private final String latestFile = "getlatest.php";
	private final String downloadFile = "getlatestfile.php";
	private String getLength = "";
	private String uri = "";
	private getUpdates getUp = null;
	private startDownload startDwn = null;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.homescreen);
		ct = this;
		DB_PATH = ct.getDatabasePath(DB_NAME).getAbsolutePath();
		initVar();
	}

	private void initVar() {
		dba = new DBAdapter(this, "MiscOpt", "optname, optvalue", "text,text");
		initListView();
		buildAlert();
	}

	private void initListView() {
		ArrayList<String> hsItems = new ArrayList<String>();
		hsAdapter = new ArrayAdapter<String>(this, R.layout.listview_item,
				optList);
		for (int i = 0; i < optList.length; ++i) {
			hsItems.add(optList[i]);
		}
		ListView lv = (ListView) findViewById(R.id.homescreenoptions);
		lv.setAdapter(hsAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				optionClicked(((TextView) arg1).getText().toString());
			}

		});
		hsAdapter.notifyDataSetChanged();
	}

	private void optionClicked(String opt) {
		if (opt.equals(optList[0])) {
			checkUpdates();
		}
		if (opt.equals(optList[1])) {
			setURL();
		}
		if (opt.equals(optList[2])) {
			clearAll();
		}
	}

	private void clearAll() {
		AlertDialog.Builder aD = new AlertDialog.Builder(ct);
		aD.setTitle("Confirm delete");
		aD.setMessage("Confirm deletion of all stored card images?");

		aD.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				yesDelete();
			}
		});

		aD.setNegativeButton("No", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.dismiss();
			}
		});
		aD.show();
	}

	private void yesDelete() {
		File f = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/mtgjudge/images");
		File[] innerF = f.listFiles();
		boolean deleteSuccess = true;
		if (innerF != null) {
			for (int i = 0; i < innerF.length; ++i) {
				if (!innerF[i].delete()) {
					deleteSuccess = false;
				}
			}
		}
		if(deleteSuccess) {
			f.delete();
			Toast.makeText(ct, "Clearing was successful", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(ct, "An error has occurred while clearing.\nPlease try again.", Toast.LENGTH_SHORT).show();
		}
	}

	private class getUpdatesCancelled implements OnCancelListener {

		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			if (getUp != null) {
				if (!getUp.isCancelled()
						&& getUp.getStatus() == AsyncTask.Status.RUNNING) {
					getUp.cancel(true);

				}
			}
		}

	}

	protected class getUpdates extends AsyncTask<String, String, String> {

		ProgressDialog pD;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pD = new ProgressDialog(MiscOptions.this);
			pD.setCancelable(true);
			pD.setTitle("Checking...");
			pD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pD.setMessage("Contacting site...");
			pD.setOnCancelListener(new getUpdatesCancelled());
			pD.show();
		}

		@Override
		protected String doInBackground(String... arg0) {

			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			// TODO Auto-generated method stub
			dba.open();
			Cursor c = null;
			boolean crashed = false;
			try {
				c = dba.query("MiscOpt", "optvalue", "optname = ?",
						new String[] { "updatesite" });
				// startManagingCursor(c);
			} catch (Exception e) {
				crashed = true;
			}
			if (!c.moveToFirst()) {
				crashed = true;
			}
			if (crashed) {
				publishProgress("An error has occurred loading.\nPlease try again, or reinstall the application if all else fails.");
				dba.close();
				finish();
			}
			uri = c.getString(0);
			if (c != null) {
				c.close();
			}
			dba.close();
			DefaultHttpClient htc = new DefaultHttpClient();
			htc.setParams(httpParameters);
			boolean success = false;
			String responseString = "";
			try {
				HttpResponse htr = htc.execute(new HttpGet(uri + "/"
						+ latestFile));
				StatusLine statusLine = htr.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					htr.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
					// htr.getEntity().getContent().close();
					success = true;
					// ..more logic
				} else {
					// Closes the connection.
					htr.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				publishProgress("There was an error retrieving data.\nPlease try again later.");
				e.printStackTrace();
			}
			if (success) {
				Log.w(pid, "Got http: " + responseString);
				dba.open();
				String[] getDetails = responseString.split("\\r?\\n");
				if (c != null) {
					c.close();
				}
				c = null;
				Log.w(pid, "Checking value: " + getDetails[0]);
				c = dba.query("MiscOpt", "COUNT(*)",
						"optname = ? AND optvalue < ?", new String[] {
								"lastdt", getDetails[0] });
				// startManagingCursor(c);
				c.moveToFirst();
				int getCt = c.getInt(0);
				Log.w(pid, "Got count: " + getCt);
				c.close();
				dba.close();
				if (getCt > 0) {
					return getDetails[1];
				} else {
					publishProgress("Current database is the latest. No need for updates.");
				}
			}
			return "";
		}

		@Override
		protected void onProgressUpdate(String... arg0) {
			super.onProgressUpdate(arg0);
			if (!this.isCancelled()) {
				Toast.makeText(ct, arg0[0], Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onPostExecute(String res) {
			pD.dismiss();
			Log.w(pid, "Post Execute: " + res);
			if (!res.equals("") && !this.isCancelled()) {
				getLength = res;
				// download file
				confirmDownload();
			}
		}
	}

	private class cancelDownload implements OnCancelListener {

		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			if (startDwn != null) {
				if (!startDwn.isCancelled()
						&& startDwn.getStatus() == AsyncTask.Status.RUNNING) {
					startDwn.cancel(true);
					File f = new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/mtgjudge/tempdatabase.data");
					f.delete();
				}
			}
		}

	}

	protected class startDownload extends AsyncTask<String, Integer, String> {

		private int filesize = -1;
		private ProgressDialog pD;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				filesize = Integer.parseInt(getLength);
				pD = new ProgressDialog(ct);
				pD.setTitle("Downloading");
				pD.setCancelable(true);
				pD.setMessage("Downloading database...please be patient...");
				pD.setMax(filesize);
				pD.setProgress(0);
				pD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pD.setOnCancelListener(new cancelDownload());
				pD.show();
			} catch (Exception e) {
				Toast.makeText(
						ct,
						"An error has occurred during download.\nPlease try again later.",
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected String doInBackground(String... params) {

			// TODO Auto-generated method stub
			if (filesize > 0) {
				try {
					URL url = new URL(uri + "/" + downloadFile);
					URLConnection connection = url.openConnection();
					connection.setConnectTimeout(3000);
					connection.connect();
					// this will be useful so that you can show a typical 0-100%
					// progress bar
					int fileLength = connection.getContentLength();

					// download the file
					File toCreateDir = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mtgjudge/");
					// have the object build the directory structure, if needed.
					toCreateDir.mkdirs();
					InputStream input = new BufferedInputStream(
							url.openStream());
					OutputStream output = new FileOutputStream(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mtgjudge/tempdatabase.data");

					byte data[] = new byte[1024];
					long total = 0;
					int count;
					while ((count = input.read(data)) != -1
							&& !this.isCancelled()) {
						total += count;
						// publishing the progress....
						publishProgress((int) (total));
						output.write(data, 0, count);
					}

					output.flush();
					output.close();
					input.close();

					if (!this.isCancelled()) {
						String outFileName = DB_PATH;// + DB_NAME;
						// Open the empty db as the output stream
						OutputStream myOutput = new FileOutputStream(
								outFileName);
						// transfer bytes from the inputfile to the outputfile
						InputStream myInput = new BufferedInputStream(
								new FileInputStream(Environment
										.getExternalStorageDirectory()
										.getAbsolutePath()
										+ "/mtgjudge/tempdatabase.data"));
						byte[] buffer = new byte[1024];
						int length;
						while ((length = myInput.read(buffer)) > 0) {
							myOutput.write(buffer, 0, length);
						}
						myInput.close();
						Log.i("CopyDB", "Updating database...done");
						// Close the streams
						myOutput.flush();
						myOutput.close();
						deleteDirectory(new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/mtgjudge"));
					}
				} catch (Exception e) {
					e.printStackTrace();
					return "ERROR";
				}
			}
			return "";
		}

		@Override
		protected void onProgressUpdate(Integer... arg0) {
			super.onProgressUpdate(arg0);
			if (!this.isCancelled()) {
				pD.setProgress(arg0[0]);
			}
		}

		@Override
		protected void onPostExecute(String res) {
			super.onPostExecute(res);
			pD.dismiss();
			if (res.equals("ERROR") && !this.isCancelled()) {
				Toast.makeText(
						ct,
						"An error has occurred during download.\nPlease try again later.",
						Toast.LENGTH_LONG).show();
			} else {
				if (!this.isCancelled()) {
					Toast.makeText(ct,
							"Database has been successfully updated",
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (path.delete());
	}

	private void confirmDownload() {
		AlertDialog.Builder aD = new AlertDialog.Builder(ct);
		aD.setTitle("Confirm download");
		aD.setMessage("Your database file is not the latest.\nDo you wish to update from the location?\n\nPlease note that the update might take about 10 minutes or more depending on your Internet connection");

		aD.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				startDwn = new startDownload();
				startDwn.execute();
			}
		});

		aD.setNegativeButton("No", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
		aD.show();
	}

	private void checkUpdates() {
		getUp = new getUpdates();
		getUp.execute("", "", "");

	}

	private void setURL() {
		dba.open();
		Cursor c = null;
		boolean crashed = false;
		try {
			c = dba.query("MiscOpt", "optvalue", "optname = ?",
					new String[] { "updatesite" });
		} catch (Exception e) {
			dba.close();
			crashed = true;
		} finally {
			if (c != null && crashed) {
				c.close();
			}
		}
		if (!c.moveToFirst()) {
			crashed = true;
		}
		if (crashed) {
			Toast.makeText(
					this,
					"An error has occurred loading.\nPlease try again, or reinstall the application if all else fails.",
					Toast.LENGTH_LONG).show();
			dba.close();
			finish();
		}
		String getVal = c.getString(0);
		input = new EditText(this);
		input.setText(getVal);
		c.close();
		dba.close();
		alert.setView(input);
		alert.show();
	}

	private void buildAlert() {
		alert = new AlertDialog.Builder(this);

		alert.setTitle("Update Location:");
		alert.setMessage("Enter location to check and obtain updates from.\nDo not edit if you are unsure!");

		// Set an EditText view to get user input

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				dba.open();
				dba.update("MiscOpt", new String[] { "optvalue" },
						new String[] { value }, "optname = 'updatesite'");
				dba.close();
				dialog.cancel();
				Log.w(pid, "Got: " + value);
				// Do something with value!
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						dialog.cancel();
						Log.w(pid, "Cancelled");
					}
				});

		alert.setNeutralButton("Default",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						dba.open();
						dba.update(
								"MiscOpt",
								new String[] { "optvalue" },
								new String[] { "http://kyo.kuiki.net/mtgjudge" },
								"optname = 'updatesite'");
						dba.close();
						dialog.cancel();
						Log.w(pid, "Default");
					}
				});
	}
}
