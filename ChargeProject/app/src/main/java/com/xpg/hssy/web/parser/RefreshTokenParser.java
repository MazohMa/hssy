package com.xpg.hssy.web.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.xpg.hssy.bean.TokenAndRefreshToken;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年8月13日
 * @version 2.0.0
 */

public class RefreshTokenParser implements WebResponseParser<TokenAndRefreshToken> {
	@Override
	public void parse(WebResponse<TokenAndRefreshToken> webResponse) {
		String json = webResponse.getResult();
		String token = null;
		String refreshToken = null ;
		TokenAndRefreshToken tokenAndRefreshToken = new TokenAndRefreshToken() ;
		try {
			token = new JSONObject(json).optString("token");
			refreshToken = new JSONObject(json).optString("refreshToken");
			tokenAndRefreshToken.setToken(token) ;
			tokenAndRefreshToken.setRefreshToken(refreshToken) ;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		webResponse.setResultObj(tokenAndRefreshToken);
	}
}
