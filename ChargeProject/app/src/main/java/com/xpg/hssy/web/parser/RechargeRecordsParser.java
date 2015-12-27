package com.xpg.hssy.web.parser;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.db.pojo.RechargeRecord;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

import java.util.ArrayList;
import java.util.List;

public class RechargeRecordsParser implements WebResponseParser<List<RechargeRecord>> {
	@Override
	public void parse(WebResponse<List<RechargeRecord>> webResponse) {
		String json = webResponse.getResult();
		List<RechargeRecord> rechargeRecords = GsonUtil.createSecurityGson().fromJson(json, new TypeToken<ArrayList<RechargeRecord>>() {
		}.getType());
		webResponse.setResultObj(rechargeRecords);
	}
}
