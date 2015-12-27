package com.xpg.hssy.bean;

public class PileDisplayQuick {
	/*
	 * 快捷发布demo
	 */
	private String dianzhuangName;
	private Type type;// 判断快捷发布类型

	public enum Type {
		DISPLAY, NONEDISPLAY
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getDianzhuangName() {
		return dianzhuangName;
	}

	public void setDianzhuangName(String dianzhuangName) {
		this.dianzhuangName = dianzhuangName;
	}

}
