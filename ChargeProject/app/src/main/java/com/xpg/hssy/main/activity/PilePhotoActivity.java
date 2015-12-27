package com.xpg.hssy.main.activity;

import java.util.List;

import android.view.LayoutInflater;

import com.easy.util.EmptyUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.view.PilePhotoPager;
import com.xpg.hssychargingpole.R;

/**
 * @description
 * @author Joke
 * @email 113979462@qq.com
 * @create 2015年6月4日
 * @version 1.0.0
 */

public class PilePhotoActivity extends BaseActivity {
	@Override
	protected void initUI() {
		super.initUI();

		int imageIndex = getIntent().getIntExtra(KEY.INTENT.IMAGE_INDEX, -1);
		List<String> imageUrls = (List) (getIntent()
				.getSerializableExtra(KEY.INTENT.IMAGE_URLS));

		PilePhotoPager ppp = (PilePhotoPager) LayoutInflater.from(this)
				.inflate(R.layout.activity_pile_photo, null);
		ppp.setCanOperate(true);
		setContentView(ppp);

		if (EmptyUtil.notEmpty(imageUrls)) {
			for (String url : imageUrls) {
				ppp.loadPhoto(url);
			}
		}
		ppp.getAdapter().notifyDataSetChanged();

		if (imageIndex > -1) {
			ppp.setCurrentItem(imageIndex);
		}
	}

}
