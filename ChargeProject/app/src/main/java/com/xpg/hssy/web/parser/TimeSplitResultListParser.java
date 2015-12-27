package com.xpg.hssy.web.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.bean.DateBookTime;
import com.xpg.hssy.bean.TimeSplitResult;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

public class TimeSplitResultListParser implements
		WebResponseParser<List<DateBookTime>> {

	@Override
	public void parse(WebResponse<List<DateBookTime>> webResponse) {
		String json = webResponse.getResult();
		List<TimeSplitResult> timeSplitResultList = GsonUtil
				.createSecurityGson().fromJson(json,
						new TypeToken<ArrayList<TimeSplitResult>>() {
						}.getType());
		List<DateBookTime> dateBookTimeList = new ArrayList<>(
				timeSplitResultList.size());
		for (TimeSplitResult timeSplitResult : timeSplitResultList) {
			dateBookTimeList.add(timeSplitResult.createDateBookTime());
		}
		webResponse.setResultObj(dateBookTimeList);
	}
}