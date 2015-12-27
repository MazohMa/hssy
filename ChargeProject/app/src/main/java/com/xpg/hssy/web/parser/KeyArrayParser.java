package com.xpg.hssy.web.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.db.pojo.Key;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月13日
 * @version 1.0.0
 */

public class KeyArrayParser implements WebResponseParser<List<Key>> {
	@Override
	public void parse(WebResponse<List<Key>> webResponse) {
		String json = webResponse.getResult();
		List<Key> keys = GsonUtil.createSecurityGson().fromJson(json,
				new TypeToken<ArrayList<Key>>() {
				}.getType());
		webResponse.setResultObj(keys);
	}
}
