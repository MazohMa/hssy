package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.BaifubaoParam;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2015年1月13日
 */

public class BaifubaoParamParser implements WebResponseParser<BaifubaoParam> {
	@Override
	public void parse(WebResponse<BaifubaoParam> webResponse) {
		String json = webResponse.getResult();
		BaifubaoParam baifubaoParam = GsonUtil.createSecurityGson().fromJson(json, BaifubaoParam.class);
		webResponse.setResultObj(baifubaoParam);
	}
}
