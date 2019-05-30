package com.ipd.jianghuzuche.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.gyf.barlibrary.ImmersionBar;
import com.ipd.jianghuzuche.R;
import com.ipd.jianghuzuche.base.BaseActivity;
import com.ipd.jianghuzuche.bean.AliPayBean;
import com.ipd.jianghuzuche.bean.ExtendTimeBean;
import com.ipd.jianghuzuche.bean.ExtendTimeListBean;
import com.ipd.jianghuzuche.bean.WeChatPayBean;
import com.ipd.jianghuzuche.common.view.TopView;
import com.ipd.jianghuzuche.contract.ExtendTimeContract;
import com.ipd.jianghuzuche.presenter.ExtendTimePresenter;
import com.ipd.jianghuzuche.utils.ApplicationUtil;
import com.ipd.jianghuzuche.utils.SPUtil;
import com.ipd.jianghuzuche.utils.ToastUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableTransformer;

import static com.ipd.jianghuzuche.common.config.IConstants.CITY;
import static com.ipd.jianghuzuche.common.config.IConstants.REQUEST_CODE_99;
import static com.ipd.jianghuzuche.common.config.IConstants.USER_ID;

/**
 * Description ：延长租期
 * Author ： rmy
 * Email ： 942685687@qq.com
 * Time ： 2019/5/6.
 */
public class ExtendTimeActivity extends BaseActivity<ExtendTimeContract.View, ExtendTimeContract.Presenter> implements ExtendTimeContract.View {

    @BindView(R.id.tv_extend_time_top)
    TopView tvExtendTimeTop;
    @BindView(R.id.tv_extend_time)
    TextView tvExtendTime;
    @BindView(R.id.tv_extend_time_service_time)
    TextView tvExtendTimeServiceTime;
    @BindView(R.id.tv_extend_time_service_charge)
    TextView tvExtendTimeServiceCharge;
    @BindView(R.id.tv_extend_time_late_fee)
    TextView tvExtendTimeLateFee;
    @BindView(R.id.tv_extend_time_power_exchange)
    TextView tvExtendTimePowerExchange;
    @BindView(R.id.tv_use_car_coupon_name)
    TextView tvUseCarCouponName;
    @BindView(R.id.tv_use_car_coupon_money)
    TextView tvUseCarCouponMoney;
    @BindView(R.id.tv_extend_time_money_sum)
    TextView tvExtendTimeMoneySum;
    @BindView(R.id.tv_extend_time_coupon_type)
    TextView tvExtendTimeCouponType;
    @BindView(R.id.ll_extend_time_coupon)
    LinearLayout llExtendTimeCoupon;
    @BindView(R.id.iv_alipay)
    ImageView ivAlipay;
    @BindView(R.id.ll_alipay)
    LinearLayout llAlipay;
    @BindView(R.id.iv_weixin_pay)
    ImageView ivWeixinPay;
    @BindView(R.id.ll_weixin_pay)
    LinearLayout llWeixinPay;
    @BindView(R.id.bt_extend_time)
    Button btExtendTime;
    @BindView(R.id.ll_power_exchange)
    LinearLayout llPowerExchange;
    @BindView(R.id.rb_charging_one)
    RadioButton rbChargingOne;
    @BindView(R.id.rb_charging_second)
    RadioButton rbChargingSecond;
    @BindView(R.id.rb_charging_three)
    RadioButton rbChargingThree;
    @BindView(R.id.tv_extend_time_power_exchange_fee)
    TextView tvExtendTimePowerExchangeFee;
    @BindView(R.id.rg_charging)
    RadioGroup rgCharging;
    @BindView(R.id.ll_extend_time_power_exchange)
    LinearLayout llExtendTimePowerExchange;
    @BindView(R.id.ll_extend_time_late)
    LinearLayout llExtendTimeLate;

