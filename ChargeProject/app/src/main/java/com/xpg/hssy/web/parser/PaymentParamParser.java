package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.PaymentParam;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年06月18日
 * @version 2.0.0
 */

public class PaymentParamParser implements WebResponseParser<PaymentParam> {
	@Override
	public void parse(WebResponse<PaymentParam> webResponse) {
		String json = webResponse.getResult();
		PaymentParam paymentParam = GsonUtil.createSecurityGson().fromJson(json,
				PaymentParam.class);
		webResponse.setResultObj(paymentParam);
	}
}
