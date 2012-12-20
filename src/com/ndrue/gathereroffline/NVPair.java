package com.ndrue.gathereroffline;

import java.util.ArrayList;

public class NVPair {

	private ArrayList<String> kname;
	private ArrayList<String> kvalue;

	public void NVPair() {
		kname = new ArrayList<String>();
		kvalue = new ArrayList<String>();
	}

	public void add(String n, String v) {
		if (kname == null) {
			kname = new ArrayList<String>();
			kvalue = new ArrayList<String>();
		}
		kname.add(n);
		kvalue.add(v);
	}

	public String get(String n) {
		return kvalue.get(kname.indexOf(n));
	}
}
