package com.xpg.hssy.web.parser;

import com.xpg.hssy.bean.RealTimeStatus;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**  RealTimeStatusParser
 * @Description
 * @author Mazoh
 * @createDate 2015年10月10日
 * @version 2.4
 */

public class RealTimeStatusParser implements WebResponseParser<RealTimeStatus> {
	@Override
	public void parse(WebResponse<RealTimeStatus> webResponse) {
		String json = webResponse.getResult();
		RealTimeStatus realTimeStatus = GsonUtil.createSecurityGson().fromJson(json,
				RealTimeStatus.class);
		webResponse.setResultObj(realTimeStatus);
	}
}
