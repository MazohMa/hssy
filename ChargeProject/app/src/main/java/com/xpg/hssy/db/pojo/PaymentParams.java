package com.xpg.hssy.db.pojo;

/**
 * PaymentParams
 *
 * @author Jason 2015 09 28
 * @version 1.0
 * @category 支付宝请求参数
 */
public class PaymentParams {

    private String partner;
    private String seller_id;
    private String out_trade_no;
    private String subject;
    private String body;
    private String total_fee;
    private String notifyUrl;
    private String service;
    private String payment_type;
    private String _input_charset;
    private String it_b_pay;
    private String return_url;
    private String sign;
    private String sign_type;

    public String getPartner() {
        return partner;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public String getService() {
        return service;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public String get_input_charset() {
        return _input_charset;
    }

    public String getIt_b_pay() {
        return it_b_pay;
    }

    public String getReturn_url() {
        return return_url;
    }

    public String getSign() {
        return sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public void setIt_b_pay(String it_b_pay) {
        this.it_b_pay = it_b_pay;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }
}
