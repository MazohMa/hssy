package com.xpg.hssy.main.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.easy.adapter.EasyAdapter;
import com.easy.util.EmptyUtil;
import com.xpg.hssy.adapter.MyOrdersAdapter;
import com.xpg.hssy.adapter.MyOrdersAdapter.ItemSelected;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssy.main.activity.MyOrderDetailActivity;
import com.xpg.hssy.main.activity.MyOrderFragmentActivity;
import com.xpg.hssy.main.fragment.callbackinterface.IAppointOperatable;
import com.xpg.hssy.main.fragment.callbackinterface.IAppointOperater;
import com.xpg.hssy.main.fragment.callbackinterface.IMyOrderOperater;
import com.xpg.hssy.main.fragment.callbackinterface.ItemForNoneOrderLayoutOperater;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.DropDownListView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mazoh 我的订单管理--无效订单
 */
@SuppressLint("NewApi")
public class MyOrderUnused extends BaseFragment implements IAppointOperatable,IMyOrderOperater,ItemForNoneOrderLayoutOperater {
    private DropDownListView myorder_lv;
    private MyOrdersAdapter myOrdersAdapter;

    private List<ChargeOrder> orders = new ArrayList<ChargeOrder>();
    private SharedPreferences sp;
    private String user_id;
    private LinearLayout yuyue_charge_layout;
    private Button btn_yuyue_charge;
    private ChargeOrder order;
    private IAppointOperater listen;
    private LoadingDialog loadingDialog = null ;

    public MyOrderUnused() {
    }

    public MyOrdersAdapter getMyOrdersAdapter() {
        return myOrdersAdapter;
    }


    public void setMyOrdersAdapter(MyOrdersAdapter myOrdersAdapter) {
        this.myOrdersAdapter = myOrdersAdapter;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (orders.size() != 0) {
                myorder_lv.onDropDownComplete();
                myOrdersAdapter.clear();
                loadOrders(start, end);
            } else if (orders.size() == 0) {
                loadOrders(start, end);
            }
        } else {
            return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        user_id = sp.getString("user_id", "");

    }

