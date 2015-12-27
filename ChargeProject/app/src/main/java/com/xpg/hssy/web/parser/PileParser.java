package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月13日
 * @version 1.0.0
 */

public class PileParser implements WebResponseParser<Pile> {
	@Override
	public void parse(WebResponse<Pile> webResponse) {
		String json = webResponse.getResult();
		Pile pile = GsonUtil.createSecurityGson().fromJson(json, Pile.class);
		if (pile.getPileId() != null)
			webResponse.setResultObj(pile);
	}
}
