package com.ndrue.gathereroffline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBAdapter {
	private static final String KEY_ROWID = "_id";
	private static final String TAG = "DBAdapter";

	private static final String DATABASE_NAME = "GathererOfflineDB.s3db";
	private static final String DATABASE_TABLE = "OracleData";
	private static final int DATABASE_VERSION = 1;

	private final Context context;

	private static String tbname;
	private static String headers;
	private static String types;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	private static final String pid = "DBAdapter";

	public String getTB() {
		return this.tbname;
	}

	public String getHeaders() {
		return this.headers;
	}

	public String getTypes() {
		return this.types;
	}

	public DBAdapter(Context ctx, String tbname, String headers, String types) {
		this.context = ctx;
		this.tbname = tbname;
		this.headers = headers;
		this.types = types;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String concat = "";
			String[] headerArr = headers.split(",");
			String[] typeArr = types.split(",");
			for (int i = 0; i < headerArr.length; ++i) {
				concat = concat + (concat.equals("") ? "" : ",") + "`"
						+ headerArr[i] + "` " + typeArr[i];
			}
			db.execSQL("CREATE TABLE IF NOT EXISTS " + tbname + " (" + concat
					+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + tbname);
			onCreate(db);
		}
	}

	public DBAdapter open() throws SQLException {
		try {
		db = DBHelper.getWritableDatabase();
		} catch (Exception e) {
			Log.w(pid, e.toString());
		}
		return this;
	}

	public void close() {
		DBHelper.close();
	}

	public long insert(String tbname, String[] headers, String[] vals) {
		ContentValues cv = new ContentValues();
		for (int i = 0; i < headers.length; ++i) {
			Log.w("DatabaseMsg", "Inserting: `" + headers[i] + "` as `" + vals[i] + "`");
			cv.put(headers[i], vals[i]);
		}
		while(db.isDbLockedByCurrentThread() || db.isDbLockedByOtherThreads()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!db.isOpen()) {
			open();
		}
		return db.insert(tbname, null, cv);
	}

	public void dropTable(String tbname) {
		db.execSQL("DROP TABLE IF EXISTS " + tbname);
	}

	public void createTable(String tbname, String headers, String types) {
		String concat = "";
		String[] headerArr = headers.split(",");
		String[] typeArr = types.split(",");
		for (int i = 0; i < headerArr.length; ++i) {
			concat = concat + (concat.equals("") ? "" : ",") + "`"
					+ headerArr[i] + "` " + typeArr[i];
		}
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tbname + " (" + concat
				+ ");");
	}

	public Cursor rawQuery(String rq, String[] args) {
		Cursor c = db.rawQuery(rq, args);
		return c;
	}
	
	public Cursor rawQuery(String rq) {
		return rawQuery(rq, null);
	}
	
	public Cursor query(String tbname, String cols) {
		return query(tbname, cols, "", null, null, null, null, -1);
	}

	public Cursor query(String tbname, String[] cols) {
		return query(tbname, cols, "", null, null, null, null, -1);
	}

	public Cursor query(String tbname, String cols, String sel) {
		return query(tbname, cols, sel, null, null, null, null, -1);
	}

	public Cursor query(String tbname, String[] cols, String sel) {
		return query(tbname, cols, sel, null, null, null, null, -1);
	}

	public Cursor query(String tbname, String cols, String sel, String[] selArgs) {
		return query(tbname, cols, sel, selArgs, "", "", "", -1);
	}

	public Cursor query(String tbname, String[] cols, String sel,
			String[] selArgs) {
		return query(tbname, cols, sel, selArgs, "", "", "", -1);
	}

	public Cursor query(String tbname, String cols, String sel,
			String[] selArgs, String groupBy) {
		return query(tbname, cols, sel, selArgs, groupBy, "", "", -1);
	}

	public Cursor query(String tbname, String[] cols, String sel,
			String[] selArgs, String groupBy) {
		return query(tbname, cols, sel, selArgs, groupBy, "", "", -1);
	}

	public Cursor query(String tbname, String cols, String sel,
			String[] selArgs, String groupBy, String having) {
		return query(tbname, cols, sel, selArgs, groupBy, having, "", -1);
	}

	public Cursor query(String tbname, String[] cols, String sel,
			String[] selArgs, String groupBy, String having) {
		return query(tbname, cols, sel, selArgs, groupBy, having, "", -1);
	}

	public Cursor query(String tbname, String cols, String sel,
			String[] selArgs, String groupBy, String having, String orderBy,
			int limit) {
		return query(tbname, new String[] { cols }, sel, selArgs, groupBy,
				having, orderBy, limit);
	}

	public Cursor query(String tbname, String[] cols, String sel,
			String[] selArgs, String groupBy, String having, String orderBy,
			int limit) {
		Cursor c = db.query(true, tbname, cols, sel, selArgs, groupBy, having,
				orderBy, (limit == -1 ? null : (limit + "")));
		return c;
	}

	public Cursor query(String tbname, String[] cols, String sel,
			String[] selArgs, String groupBy, String having, String orderBy,
			String limit) {
		Cursor c = db.query(true, tbname, cols, sel, selArgs, groupBy, having,
				orderBy, limit);
		return c;
	}
	
	public boolean update(String tbname, String[] headers, String[] vals,
			String condition) {
		ContentValues args = new ContentValues();
		for (int i = 0; i < headers.length; ++i) {
			args.put(headers[i], vals[i]);
		}
		return db.update(tbname, args, condition, null) > 0;
	}

	public SQLiteStatement prepare(String sqlstmt) {
		SQLiteStatement stmt = db.compileStatement(sqlstmt);
		return stmt;
	}

	public boolean execPrepare(SQLiteStatement stmt, String[] vals) {
		boolean success = false;
		for (int i = 1; i <= vals.length; ++i) {
			stmt.bindString(i, vals[i - 1].trim());
		}
		try {
			stmt.execute();
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	public void closePrepare(SQLiteStatement stmt) {
		stmt.close();
		close();
	}
}