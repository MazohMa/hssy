package com.xpg.hssy.web.parser;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.bean.CommonDataBean;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by black-Gizwits on 2015/11/18.
 */
public class DKeyValueMapParser implements WebResponseParser<Map<Integer, String>> {
	@Override
	public void parse(WebResponse<Map<Integer, String>> webResponse) {
		String json = webResponse.getResult();
		List<CommonDataBean> commonDataBeanList = GsonUtil.createSecurityGson().fromJson(json, new TypeToken<ArrayList<CommonDataBean>>() {
		}.getType());
		HashMap<Integer, String> dKeyValueMap = new HashMap<>();
		if (commonDataBeanList != null) {
			for (CommonDataBean commonDataBean : commonDataBeanList) {
				dKeyValueMap.put(commonDataBean.getValue(), commonDataBean.getDkey());
			}
		}
		webResponse.setResultObj(dKeyValueMap);
	}
}
