package com.xpg.hssy.web.parser;

import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月13日
 * @version 1.0.0
 */

public class IsFavorParser implements WebResponseParser<Boolean> {
	@Override
	public void parse(WebResponse<Boolean> webResponse) {
		String json = webResponse.getResult();
		boolean b = Boolean.valueOf(json);
		webResponse.setResultObj(b);
	}
}
