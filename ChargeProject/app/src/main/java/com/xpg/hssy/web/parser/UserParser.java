package com.xpg.hssy.web.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月13日
 * @version 1.0.0
 */

public class UserParser implements WebResponseParser<User> {
	@Override
	public void parse(WebResponse<User> webResponse) {
		String json = webResponse.getResult();
		JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
		User user = null;
		if (jo.has("user")) {
			String token = jo.get("accessToken").getAsString();
			String refreshToken = jo.get("refreshToken").getAsString();
			// JsonElement je =
			// jo.get("user").getAsJsonObject().remove("avatar");
			// LogUtil.e("debug", je);
			user = GsonUtil.createSecurityGson().fromJson(jo.get("user"),
					User.class);
			user.setToken(token);
			user.setRefreshToken(refreshToken);
		} else {
			user = GsonUtil.createSecurityGson().fromJson(jo, User.class);
		}
		try {
			// user.setPortrait(BitmapUtil.base64ToBitmap(user.getAvatar()));
		} catch (Exception e) {
		}
		webResponse.setResultObj(user);
	}
}
