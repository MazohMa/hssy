package com.xpg.hssy.web.parser;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.db.pojo.PileScore;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月13日
 * @version 1.0.0
 */

public class PileScoreParser implements WebResponseParser<PileScore> {
	@Override
	public void parse(WebResponse<PileScore> webResponse) {
		String json = webResponse.getResult();
		PileScore pileScore = GsonUtil.createSecurityGson().fromJson(json, new TypeToken<PileScore>() {
				}.getType());
		if (pileScore == null) {
			return;
		}
		webResponse.setResultObj(pileScore);
	}
}
