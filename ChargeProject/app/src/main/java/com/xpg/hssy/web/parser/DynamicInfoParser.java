package com.xpg.hssy.web.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.bean.DynamicInfo;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guitian
 * @version 1.0.0
 * @Description
 * @createDate 2015年10月10日
 */
public class DynamicInfoParser implements WebResponseParser<List<DynamicInfo>> {

    @Override
    public void parse(WebResponse<List<DynamicInfo>> webResponse) {
        String json = webResponse.getResult();
        JsonArray ja = new JsonParser().parse(json).getAsJsonArray();
        if (ja == null) {
            return;
        }
        List<DynamicInfo> infoList = GsonUtil.createSecurityGson().fromJson(
                ja.toString(), new TypeToken<ArrayList<DynamicInfo>>() {
                }.getType());
        webResponse.setResultObj(infoList);
    }
}
