package com.zxb.client;

import android.annotation.SuppressLint;

import java.util.HashMap;


@SuppressLint("UseSparseArrays")
public class TransferRequestTag {
	
	//测试->生产  21->23  22->24

	public static final int Login = 1;// 登录
	public static final int Register = 2;// 注册   预生成商户网点失败
	public static final int ModifyLoginPwd = 3;// 修改登录密码
	public static final int ForgetLoginPwd = 4;// 忘记登录密码
	public static final int SignIn = 5;// 签到
	public static final int Consume = 6;// 消费
	public static final int ConsumeCancel = 7;// 消费撤销
	public static final int BalanceQuery = 8;// 余额查询
	public static final int FlowQuery = 9;// 流水查询
	public static final int CreditCardApply = 10;// 信用卡额度申请
	public static final int ClearQuery = 11;// 清算查询
	public static final int AppCommend = 12;// 应用推荐
	public static final int ReferenceMsg = 13;// 参考信息
	public static final int ShareTransfer = 14;// 分享交易
	public static final int ExaminePhone = 15;// 检验手机号是否存在
	public static final int CompareOldPwd = 16;// 对比原密码是否正确
	public static final int SmsSend = 17;// 短信发送
	public static final int SmsCheck = 18;// 短信码验证
	public static final int MerchantQuery = 19;// 商户信息查询
	public static final int LoadUpHead = 20;// 上传头像
	public static final int GetDownLoadHead = 21;// 下载头像
	public static final int CashCharge = 22;// 现金记账
	public static final int GetCashCharge = 23;// 获取现金记账列表
	public static final int CashDelete = 24;// 删除现金记账  
	public static final int LoadUpStreetImg = 25;// 上传街景图片 
	public static final int GetDownLoadStreetImg = 26;// 下载街景图片  
	public static final int GetProvinceName = 27; // 获取省名称
	public static final int GetCityName = 28; // 获取城市名称
	public static final int GetBank = 29; // 获取银行名称
	public static final int GetBankBranch = 30; // 获取银行支行名称
	public static final int UpLoadImage = 31; // 上传图片                  上传参数缺失
	public static final int CheckTicket = 32; // 查看交易小票				 查询流水失败，请联系客服
	public static final int SendTicket = 33; // 发送交易小票				 查询小票信息失败，请联系客服
	public static final int DrawMoney = 34; // 提现						 用户密码错误
	public static final int MyAccount = 35; // 我的账户				     13945621452 此商户不存在或未审核通过或已关闭
	public static final int PhoneRecharge = 36; // 手机充值   
	public static final int Authentication = 37; // 实名认证  
	public static final int CardCard = 38; // 卡卡转账
	public static final int CreditCard = 39; // 信用卡还款
	public static final int UploadSignImage = 40; // 上传签购单
	public static final int UpdateVersion = 41; 
	public static final int RateType = 42; 
	public static final int RateInstruction = 43; 
	
	private static HashMap<Integer, String> requestTagMap = null;

