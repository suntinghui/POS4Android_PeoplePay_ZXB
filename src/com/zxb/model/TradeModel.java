package com.zxb.model;

import java.io.Serializable;

public class TradeModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String sysDate; // 交易日期时间
	private String merName;
	private String logDate; // 交易日期
	private String logNo; // 交易流水号
	private String txncd; // 交易类型
	private String txnsts; // 状态
	private String cardNo;
	private String txnamt;

	public String getSysDate() {
		return sysDate;
	}

	public void setSysDate(String sysDate) {
		this.sysDate = sysDate;
	}

	public String getMerName() {
		return merName;
	}

	public void setMerName(String merName) {
		this.merName = merName;
	}

	public String getLogDate() {
		return logDate;
	}

	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}

	public String getLogNo() {
		return logNo;
	}

	public void setLogNo(String logNo) {
		this.logNo = logNo;
	}

	public String getTxncd() {
		return this.txncd;
	}

	public String formatTxncd() {
		if (this.txncd.equals("0200000000")) {
			return "消费";
		} else if (this.txncd.equals("0200200000")) {
			return "消费撤销";
		}

		return "";
	}

	public void setTxncd(String txncd) {
		this.txncd = txncd;
	}

	public String getTxnsts() {
		return this.txnsts;
	}

	public String formatTxnsts() {
		if (this.txnsts.equalsIgnoreCase("S")) {
			return "成功";
		} else if (this.txnsts.equalsIgnoreCase("R")) {
			return "撤销";
		} else if (this.txnsts.equalsIgnoreCase("0")) {
			return "预计";
		} else if (this.txnsts.equalsIgnoreCase("C")) {
			return "冲正";
		} else if (this.txnsts.equalsIgnoreCase("T")) {
			return "超时";
		} else if (this.txnsts.equalsIgnoreCase("F")) {
			return "失败";
		} else if (this.txnsts.equalsIgnoreCase("E")) {
			return "完成";
		}
		return txnsts;
	}

	public void setTxnsts(String txnsts) {
		this.txnsts = txnsts;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getTxnamt() {
		return txnamt;
	}

	public void setTxnamt(String txnamt) {
		this.txnamt = txnamt;
	}
	
	public String getStatus(){
		return this.formatTxncd()+this.formatTxnsts();
	}

}
