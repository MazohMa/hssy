package com.xpg.hssy.web.parser;

import com.xpg.hssy.db.pojo.PileStation;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Jason Law
 * @createDate 2015年10月12日
 * @version 2.4.0
 */

public class PileStationParser implements WebResponseParser<PileStation> {
	@Override
	public void parse(WebResponse<PileStation> webResponse) {
		String json = webResponse.getResult();
		PileStation pileStation = GsonUtil.createSecurityGson().fromJson(json, PileStation.class);
		if (pileStation.getId() != null)
			webResponse.setResultObj(pileStation);
	}
}
