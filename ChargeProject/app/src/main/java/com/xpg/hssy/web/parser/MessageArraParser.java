package com.xpg.hssy.web.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.bean.Message;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;
/**
 * @Description
 * @author Mazoh
 * @createDate 2015年01月13日
 * @version 1.0.0
 */
public class MessageArraParser implements WebResponseParser<List<Message>> {

	@Override
	public void parse(WebResponse<List<Message>> webResponse) {
		String json = webResponse.getResult();
		List<Message> messages = GsonUtil.createSecurityGson().fromJson(json,
				new TypeToken<ArrayList<Message>>() {
				}.getType());
		webResponse.setResultObj(messages);
	}

}
