package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.BeecloudPaymentParam;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Jason
 * @createDate 2015年09月29日
 * @version 1.0.0
 */

public class BeecloudPaymentParamParser implements WebResponseParser<BeecloudPaymentParam> {
	@Override
	public void parse(WebResponse<BeecloudPaymentParam> webResponse) {
		String json = webResponse.getResult();
		BeecloudPaymentParam paymentParams = GsonUtil.createSecurityGson().fromJson(json,
				BeecloudPaymentParam.class);
		webResponse.setResultObj(paymentParams);
	}
}