	public static HashMap<Integer, String> getRequestTagMap() {
		if (null == requestTagMap) {	
			requestTagMap = new HashMap<Integer, String>();

			requestTagMap.put(Login, Constants.ip+"199002.tranm");// http://211.147.87.24:8092/posm/199002.tran5
			//http://211.147.87.20:8092/Vpm/199002.tranm
			requestTagMap.put(Register, Constants.ip+"199001.tranm");
			requestTagMap.put(ModifyLoginPwd, Constants.ip+"199003.tranm");
			requestTagMap.put(ForgetLoginPwd, Constants.ip+"199004.tranm");
			requestTagMap.put(SignIn, Constants.ip+"199020.tranm");
			requestTagMap.put(Consume, Constants.ip+"199005.tranm");
			requestTagMap.put(ConsumeCancel, "http://211.147.87.23:8088/posp/199006.tran");
			requestTagMap.put(BalanceQuery, "http://211.147.87.23:8088/posp/199007.tran");
			requestTagMap.put(FlowQuery, Constants.ip+"199008.tranm"); // 流水查询
			requestTagMap.put(CreditCardApply, "http://211.147.87.23:8080/posp/199010.tran");// 信用卡额度申请
			requestTagMap.put(ClearQuery, "http://211.147.87.23:8080/posp/199009.tran");
			requestTagMap.put(AppCommend, "http://211.147.87.24:8092/posm/199011.tran5");
			requestTagMap.put(ReferenceMsg, Constants.ip+"199012.tranm");
			requestTagMap.put(ShareTransfer, "http://211.147.87.24:8092/posm/199015.tran5");
			requestTagMap.put(ExaminePhone, "http://211.147.87.24:8092/posm/199016.tran5");
			requestTagMap.put(CompareOldPwd, "http://211.147.87.24:8092/posm/199017.tran5");
			requestTagMap.put(SmsSend, Constants.ip+"199018.tranm");
			requestTagMap.put(SmsCheck, Constants.ip+"199019.tranm");
			requestTagMap.put(MerchantQuery, Constants.ip+"199022.tranm");
			
			requestTagMap.put(CashCharge, "http://220.194.46.46:8080/zfb/mpos/transProcess.do?operationId=addTransaction");
			requestTagMap.put(GetCashCharge, "http://220.194.46.46:8080/zfb/mpos/transProcess.do?operationId=getTransaction");
			requestTagMap.put(CashDelete, "http://220.194.46.46:8080/zfb/mpos/transProcess.do?operationId=delTransaInfo");

			requestTagMap.put(UpdateVersion, "http://220.194.46.46:8080/zfb/mpos/transProcess.do?operationId=getVersion&type=2");
			//http://192.168.4.126:8080/zfb/mpos/transProcess.do?operationId=getVersion&type=2
			
			requestTagMap.put(LoadUpHead, "http://220.194.46.46:8080/zfb/mpos/transProcess.do?operationId=setHeadImg");
			requestTagMap.put(GetDownLoadHead, "http://220.194.46.46:8080/zfb/mpos/transProcess.do?operationId=getHeadImg");
			requestTagMap.put(LoadUpStreetImg, "http://220.194.46.46:8080/zfb/mpos/transProcess.do?operationId=setStreetImg");
			requestTagMap.put(GetDownLoadStreetImg, "http://220.194.46.46:8080/zfb/mpos/transProcess.do?operationId=getStreetImg");
			// 220.194.46.46    116.228.88.115:18080
			requestTagMap.put(GetProvinceName, Constants.ip+"199031.tranm");
			requestTagMap.put(GetCityName, Constants.ip+"199032.tranm");
			requestTagMap.put(GetBank, Constants.ip+"199035.tranm");
			requestTagMap.put(GetBankBranch, Constants.ip+"199034.tranm");
			requestTagMap.put(UpLoadImage, Constants.ip+"199021.tran");
			requestTagMap.put(CheckTicket, Constants.ip+"199036.tranm");
			requestTagMap.put(SendTicket, Constants.ip+"199037.tranm");
			requestTagMap.put(DrawMoney, Constants.ip+"199025.tranm");
			requestTagMap.put(MyAccount, Constants.ip+"199026.tranm");
			requestTagMap.put(PhoneRecharge, Constants.ip+"708103.tranp");
			requestTagMap.put(Authentication, Constants.ip+"199030.tranm");
			requestTagMap.put(CardCard, Constants.ip+"708101.tranm");
			requestTagMap.put(CreditCard, Constants.ip+"708102.tranm");
			requestTagMap.put(UploadSignImage, Constants.ip+"199010.tranm");
			requestTagMap.put(RateType, Constants.ip+"199038.tranm");
			
			// 192.168.4.115:8080  测试服务器地址
			// 59.49.29.154:8586 外网
			
		}

		return requestTagMap;
	}

}
