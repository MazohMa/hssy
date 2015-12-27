package com.xpg.hssy.web.parser;

import com.xpg.hssy.bean.Article;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @author Mazoh
 * @version 2.4.0
 * @Description 优易圈话题解析
 * @createDate 2015年10月14日
 */

public class ArticleParser implements WebResponseParser<Article> {
	@Override
	public void parse(WebResponse<Article> webResponse) {
		String json = webResponse.getResult();
		Article article = GsonUtil.createSecurityGson().fromJson(json, Article.class);
		article.filterLikes();
		webResponse.setResultObj(article);
	}
}