    @Override
    public void onResume() {
        super.onResume();
        // loadOrders();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        myOrdersAdapter.clear();
        loadOrders(start, end);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.myorder_tab, container, false);
        myorder_lv = (DropDownListView) v.findViewById(R.id.myorder_lv);
        yuyue_charge_layout = (LinearLayout) v
                .findViewById(R.id.yuyue_charge_layout);
        yuyue_charge_layout.setVisibility(View.VISIBLE);
        btn_yuyue_charge = (Button) v.findViewById(R.id.btn_yuyue_charge);
        myOrdersAdapter = new MyOrdersAdapter(this,getListen(), getActivity(), orders);
        myorder_lv.setAdapter(myOrdersAdapter);
        myorder_lv.onDropDownComplete();
        myorder_lv.setOnBottomListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myorder_lv.onDropDownComplete();
                loadOrders(start, end);
            }
        });
        myorder_lv.setOnItemClickListener(new MyItemClick());
        myOrdersAdapter.setItemSelected(new MyItemSelected());
        return v;
    }

    private ItemOnclickForSelections mItemOnclickForSelections;

    public ItemOnclickForSelections getmItemOnclickForSelections() {
        return mItemOnclickForSelections;
    }

    public void setmItemOnclickForSelections(
            ItemOnclickForSelections mItemOnclickForSelections) {
        this.mItemOnclickForSelections = mItemOnclickForSelections;
    }

    @Override
    public void itemForNoneLayout() {
        yuyue_charge_layout.setVisibility(View.VISIBLE);
        myorder_lv.setVisibility(View.GONE);
    }

    public interface ItemOnclickForSelections {
        public void myItemForSelected(ArrayList<ChargeOrder> chargeOrders);
    }

    class MyItemSelected implements ItemSelected {

        @Override
        public void cancelCollect(ArrayList<ChargeOrder> chargeOrders) {
            Log.i("cancelCollect", chargeOrders.size() + "");
            if (mItemOnclickForSelections != null) {
                mItemOnclickForSelections.myItemForSelected(chargeOrders);
            }
        }

    }

    class MyItemClick implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (myOrdersAdapter.getMode() == EasyAdapter.MODE_CHECK_BOX) {
                if (myOrdersAdapter.isSelected(position)) {
                    myOrdersAdapter.unselect(position);
                } else {
                    myOrdersAdapter.select(position);
                }
            } else {
                order = myOrdersAdapter.get(position);
                Intent intent = new Intent(getActivity(),
                        MyOrderDetailActivity.class);
//                intent.putExtra("order", order);
                intent.putExtra("orderId",order.getOrderId()) ;
                getActivity().startActivityForResult(intent,
                        MyOrderFragmentActivity.REQUEST_FROM_MY_ORDER_DETAIL);
                getActivity().overridePendingTransition(R.anim.slide_left_in,
                        R.anim.slide_right_out);
            }
        }

    }

    private void initData() {

    }

    int start = -1;
    int end = -1;

    private void loadOrders(int start, int end)

    {
        List<Integer> actionTypes = new ArrayList<Integer>();
        actionTypes.add(ChargeOrder.ACTION_TIMEOUT);
        actionTypes.add(ChargeOrder.ACTION_REJECT);
        actionTypes.add(ChargeOrder.ACTION_CANCEL);
        if (user_id != null && !"".equals(user_id)) {
            WebAPIManager.getInstance().getMyOrder(user_id, null, actionTypes,
                    orders.size(), MyConstant.PAGE_SIZE, start, end,
                    new WebResponseHandler<List<ChargeOrder>>(getActivity()) {

                        @Override
                        public void onStart() {
                            super.onStart();
                            loadingDialog = new LoadingDialog(getActivity(),R.string.loading) ;
                            loadingDialog.showDialog();

                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            loadingDialog.dismiss();
                            TipsUtil.showTips(getActivity(), e);
                        }

                        @Override
                        public void onFailure(
                                WebResponse<List<ChargeOrder>> response) {
                            super.onFailure(response);
                            loadingDialog.dismiss();
                            TipsUtil.showTips(getActivity(), response);
                        }

                        @Override
                        public void onSuccess(
                                WebResponse<List<ChargeOrder>> response) {
                            super.onSuccess(response);
                            loadingDialog.dismiss();
                            List<ChargeOrder> orders = response.getResultObj();
                            if (orders == null
                                    || orders.size() < MyConstant.PAGE_SIZE) {
                                myorder_lv.setHasMore(false);
                                myorder_lv.setShowFooterWhenNoMore(true);
                                myorder_lv.setAutoLoadOnBottom(false);
                            } else {
                                myorder_lv.setHasMore(true);
                                myorder_lv.setShowFooterWhenNoMore(false);
                                myorder_lv.setAutoLoadOnBottom(true);
                            }
                            if (!EmptyUtil.isEmpty(orders)) {

                                yuyue_charge_layout.setVisibility(View.GONE);
                                myOrdersAdapter.add(orders);

                            } else {
                                if (myOrdersAdapter.getCount() <= 0) {

                                    yuyue_charge_layout
                                            .setVisibility(View.VISIBLE);
                                    btn_yuyue_charge
                                            .setOnClickListener(new OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    getActivity().setResult(MainActivity.RESULT_FIND_PILE);
                                                    getActivity().finish();
                                                }
                                            });
                                }
                            }
                            myorder_lv.onBottomComplete();

                        }

                    });

        }
    }

    public String getFragmentName() {
        return "MyOrderUnused";
    }

    public IAppointOperater getListen() {
        return listen;
    }

    public void setListen(IAppointOperater listen) {
        this.listen = listen;
    }

    @Override
    public void refresh(int startOffset, int endOffset) {
        myOrdersAdapter.clear();
        loadOrders(startOffset, endOffset);
    }

    public void hideFindPileBtn(){
        btn_yuyue_charge.setVisibility(View.GONE);
    }

    public void showFindPileBtn(){
        btn_yuyue_charge.setVisibility(View.VISIBLE);
    }
}
