package com.xpg.hssy.main.activity.callbackinterface;

import com.xpg.hssy.db.pojo.DistrictData;

public interface ISelectCityOperator {
	void onCitySelected(DistrictData city);

	void onCitySelected(String cityStr);
}
