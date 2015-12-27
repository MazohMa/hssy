package com.xpg.hssy.adapter;

import java.util.ArrayList;
import java.util.List;

import android.widget.BaseAdapter;

public class AdapterListConstance {
	private static AdapterListConstance instacne;
	private static List<BaseAdapter> adapters;

	private AdapterListConstance() {
	}

	public static AdapterListConstance getInstance() {
		if (instacne == null) {
			instacne = new AdapterListConstance();
			adapters = new ArrayList<BaseAdapter>();
		}
		return instacne;
	}

	public void addAdapter(BaseAdapter adapter) {
		adapters.add(adapter);
	}

	public BaseAdapter getAdapter() {
		return adapters.get(0);
	}
}
