package com.xpg.hssy.web.parser;

import com.xpg.hssy.bean.CtrlRes;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年11月20日
 * @version 2.4.0
 */

public class CtrlResParser implements WebResponseParser<CtrlRes> {
	@Override
	public void parse(WebResponse<CtrlRes> webResponse) {
		String json = webResponse.getResult();
		CtrlRes ctrlRes = GsonUtil.createSecurityGson().fromJson(json,
				CtrlRes.class);
		webResponse.setResultObj(ctrlRes);
	}
}