    private List<String> listData;
    private OptionsPickerView pvOptions;
    private int orderId = 0;
    private String coupon_name = "";
    private double coupon_money = 0;
    private int couponId = 0;
    private List<ExtendTimeListBean.DataBean.VehicleServicesBean> extendTimeListBean;
    private ExtendTimeBean.DataBean.VehicleListBean ehicleListBean;
    private double lateMoney = 0;
    private double extendTimePowerExchangeFee = 0;
    private int payType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_extend_time;
    }

    @Override
    public ExtendTimeContract.Presenter createPresenter() {
        return new ExtendTimePresenter(this);
    }

    @Override
    public ExtendTimeContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        //将每个Activity加入到栈中
        ApplicationUtil.getManager().addActivity(this);
        //防止状态栏和标题重叠
        ImmersionBar.setTitleBar(this, tvExtendTimeTop);

        orderId = getIntent().getIntExtra("order_id", 0);
        extendTimeListBean = new ArrayList<>();
    }

    @Override
    public void initListener() {
        rgCharging.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_charging_one:
                        llExtendTimePowerExchange.setVisibility(View.VISIBLE);
                        tvExtendTimePowerExchange.setText(extendTimeListBean.get(0).getServicesName());
                        tvExtendTimePowerExchangeFee.setText(extendTimeListBean.get(0).getServicesCost() + "元");
                        extendTimePowerExchangeFee = extendTimeListBean.get(0).getServicesCost();
                        break;
                    case R.id.rb_charging_second:
                        llExtendTimePowerExchange.setVisibility(View.VISIBLE);
                        tvExtendTimePowerExchange.setText(extendTimeListBean.get(1).getServicesName());
                        tvExtendTimePowerExchangeFee.setText(extendTimeListBean.get(1).getServicesCost() + "元");
                        extendTimePowerExchangeFee = extendTimeListBean.get(1).getServicesCost();
                        break;
                    case R.id.rb_charging_three:
                        llExtendTimePowerExchange.setVisibility(View.VISIBLE);
                        tvExtendTimePowerExchange.setText(extendTimeListBean.get(2).getServicesName());
                        tvExtendTimePowerExchangeFee.setText(extendTimeListBean.get(2).getServicesCost() + "元");
                        extendTimePowerExchangeFee = extendTimeListBean.get(2).getServicesCost();
                        break;
                }

                double i = Double.parseDouble(tvExtendTimeServiceCharge.getText().toString().trim().replaceAll("元", ""))
                        + lateMoney
                        + extendTimePowerExchangeFee
                        - coupon_money;
                tvExtendTimeMoneySum.setText(i + "元");
            }
        });
    }

    @Override
    public void initData() {
        TreeMap<String, String> extendTimeMap = new TreeMap<>();
        extendTimeMap.put("userId", SPUtil.get(this, USER_ID, "") + "");
        extendTimeMap.put("orderId", orderId + "");
        getPresenter().getExtendTime(extendTimeMap, true, false);

        TreeMap<String, String> extendTimeListMap = new TreeMap<>();
        extendTimeListMap.put("city", SPUtil.get(this, CITY, "") + "");
        getPresenter().getExtendTimeList(extendTimeListMap, false, false);
    }

    private List<String> getUseCarTimeData() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            list.add(i + "个月");
        }
        return list;
    }

    //条件选择器
    private void showPickerView() {
        listData = getUseCarTimeData();
        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tvExtendTime.setText(listData.get(options1));
                tvExtendTimeServiceTime.setText(listData.get(options1).replaceAll("个月", "") + "x" + ehicleListBean.getVehicleRent());
                tvExtendTimeServiceCharge.setText((Double.parseDouble(listData.get(options1).replaceAll("个月", "")) * ehicleListBean.getVehicleRent()) + "元");
            }
        })
                .setDividerColor(getResources().getColor(R.color.white))//设置分割线的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final Button tvSubmit = (Button) v.findViewById(R.id.bt_pickview_confirm);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.returnData();
                                pvOptions.dismiss();
                            }
                        });
                    }
                })
                .setSelectOptions(0)//设置选择第一个
                .setOutSideCancelable(true)//点击背的地方不消失
                .build();//创建
        pvOptions.setPicker(listData);
        pvOptions.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case REQUEST_CODE_99:
                    couponId = data.getIntExtra("coupon_id", 0);
                    coupon_name = data.getStringExtra("coupon_name");
                    tvUseCarCouponName.setText(coupon_name);
                    tvExtendTimeCouponType.setText(coupon_name);
                    coupon_money = data.getDoubleExtra("coupon_money", 0);
                    tvUseCarCouponMoney.setText("-" + coupon_money + "元");
                    double i = Double.parseDouble(tvExtendTimeServiceCharge.getText().toString().trim().replaceAll("元", ""))
                            + lateMoney
                            + extendTimePowerExchangeFee
                            - coupon_money;
                    tvExtendTimeMoneySum.setText(i + "元");
                    break;
            }
        }
    }

    private void payType(int payType) {
        switch (payType) {
            case 0:
                TreeMap<String, String> aliPayMap = new TreeMap<>();
                aliPayMap.put("userId", SPUtil.get(this, USER_ID, "") + "");
                aliPayMap.put("orderId", orderId + "");
                aliPayMap.put("tenancyService", tvExtendTimeServiceCharge.getText().toString().trim().replaceAll("元", ""));
                aliPayMap.put("coupon", coupon_money + "");
                aliPayMap.put("total", tvExtendTimeMoneySum.getText().toString().trim().replaceAll("元", ""));
                aliPayMap.put("rentDuration", tvExtendTime.getText().toString().trim().replaceAll("个月", ""));
                aliPayMap.put("chargeMoney", extendTimePowerExchangeFee + "");
                aliPayMap.put("couponTitle", coupon_name);
                aliPayMap.put("lateMoney", lateMoney + "");
                getPresenter().getExtendTimeAli(aliPayMap, true, false);
                break;
            case 1:
                TreeMap<String, String> weixinPayMap = new TreeMap<>();
                weixinPayMap.put("userId", SPUtil.get(this, USER_ID, "") + "");
                weixinPayMap.put("orderId", orderId + "");
                weixinPayMap.put("tenancyService", tvExtendTimeServiceCharge.getText().toString().trim().replaceAll("元", ""));
                weixinPayMap.put("coupon", coupon_money + "");
                weixinPayMap.put("couponTitle", coupon_name);
                weixinPayMap.put("total", tvExtendTimeMoneySum.getText().toString().trim().replaceAll("元", ""));
                weixinPayMap.put("rentDuration", tvExtendTime.getText().toString().trim().replaceAll("个月", ""));
                weixinPayMap.put("chargeMoney", extendTimePowerExchangeFee + "");
                weixinPayMap.put("lateMoney", lateMoney + "");
                getPresenter().getExtendTimeWeiChat(weixinPayMap, true, false);
                break;
        }
    }

    @OnClick({R.id.tv_extend_time, R.id.ll_extend_time_coupon, R.id.ll_alipay, R.id.ll_weixin_pay, R.id.bt_extend_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_extend_time:
                showPickerView();
                break;
            case R.id.ll_extend_time_coupon:
                startActivityForResult(new Intent(this, UserCouponActivity.class).putExtra("money", Double.parseDouble(tvExtendTimeMoneySum.getText().toString().trim().replaceAll("元", ""))).putExtra("coupon_id", couponId), REQUEST_CODE_99);
                break;
            case R.id.ll_alipay:
                payType = 0;
                ivAlipay.setVisibility(View.VISIBLE);
                ivWeixinPay.setVisibility(View.GONE);
                break;
            case R.id.ll_weixin_pay:
                payType = 1;
                ivAlipay.setVisibility(View.GONE);
                ivWeixinPay.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_extend_time:
                if (!tvExtendTime.getText().toString().trim().equals("请选择租赁时长"))
                    payType(payType);
                else
                    ToastUtil.showShortToast("请选择租赁时长");
                break;
        }
    }

    @Override
    public void resultExtendTime(ExtendTimeBean data) {
        ehicleListBean = data.getData().getVehicleList();
        tvExtendTimeServiceCharge.setText(ehicleListBean.getRentOften() * ehicleListBean.getVehicleRent() + "元");
        lateMoney = data.getData().getLateMoney();
        if (data.getData().getLateMoney() != 0)
            tvExtendTimeLateFee.setText(data.getData().getLateMoney() + "元");
        else
            llExtendTimeLate.setVisibility(View.GONE);

        tvExtendTimeMoneySum.setText(Double.parseDouble(tvExtendTimeServiceCharge.getText().toString().trim().replaceAll("元", "")) + lateMoney + "元");
    }

    @Override
    public void resultExtendTimeList(ExtendTimeListBean data) {
        extendTimeListBean.clear();
        if (data.getData().getVehicleServices().size() > 0)
            extendTimeListBean.addAll(data.getData().getVehicleServices());
        else
            llExtendTimePowerExchange.setVisibility(View.GONE);

        switch (data.getData().getVehicleServices().size()) {
            case 0:
                llPowerExchange.setVisibility(View.GONE);
                break;
            case 1:
                rbChargingOne.setText(data.getData().getVehicleServices().get(0).getServicesName());
                rbChargingSecond.setVisibility(View.GONE);
                rbChargingThree.setVisibility(View.GONE);
                break;
            case 2:
                rbChargingOne.setText(data.getData().getVehicleServices().get(0).getServicesName());
                rbChargingSecond.setText(data.getData().getVehicleServices().get(1).getServicesName());
                rbChargingThree.setVisibility(View.GONE);
                break;
            case 3:
                rbChargingOne.setText(data.getData().getVehicleServices().get(0).getServicesName());
                rbChargingSecond.setText(data.getData().getVehicleServices().get(1).getServicesName());
                rbChargingThree.setText(data.getData().getVehicleServices().get(2).getServicesName());
                break;
        }
    }

    @Override
    public void resultExtendTimeAli(AliPayBean data) {
        if (data.getCode() == 200) {
            new AliPay(ExtendTimeActivity.this, data.getData().getData());
        }
    }

    @Override
    public void resultExtendTimeWeiChat(WeChatPayBean data) {
        if (data.getCode() == 200) {
            IWXAPI api = WXAPIFactory.createWXAPI(this, null);
            api.registerApp("wx1a65c563b86ec579");
            PayReq req = new PayReq();
            req.appId = data.getData().getData().getAppid();//你的微信appid
            req.partnerId = data.getData().getData().getPartnerid();//商户号
            req.prepayId = data.getData().getData().getPrepayid();//预支付交易会话ID
            req.nonceStr = data.getData().getData().getNoncestr();//随机字符串
            req.timeStamp = data.getData().getData().getTimestamp() + "";//时间戳
            req.packageValue = data.getData().getData().getPackageX(); //扩展字段, 这里固定填写Sign = WXPay
            req.sign = data.getData().getData().getSign();//签名
            //  req.extData         = "app data"; // optional
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.sendReq(req);
        }
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return this.bindToLifecycle();
    }
}