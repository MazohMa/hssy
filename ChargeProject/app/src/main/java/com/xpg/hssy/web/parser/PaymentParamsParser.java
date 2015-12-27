package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.PaymentParams;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Jason
 * @createDate 2015年09月29日
 * @version 1.0.0
 */

public class PaymentParamsParser implements WebResponseParser<PaymentParams> {
	@Override
	public void parse(WebResponse<PaymentParams> webResponse) {
		String json = webResponse.getResult();
		PaymentParams paymentParams = GsonUtil.createSecurityGson().fromJson(json,
				PaymentParams.class);
		webResponse.setResultObj(paymentParams);
	}
}
