package com.xpg.hssy.web;

/**
 * Parser
 * 
 * @Description
 * @author Joke Huang
 * @createDate 2014年7月9日
 * @version 1.0.0
 */

public interface WebResponseParser<T> {
	void parse(WebResponse<T> webResponse);
}
