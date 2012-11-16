package com.ndrue.gathereroffline;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.widget.Toast;

public class FileReader extends Activity {

	private String fname = "";
	private ArrayList<String> dataLines;
	private boolean isRead = false;
	
	public FileReader(String fname) {
		this.fname = fname;
		isRead = false;
		dataLines = new ArrayList<String>();
	}
	
	public boolean readFile() {
		boolean toRet = false;
		try {
			BufferedReader bR = new BufferedReader(new InputStreamReader(
					openFileInput(fname)));
			dataLines = new ArrayList<String>();
			String toRead = "";
			while ((toRead = bR.readLine()) != null) {
				dataLines.add(toRead);
			}
			toRet = true;
			isRead = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toRet;
	}
	
	public ArrayList<String> read() {
		if(isRead) {
			return dataLines;
		} else {
			if(readFile()) {
				return dataLines;
			} else {
				return null;
			}
		}
	}
	
	public ArrayList<String> read(int from) {
		if(!isRead) {
			if(!readFile()) {
				return null;
			}
		}
		ArrayList<String> toRet = new ArrayList<String>();
		for(int i=from;i<dataLines.size();++i) {
			toRet.add(dataLines.get(i));
		}
		return toRet;
	}
	
	public ArrayList<String> read(int from, int to) {
		if(!isRead) {
			if(!readFile()) {
				return null;
			}
		}
		ArrayList<String> toRet = new ArrayList<String>();
		for(int i=from;i<to;++i) {
			toRet.add(dataLines.get(i));
		}
		return toRet;
	}
	
}
