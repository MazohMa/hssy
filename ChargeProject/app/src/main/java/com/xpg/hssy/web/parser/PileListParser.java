package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.PileList;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月13日
 * @version 1.0.0
 */

public class PileListParser implements WebResponseParser<PileList> {
	@Override
	public void parse(WebResponse<PileList> webResponse) {
		String json = webResponse.getResult();
		PileList pileList = GsonUtil.createSecurityGson().fromJson(json, PileList.class);
			webResponse.setResultObj(pileList);
	}
}
