package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.ReturnAlipay;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年01月13日
 * @version 1.0.0
 */

public class ReturnAlipayParser implements WebResponseParser<ReturnAlipay> {
	@Override
	public void parse(WebResponse<ReturnAlipay> webResponse) {
		String json = webResponse.getResult();
		ReturnAlipay returnAlipay = GsonUtil.createSecurityGson().fromJson(
				json, ReturnAlipay.class);
		webResponse.setResultObj(returnAlipay);
	}
}
