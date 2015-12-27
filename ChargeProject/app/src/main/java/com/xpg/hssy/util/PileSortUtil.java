package com.xpg.hssy.util;

import android.content.Context;

import com.xpg.hssy.db.pojo.Pile;

import java.util.ArrayList;
import java.util.List;

/**
 * @description
 * @author Mazoh
 * @email 471977848@qq.com
 * @create 2015年7月15日
 * @version 2.0.0
 */

public class PileSortUtil {
	/**
	 * 根据桩的角色（主人桩与家人桩），进行排序
	 */
	public static List<Pile> sortByPileAuthority(final Context context,
			List<Pile> piles, String user_id) {
		List<Pile> pilesOwn = new ArrayList<Pile>();
		List<Pile> pilesFamily = new ArrayList<Pile>();
		for (Pile pile : piles) {
			if (pile.getUserid().equals(user_id)) {
				pilesOwn.add(pile);
			} else {
				pilesFamily.add(pile);
			}
		}
		piles.clear() ; 
		piles.addAll(pilesOwn) ;
		piles.addAll(pilesFamily) ;
		pilesOwn = null ;
		pilesFamily = null ;
		return piles;
	}
}
