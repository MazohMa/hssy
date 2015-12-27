package com.xpg.hssy.bean;

/**
 * HelpAndSuggestion
 * 
 * @author Mazoh 帮助与反馈
 * 
 */
public class HelpAndSuggestion {
	private String title;
	private String content;

	public HelpAndSuggestion() {

	}

	public HelpAndSuggestion(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
