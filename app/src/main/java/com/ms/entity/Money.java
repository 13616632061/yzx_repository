package com.ms.entity;

import java.io.Serializable;

public class Money implements Serializable {
	private String type;
	private String mtime;
	private String message;
	private double money;
	private int status;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMtime() {
		return mtime;
	}

	public void setMtime(String mtime) {
		this.mtime = mtime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusStr() {
		if (status == 1) {
			return "审核中";
		} else if(status == 2) {
			return "未通过";
		} else {
			return (type.equals("withdraw")) ? "提现成功" : "充值成功";
		}
	}
}
