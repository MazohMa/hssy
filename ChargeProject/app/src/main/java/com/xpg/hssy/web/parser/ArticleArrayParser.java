package com.xpg.hssy.web.parser;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.bean.Article;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BJ
 * @version 1.0.0
 * @Description
 * @createDate 2015年10月15日
 */

public class ArticleArrayParser implements WebResponseParser<List<Article>> {
	@Override
	public void parse(WebResponse<List<Article>> webResponse) {
		String json = webResponse.getResult();
		List<Article> articles = GsonUtil.createSecurityGson().fromJson(json, new TypeToken<ArrayList<Article>>() {
				}.getType());
		for(Article article:articles){
			article.filterLikes();
		}
		webResponse.setResultObj(articles);
	}
}
