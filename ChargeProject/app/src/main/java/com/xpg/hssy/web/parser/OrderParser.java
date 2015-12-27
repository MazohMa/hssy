package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月13日
 * @version 1.0.0
 */

public class OrderParser implements WebResponseParser<ChargeOrder> {
	@Override
	public void parse(WebResponse<ChargeOrder> webResponse) {
		String json = webResponse.getResult();
		ChargeOrder order = GsonUtil.createSecurityGson().fromJson(json, ChargeOrder.class);
		webResponse.setResultObj(order);
	}
}
