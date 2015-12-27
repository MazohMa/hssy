package com.xpg.hssy.web.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.bean.CongruentPoint;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

public class CongruentPointArrayParser implements
		WebResponseParser<List<CongruentPoint>> {

	@Override
	public void parse(WebResponse<List<CongruentPoint>> webResponse) {
		String json = webResponse.getResult();
		List<CongruentPoint> congruentPoints = GsonUtil.createSecurityGson()
				.fromJson(json, new TypeToken<ArrayList<CongruentPoint>>() {
				}.getType());
		webResponse.setResultObj(congruentPoints);
	}
}
