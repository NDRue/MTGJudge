package com.ndrue.gathereroffline;

import java.util.ArrayList;

public class MeaningfulArray {

	private String arrName;
	private ArrayList arrayObj;
	
	public MeaningfulArray(String arrName) {
		this.arrName = arrName;
		arrayObj = new ArrayList();
	}
	
	public void set(Object obj) {
		arrayObj.add(obj);
	}
	
	public void set(Object obj, int pos) {
		arrayObj.set(pos, obj);
	}
	
	public Object get(int pos) {
		return arrayObj.get(pos);
	}
	
	public int count() {
		return arrayObj.size();
	}
	
	public void reset() {
		arrayObj.clear();
	}
	
}
