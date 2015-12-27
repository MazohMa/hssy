package com.xpg.hssy.bean;

import com.xpg.hssy.db.pojo.Pile;

public class PileState {
	private String isFree;

	public String getIsFree() {
		return isFree;
	}

	public void setIsFree(String isFree) {
		this.isFree = isFree;
	}

	public Pile getPile() {
		return pile;
	}

	public void setPile(Pile pile) {
		this.pile = pile;
	}

	private Pile pile;
}
