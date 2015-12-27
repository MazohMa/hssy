package com.xpg.hssy.web.parser;

import com.xpg.hssy.bean.Price;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gunter on 2015/10/29.
 */
public class PriceParser implements WebResponseParser<Price> {

    @Override
    public void parse(WebResponse<Price> webResponse) {
        String json = webResponse.getResult();
        String price;
        Price priceObj = new Price();
        try {
            price = new JSONObject(json).optString("price");
            priceObj.setPrice(price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webResponse.setResultObj(priceObj);
    }
}
