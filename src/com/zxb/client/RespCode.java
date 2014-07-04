package com.zxb.client;


public enum RespCode {
	
	RESP_000000("000000", "成功"),
	RESP_000001("000000", "请求xml报文解析错误"),
	RESP_100001("100001", "请求xml报文mac验证错误"),
	RESP_200001("200001", "请求xml报文参数缺失"),
	RESP_300001("300001", "请求xml报文mac验证过程出错"),
	RESP_400001("400001", "请求xml报文交易码不符"),
	RESP_000002("000002", "用户登录用户名不存在或者商户正在审核中"),
	RESP_100002("100002", "用户密码错误或者原密码错误"),
	RESP_300002("300002", "注册失败"),
	RESP_400002("400002", "商户审核未通过"),
	RESP_500002("500002", "该商户账号已锁定"),
	RESP_200002("200002", "验证码错误"),
	RESP_000003("000003", "无效终端"),
	RESP_100003("100003", "地域超出容许范围"),
	RESP_200003("200003", "交易不存在"), // 针对撤销交易
	RESP_000004("000004", "请核对卡信息后重新输入"),
	RESP_100004("100004", "过期的卡"),
	RESP_200004("200004", "有作弊嫌疑的卡"),
	RESP_300004("300004", "受限制的卡，改商户没有开通改功能"),
	RESP_400004("400004", "挂失卡"),
	RESP_500004("500004", "已销户"),
	RESP_600004("600004", "密码试输入超限"),
	RESP_700004("700004", "余额不足"),
	RESP_000005("000005", "网络不行，请稍后再试"),
	RESP_100005("100005", "无效证件类型"),
	RESP_200005("200005", "超出持卡人设置的交易限额"),
	RESP_300005("300005", "支付内部系统错误"),
	RESP_400005("400005", "交易不能受理"),
	RESP_000006("000006", "服务器出现内部错误,请联系客服"),
	RESP_000099("000099", "其他,请联系支付系统进行解决。");
	
	private String code;
	private String message;
	
	private RespCode(String code, String message){
		this.code = code;
		this.message = message;
	}
	
	public String getCode(){
		return this.code;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public static String getMessage(String code){
		for (RespCode error : RespCode.values()){
			if (code.equals(error.getCode().trim())){
				return error.getMessage();
			}
		}
		
		return "未知错误";
	}

}
