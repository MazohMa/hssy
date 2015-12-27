package com.xpg.hssy.web.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.db.pojo.Record;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mazoh
 * @version 1.0.0
 * @Description
 * @createDate 2015年01月13日
 */

public class OrderArrayParser implements WebResponseParser<List<ChargeOrder>> {
	@Override
	public void parse(WebResponse<List<ChargeOrder>> webResponse) {
		String json = webResponse.getResult();
		JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
		JsonArray ja = jo.getAsJsonArray("orders");
		if (ja == null) {
			return;
		}
		List<ChargeOrder> orders = GsonUtil.createSecurityGson().fromJson(ja.toString(), new TypeToken<ArrayList<ChargeOrder>>() {
		}.getType());
		List<ChargeOrder> dirtyData = new ArrayList<ChargeOrder>();
		Record.RecordSorter recordSorter = new Record.RecordSorter();
		// 处理脏数据,移除
		for (ChargeOrder order : orders) {
			if (order.getAction() == ChargeOrder.ACTION_COMPLETE && order.getChargeList() == null) {
				dirtyData.add(order);
			} else if (order.getChargeList() != null) {
				List<ChargeRecord> crs = order.getChargeList();
				Collections.sort(crs, recordSorter);
				order.setChargeList(crs);
			}
		}
		orders.removeAll(dirtyData);
		webResponse.setResultObj(orders);
	}
}
