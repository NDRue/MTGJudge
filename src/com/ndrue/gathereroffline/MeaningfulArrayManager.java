package com.ndrue.gathereroffline;

import java.util.ArrayList;

public class MeaningfulArrayManager {

	private ArrayList<String> arrNames;
	private ArrayList<ArrayList<Object>> arrStored;
	
	public MeaningfulArrayManager() {
		arrNames = new ArrayList<String>();
		arrStored = new ArrayList<ArrayList<Object>>();
	}
	
	public void array(String arrName) {
		if(!arrNames.contains(arrName)) {
			ArrayList<Object> newArr = new ArrayList<Object>();
			arrNames.add(arrName);
			arrStored.add(newArr);
		}
	}
	
	private int getPos(String arrName) {
		int toRet = -1;
		if(!arrNames.contains(arrName)) {
			ArrayList<Object> newArr = new ArrayList<Object>();
			arrNames.add(arrName);
			arrStored.add(newArr);
		}
		toRet = arrNames.indexOf(arrName);
		return toRet;
	}
	
	public void set(String arrName, Object obj) {
		int pos = getPos(arrName);
		ArrayList<Object> mArr = arrStored.get(pos);
		mArr.add(obj);
		arrStored.set(pos, mArr);
	}
	
	public Object get(String arrName, int pos2) {
		int pos = getPos(arrName);
		ArrayList<Object> mArr = arrStored.get(pos);
		return mArr.get(pos2);
	}
	
	public void remove(String arrName, int pos2) {
		int pos = getPos(arrName);
		ArrayList<Object> mArr = arrStored.get(pos);
		mArr.remove(pos2);
		arrStored.set(pos, mArr);
	}
	
	public void clear(String arrName) {
		int pos = getPos(arrName);
		arrNames.remove(pos);
		arrStored.remove(pos);
	}
	
	public String[] getNames() {
		int ttl = arrNames.size();
		String[] toRet = new String[ttl];
		for(int i=0;i<ttl;++i) {
			toRet[i] = arrNames.get(i);
		}
		return toRet;
	}
}
