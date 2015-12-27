package com.xpg.hssy.web.parser;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.db.pojo.ExpenseRecord;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRecordsParser implements WebResponseParser<List<ExpenseRecord>> {
	@Override
	public void parse(WebResponse<List<ExpenseRecord>> webResponse) {
		String json = webResponse.getResult();
		List<ExpenseRecord> expenseRecord = GsonUtil.createSecurityGson().fromJson(json, new TypeToken<ArrayList<ExpenseRecord>>() {
		}.getType());
		webResponse.setResultObj(expenseRecord);
	}
}
