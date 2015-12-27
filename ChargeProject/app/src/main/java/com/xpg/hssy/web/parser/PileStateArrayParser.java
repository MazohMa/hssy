package com.xpg.hssy.web.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.bean.PileState;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseParser;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月13日
 * @version 1.0.0
 */

public class PileStateArrayParser implements WebResponseParser<List<Pile>> {
	private boolean isFiltrateFree = false;

	public PileStateArrayParser() {
	}

	public PileStateArrayParser(boolean isFiltrateFree) {
		this.isFiltrateFree = isFiltrateFree;
	}

	@Override
	public void parse(WebResponse<List<Pile>> webResponse) {
		String json = webResponse.getResult();
		List<PileState> pileStates = GsonUtil.createSecurityGson().fromJson(
				json, new TypeToken<ArrayList<PileState>>() {
				}.getType());
		if (pileStates == null) {
			return;
		}
		List<Pile> piles = new ArrayList<>(pileStates.size());

		if (isFiltrateFree) {
			for (PileState pileState : pileStates) {
				// 处理非空闲桩,处理脏数据,移除
				if (pileState.getIsFree() == "1"
						&& pileState.getPile().getPileId() != null
						&& pileState.getPile().getType() != null) {
					piles.add(pileState.getPile());
				}
			}
		} else {
			for (PileState pileState : pileStates) {
				// 处理脏数据,移除
				if (pileState.getPile().getPileId() != null
						&& pileState.getPile().getType() != null) {
					piles.add(pileState.getPile());
				}
			}
		}
		webResponse.setResultObj(piles);
	}
}
