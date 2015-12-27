package com.xpg.hssy.web.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.db.pojo.Command;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年01月13日
 * @version 1.0.0
 */

public class CommandArrayParser implements WebResponseParser<List<Command>> {
	@Override
	public void parse(WebResponse<List<Command>> webResponse) {
		String json = webResponse.getResult();
		List<Command> commands = GsonUtil.createSecurityGson().fromJson(json,
				new TypeToken<ArrayList<Command>>() {
				}.getType());
		webResponse.setResultObj(commands);
	}
}
