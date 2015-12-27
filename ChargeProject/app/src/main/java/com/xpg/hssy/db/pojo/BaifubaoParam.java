package com.xpg.hssy.db.pojo;

/**
 * Created by Administrator on 2015/11/20.
 */
public class BaifubaoParam {
	private String order_no;
	private String service_code;
	private String sp_no;
	private String order_create_time;
	private String goods_name;
	private String unit_amount;
	private String unit_count;
	private String transport_amount;
	private String total_amount;
	private String currency;
	private String goods_desc;
	private String return_url;
	private String pay_type;
	private String input_charset;
	private String version;
	private String sign;
	private String sign_method;
	private String sp_user_name;
	private String contract_type;
	private String pure_sign;
	private String secret;

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getService_code() {
		return service_code;
	}

	public void setService_code(String service_code) {
		this.service_code = service_code;
	}

	public String getSp_no() {
		return sp_no;
	}

	public void setSp_no(String sp_no) {
		this.sp_no = sp_no;
	}

	public String getOrder_create_time() {
		return order_create_time;
	}

	public void setOrder_create_time(String order_create_time) {
		this.order_create_time = order_create_time;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getUnit_amount() {
		return unit_amount;
	}

	public void setUnit_amount(String unit_amount) {
		this.unit_amount = unit_amount;
	}

	public String getUnit_count() {
		return unit_count;
	}

	public void setUnit_count(String unit_count) {
		this.unit_count = unit_count;
	}

	public String getTransport_amount() {
		return transport_amount;
	}

	public void setTransport_amount(String transport_amount) {
		this.transport_amount = transport_amount;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getGoods_desc() {
		return goods_desc;
	}

	public void setGoods_desc(String goods_desc) {
		this.goods_desc = goods_desc;
	}

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getInput_charset() {
		return input_charset;
	}

	public void setInput_charset(String input_charset) {
		this.input_charset = input_charset;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSign_method() {
		return sign_method;
	}

	public void setSign_method(String sign_method) {
		this.sign_method = sign_method;
	}

	public String getSp_user_name() {
		return sp_user_name;
	}

	public void setSp_user_name(String sp_user_name) {
		this.sp_user_name = sp_user_name;
	}

	public String getContract_type() {
		return contract_type;
	}

	public void setContract_type(String contract_type) {
		this.contract_type = contract_type;
	}

	public String getPure_sign() {
		return pure_sign;
	}

	public void setPure_sign(String pure_sign) {
		this.pure_sign = pure_sign;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String toString(){
		return "currency="+currency + "&goods_desc=" + goods_desc +"&input_charset="+input_charset +
				"&order_create_time=" + order_create_time + "&order_no=" + order_no + "&pay_type ="+ pay_type + "&return_url="+return_url
				+"&sign_method="+sign_method + "&sp_no=" + sp_no + "&total_amount="+total_amount +"&transport_amount="+transport_amount +
				"&unit_amount="+unit_amount + "&unit_count="+unit_count+"&sign="+sign;
	}
}
