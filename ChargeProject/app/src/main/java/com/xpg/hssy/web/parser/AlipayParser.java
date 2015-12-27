package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.Alipay;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年01月13日
 * @version 1.0.0
 */

public class AlipayParser implements WebResponseParser<Alipay> {
	@Override
	public void parse(WebResponse<Alipay> webResponse) {
		String json = webResponse.getResult();
		Alipay alipay = GsonUtil.createSecurityGson().fromJson(json,
				Alipay.class);
		webResponse.setResultObj(alipay);
	}
}
