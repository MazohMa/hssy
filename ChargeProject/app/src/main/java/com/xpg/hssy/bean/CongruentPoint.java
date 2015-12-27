package com.xpg.hssy.bean;

import java.util.List;

import com.xpg.hssy.db.pojo.Pile;

public class CongruentPoint {
	private int num;
	private Pile pile;
	private double avgLong;
	private double avgLat;
	private double topRightLat;
	private double topRightLong;
	private double bottomLeftLat;
	private double bottomLeftLong;
	private List<Pile> piles;

	public List<Pile> getPiles() {
		return piles;
	}

	public void setPiles(List<Pile> piles) {
		this.piles = piles;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Pile getPile() {
		return pile;
	}

	public void setPile(Pile pile) {
		this.pile = pile;
	}

	public double getAvgLong() {
		return avgLong;
	}

	public void setAvgLong(double avgLong) {
		this.avgLong = avgLong;
	}

	public double getAvgLat() {
		return avgLat;
	}

	public void setAvgLat(double avgLat) {
		this.avgLat = avgLat;
	}

	public double getTopRightLat() {
		return topRightLat;
	}

	public void setTopRightLat(double topRightLat) {
		this.topRightLat = topRightLat;
	}

	public double getTopRightLong() {
		return topRightLong;
	}

	public void setTopRightLong(double topRightLong) {
		this.topRightLong = topRightLong;
	}

	public double getBottomLeftLat() {
		return bottomLeftLat;
	}

	public void setBottomLeftLat(double bottomLeftLat) {
		this.bottomLeftLat = bottomLeftLat;
	}

	public double getBottomLeftLong() {
		return bottomLeftLong;
	}

	public void setBottomLeftLong(double bottomLeftLong) {
		this.bottomLeftLong = bottomLeftLong;
	}

}
