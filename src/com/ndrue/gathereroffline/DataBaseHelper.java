package com.ndrue.gathereroffline;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "";
	private static String DB_NAME = "GathererOfflineDB.s3db";
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	/*
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		DB_PATH = context.getDatabasePath(DB_NAME).getAbsolutePath();
		this.myContext = context;
	}

	/*
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();
		Log.i("CopyDB", "DBExist: " + dbExist + " for " + DB_PATH);
		if (dbExist) {
			checkIfValid();
			// do nothing - database already exist
		} else {

			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	private void checkIfValid() {
		Cursor c = null;
		boolean restartNew = false;
		DBAdapter dba = new DBAdapter(myContext, "MiscOpt",
				"optname, optvalue", "text,text");
		try {
			dba.open();
			c = dba.query("MiscOpt", "optname");
			if (!c.moveToFirst()) {
				restartNew = true;
			}
		} catch (Exception e) {
			restartNew = true;
		} finally {
			if (c != null) {
				c.close();
			}
			dba.close();
		}
		if (restartNew) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/*
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH;// + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/*
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 */
	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		Log.i("CopyDB", "Copying database...");
		// Path to the just created empty db
		String outFileName = DB_PATH;// + DB_NAME;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		for (int i = 1; i <= 5; ++i) {
			InputStream myInput = myContext.getAssets().open(
					DB_NAME + ".00" + i);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			myInput.close();
		}
		Log.i("CopyDB", "Copying database...done");
		// Close the streams
		myOutput.flush();
		myOutput.close();
	}

	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH;// + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}