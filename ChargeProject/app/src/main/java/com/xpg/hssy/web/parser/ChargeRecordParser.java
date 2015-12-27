package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年10月29日
 * @version 4.0.0
 */

public class ChargeRecordParser implements WebResponseParser<ChargeRecord> {
	@Override
	public void parse(WebResponse<ChargeRecord> webResponse) {
		String json = webResponse.getResult();
		ChargeRecord chargeRecord = GsonUtil.createSecurityGson().fromJson(json,
				ChargeRecord.class);
		webResponse.setResultObj(chargeRecord);
	}
}
