package com.xpg.hssy.web.parser;

import com.xpg.hssy.bean.PileAuthentication;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**  PileAuthenticationParser
 * @Description
 * @author Mazoh
 * @createDate 2015年10月10日
 * @version 2.4
 */

public class PileAuthenticationParser implements WebResponseParser<PileAuthentication> {
	@Override
	public void parse(WebResponse<PileAuthentication> webResponse) {
		String json = webResponse.getResult();
		PileAuthentication pileAuthentication = GsonUtil.createSecurityGson().fromJson(json,
				PileAuthentication.class);
		webResponse.setResultObj(pileAuthentication);
	}
}
