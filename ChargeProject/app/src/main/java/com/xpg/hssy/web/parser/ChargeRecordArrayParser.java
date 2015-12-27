package com.xpg.hssy.web.parser;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.db.pojo.Record;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2015年1月13日
 */

public class ChargeRecordArrayParser implements WebResponseParser<List<ChargeRecord>> {
	@Override
	public void parse(WebResponse<List<ChargeRecord>> webResponse) {
		String json = webResponse.getResult();
		List<ChargeRecord> crs = GsonUtil.createSecurityGson().fromJson(json, new TypeToken<ArrayList<ChargeRecord>>() {
				}.getType());
		if (crs != null) {
			Collections.sort(crs, new Record.RecordSorter());
		}
		webResponse.setResultObj(crs);
	}
}
