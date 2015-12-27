package com.xpg.hssy.db.pojo;

import java.util.List;

/**
 * Created by Administrator on 2015/10/19.
 */
public class PileList {
	private List<Pile> pileList;
	private int freeNum;
	private int alterNum;
	private int directNum;

	public List<Pile> getPileList() {
		return pileList;
	}

	public void setPileList(List<Pile> pileList) {
		this.pileList = pileList;
	}

	public int getFreeNum() {
		return freeNum;
	}

	public void setFreeNum(int freeNum) {
		this.freeNum = freeNum;
	}

	public int getAlterNum() {
		return alterNum;
	}

	public void setAlterNum(int alterNum) {
		this.alterNum = alterNum;
	}

	public int getDirectNum() {
		return directNum;
	}

	public void setDirectNum(int directNum) {
		this.directNum = directNum;
	}
}
