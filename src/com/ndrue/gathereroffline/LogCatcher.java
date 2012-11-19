package com.ndrue.gathereroffline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Environment;

public class LogCatcher {

	public void LogCatcher() {
	}
	
	//private static String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "data/com.ndrue.gathereroffline/database/";
	
	public void write(String s) {
		File toCreateDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mtgjudgelog/");
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
		}
	}
	
}
