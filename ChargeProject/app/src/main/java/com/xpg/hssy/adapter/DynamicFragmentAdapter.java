package com.xpg.hssy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xpg.hssy.bean.DynamicInfo;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.pojo.Favorite;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.main.activity.WebViewNewsActivity;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssychargingpole.BuildConfig;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mazoh
 * @version 2.4.0
 * @description
 * @create 2015年9月16日
 */

public class DynamicFragmentAdapter extends EasyAdapter<DynamicInfo> {

    private final String WEB_PREFIX = BuildConfig.BASE_URL+"/news/view?infoid=";

    private Context context;
    private boolean imageLoadable = true;
    private DisplayImageOptions option;
    private LoadingDialog loadingDialog;
    private List<Favorite> favorites = new ArrayList<>();
    private Favorite favorite;
    private String user_id;

    {
        option = ImageLoaderManager.createDisplayOptionsWtichImageResurces(R.drawable.find_sanyoubg, R.drawable.find_sanyoubg, R.drawable.find_sanyoubg);
    }

    public DynamicFragmentAdapter(Context context, List<DynamicInfo> items) {
        super(context, items);
        this.context = context;
        user_id = context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("user_id", "");
    }

    @Override
    protected ViewHolder newHolder() {
        return new ViewHolder() {
            private ImageView iv_pic;
            private TextView tv_title, tv_content, tv_time, proud_num;

            @Override
            protected void update() {
                DynamicInfo info = get(position);
//                iv_pic.setImageURI(Uri.parse(info.getCoverImg()));
                tv_title.setText(info.getTitle());
                tv_content.setText(info.getContent());

                tv_time.setText(TimeUtil.format(Long.parseLong(info.getIssueTime()), "yyyy-MM-dd"));
                proud_num.setText(info.getViewCount());
                if (imageLoadable) {
                    String coverCropImg = info.getCoverImg();
                    String url = coverCropImg;
                    ImageLoaderManager.getInstance().displayImage(url, iv_pic, option);
                    //TODO 增加桩主头像
                }
            }

            @Override
            protected View init(LayoutInflater arg0) {
                View root = arg0.inflate(R.layout.fragment_dynamic_content_item, null);
                iv_pic = (ImageView) root.findViewById(R.id.iv_pic);
                tv_title = (TextView) root.findViewById(R.id.tv_title);
                tv_content = (TextView) root.findViewById(R.id.tv_content);
                tv_time = (TextView) root.findViewById(R.id.tv_time);
                proud_num = (TextView) root.findViewById(R.id.proud_num);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DynamicInfo info = get(position);
                        Intent data = new Intent(context, WebViewNewsActivity.class);
                        String link = WEB_PREFIX + info.getId();
                        data.putExtra(KEY.INTENT.WEB_LINK, link);
                        context.startActivity(data);
                    }
                });
                return root;
            }
        };

    }

    public boolean isImageLoadable() {
        return imageLoadable;
    }

    public void setImageLoadable(boolean imageLoadable) {
        this.imageLoadable = imageLoadable;
    }
}
