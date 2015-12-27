package com.xpg.hssy.web.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月13日
 * @version 1.0.0
 */

public class PileArrayParser implements WebResponseParser<List<Pile>> {
	@Override
	public void parse(WebResponse<List<Pile>> webResponse) {
		String json = webResponse.getResult();
		List<Pile> piles = GsonUtil.createSecurityGson().fromJson(json,
				new TypeToken<ArrayList<Pile>>() {
				}.getType());
		if (piles == null) {
			return;
		}
		Iterator<Pile> it = piles.iterator();
		while (it.hasNext()) {
			if (it.next().getPileId() == null) {
				it.remove();
			}
		}
		webResponse.setResultObj(piles);
	}
}
