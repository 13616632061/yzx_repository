package com.ms.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.ms.ks.R;

public class Order implements Parcelable {
	private String orderSn;
	private String orderTime;
	private String name;

	private int payStatus;
	private int shippingId;
	private int shipStatus;

	private int buttonAffirm;
	private int buttonOff;
	private int shipmentsYes;
	private int shipmentsNo;

	private String shipAddr;
	private String shipName;
	private String shipMobile;

	private double payed;
	private double shipped;
	private double apay;

	private int deliveryExpress;
	private int deliverySeller;
	private int deliverySellerDtId;

	private String sellerName;
	private String sellerTel;

	String memo;

	int distribution;

	String status;

	double cost_item, pmt_order, finalPayed;

	String payment_status;
	String print_number;
	private String order_num;
	private String qrcode_url;
	private String desk_num;

	public String getPayment_status() {
		return payment_status;
	}

	public void setPayment_status(String payment_status) {
		this.payment_status = payment_status;
	}

	public String getPrint_number() {
		return print_number;
	}

	public void setPrint_number(String print_number) {
		this.print_number = print_number;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(int payStatus) {
		this.payStatus = payStatus;
	}

	public int getShippingId() {
		return shippingId;
	}

	public void setShippingId(int shippingId) {
		this.shippingId = shippingId;
	}

	public int getButtonAffirm() {
		return buttonAffirm;
	}

	public void setButtonAffirm(int buttonAffirm) {
		this.buttonAffirm = buttonAffirm;
	}

	public int getButtonOff() {
		return buttonOff;
	}

	public void setButtonOff(int buttonOff) {
		this.buttonOff = buttonOff;
	}

	public int getShipmentsYes() {
		return shipmentsYes;
	}

	public void setShipmentsYes(int shipmentsYes) {
		this.shipmentsYes = shipmentsYes;
	}

	public int getShipmentsNo() {
		return shipmentsNo;
	}

	public void setShipmentsNo(int shipmentsNo) {
		this.shipmentsNo = shipmentsNo;
	}

	public int getShipStatus() {
		return shipStatus;
	}

	public void setShipStatus(int shipStatus) {
		this.shipStatus = shipStatus;
	}

	public String getShipAddr() {
		return shipAddr;
	}

	public void setShipAddr(String shipAddr) {
		this.shipAddr = shipAddr;
	}

	public String getShipName() {
		return shipName;
	}

	public void setShipName(String shipName) {
		this.shipName = shipName;
	}


	public String getShipMobile() {
		return shipMobile;
	}

	public void setShipMobile(String shipMobile) {
		this.shipMobile = shipMobile;
	}

	public double getPayed() {
		return payed;
	}

	public void setPayed(double payed) {
		this.payed = payed;
	}

	public double getCost_item() {
		return cost_item;
	}

	public void setCost_item(double cost_item) {
		this.cost_item = cost_item;
	}

	public double getPmt_order() {
		return pmt_order;
	}

	public void setPmt_order(double pmt_order) {
		this.pmt_order = pmt_order;
	}

	public double getShipped() {
		return shipped;
	}

	public void setShipped(double shipped) {
		this.shipped = shipped;
	}

	public double getFinalPayed() {
		return finalPayed;
	}

	public void setFinalPayed(double finalPayed) {
		this.finalPayed = finalPayed;
	}

	public int getDeliveryExpress() {
		return deliveryExpress;
	}

	public void setDeliveryExpress(int deliveryExpress) {
		this.deliveryExpress = deliveryExpress;
	}

	public int getDeliverySeller() {
		return deliverySeller;
	}

	public void setDeliverySeller(int deliverySeller) {
		this.deliverySeller = deliverySeller;
	}

	public int getDeliverySellerDtId() {
		return deliverySellerDtId;
	}

	public void setDeliverySellerDtId(int deliverySellerDtId) {
		this.deliverySellerDtId = deliverySellerDtId;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getSellerTel() {
		return sellerTel;
	}

	public void setSellerTel(String sellerTel) {
		this.sellerTel = sellerTel;
	}


	public int getDistribution() {
		return distribution;
	}

	public void setDistribution(int distribution) {
		this.distribution = distribution;
	}

	public String getPayStatusStr() {
		if (payStatus == 1) {
			return "已支付";
		} else {
			return "未支付";
		}
	}

	public String getShippingStr() {
		if (distribution == 1) {
			return "到店付";
		} else {
			return "配送单";
		}
	}

	public int getShippingRes() {
		if (distribution == 1) {
			return R.drawable.ic_dd;
		} else {
			return R.drawable.ic_ps;
		}
	}

	public String getShippingStr2() {
		if (distribution == 1) {
			return "到店";
		} else {
			return "配送";
		}
	}

	public boolean hasShippingAddr() {
		if (distribution == 1) {
			return false;
		} else {
			return true;
		}
	}

	public boolean canClose() {
		return buttonOff == 1;
	}

	public boolean canComplete() {
		return buttonAffirm == 1;
	}

	public boolean canShip() {
		if (hasShippingAddr()) {
			if (payStatus == 1 && shipStatus != 1) {
				return true;
			}
		}

		return false;
	}

	public static final Creator<Order> CREATOR = new Creator<Order>() {
		@Override
		public Order createFromParcel(Parcel source) {//该方法用于告诉平台如何从包裹里创建数据类实例
			return new Order(source);
		}
		@Override
		public Order[] newArray(int size) {
			return new Order[size];
		}
	};

	public Order(String orderSn, String orderTime, String name, int payStatus, int shippingId, int shipStatus,
				 int buttonAffirm, int buttonOff, int shipmentsYes, int shipmentsNo,
				 String shipAddr, String shipName, String shipMobile,
				 double payed, double shipped, int deliveryExpress, int deliverySeller, int deliverySellerDtId,
				 String sellerName, String sellerTel, String memo, int distribution, String status,
				 double cost_item, double pmt_order, double finalPayed, String payment_status, String print_number,
				 double apay, String order_num, String qrcode_url, String desk_num){
		this.orderSn = orderSn;
		this.orderTime = orderTime;
		this.name = name;
		this.payStatus = payStatus;
		this.shippingId = shippingId;
		this.shipStatus = shipStatus;
		this.buttonAffirm = buttonAffirm;
		this.buttonOff = buttonOff;
		this.shipmentsYes = shipmentsYes;
		this.shipmentsNo = shipmentsNo;
		this.shipAddr = shipAddr;
		this.shipName = shipName;
		this.shipMobile = shipMobile;
		this.payed = payed;
		this.shipped = shipped;
		this.deliveryExpress = deliveryExpress;
		this.deliverySeller = deliverySeller;
		this.deliverySellerDtId = deliverySellerDtId;
		this.sellerName = sellerName;
		this.sellerTel = sellerTel;
		this.memo = memo;
		this.distribution = distribution;
		this.status = status;
		this.cost_item = cost_item;
		this.pmt_order = pmt_order;
		this.finalPayed = finalPayed;
		this.payment_status = payment_status;
		this.print_number = print_number;
		this.apay = apay;
		this.order_num = order_num;
		this.qrcode_url = qrcode_url;
		this.desk_num = desk_num;
	}

	public Order(Parcel in){
		this.orderSn = in.readString();
		this.orderTime = in.readString();
		this.name = in.readString();
		this.payStatus = in.readInt();
		this.shippingId = in.readInt();
		this.shipStatus = in.readInt();
		this.buttonAffirm = in.readInt();
		this.buttonOff = in.readInt();
		this.shipmentsYes = in.readInt();
		this.shipmentsNo = in.readInt();
		this.shipAddr = in.readString();
		this.shipName = in.readString();
		this.shipMobile = in.readString();
		this.payed = in.readDouble();
		this.shipped = in.readDouble();
		this.deliveryExpress = in.readInt();
		this.deliverySeller = in.readInt();
		this.deliverySellerDtId = in.readInt();
		this.sellerName = in.readString();
		this.sellerTel = in.readString();
		this.memo = in.readString();
		this.distribution = in.readInt();
		this.status = in.readString();
		this.cost_item = in.readDouble();
		this.pmt_order = in.readDouble();
		this.finalPayed = in.readDouble();
		this.payment_status = in.readString();
		this.print_number = in.readString();
		this.apay = in.readDouble();
		this.order_num = in.readString();
		this.qrcode_url = in.readString();
		this.desk_num = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getOrderSn());
		dest.writeString(this.getOrderTime());
		dest.writeString(this.getName());
		dest.writeInt(this.getPayStatus());
		dest.writeInt(this.getShippingId());
		dest.writeInt(this.getShipStatus());
		dest.writeInt(this.getButtonAffirm());
		dest.writeInt(this.getButtonOff());
		dest.writeInt(this.getShipmentsYes());
		dest.writeInt(this.getShipmentsNo());
		dest.writeString(this.getShipAddr());
		dest.writeString(this.getShipName());
		dest.writeString(this.getShipMobile());
		dest.writeDouble(this.getPayed());
		dest.writeDouble(this.getShipped());
		dest.writeInt(this.getDeliveryExpress());
		dest.writeInt(this.getDeliverySeller());
		dest.writeInt(this.getDeliverySellerDtId());
		dest.writeString(this.getSellerName());
		dest.writeString(this.getSellerTel());
		dest.writeString(this.getMemo());
		dest.writeInt(this.getDistribution());
		dest.writeString(this.getStatus());
		dest.writeDouble(this.getCost_item());
		dest.writeDouble(this.getPmt_order());
		dest.writeDouble(this.getFinalPayed());
		dest.writeString(this.getPayment_status());
		dest.writeString(this.getPrint_number());
		dest.writeDouble(this.getApay());
		dest.writeString(this.getOrder_num());
		dest.writeString(this.getQrcode_url());
		dest.writeString(this.getDesk_num());
	}

	public String getStatusStr() {
		return getPayment_status();
//		if (status.equals("dead")) {
//			return "<font color='red'>已作废</font>";
//		} else if (status.equals("finish")) {
//			return "<font color='blue'>已完成</font>";
//		} else {
//			//活动订单显示支付状态
//			if (payStatus == 1) {
//				return "已支付";
//			} else {
//				return "<font color='red'>未支付</font>";
//			}
//		}
	}


	public double getApay() {
		return apay;
	}

	public void setApay(double apay) {
		this.apay = apay;
	}

	public String getOrder_num() {
		return order_num;
	}

	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}

	public String getQrcode_url() {
		return qrcode_url;
	}

	public void setQrcode_url(String qrcode_url) {
		this.qrcode_url = qrcode_url;
	}

	public String getDesk_num() {
		return desk_num;
	}

	public void setDesk_num(String desk_num) {
		this.desk_num = desk_num;
	}

	public boolean hasQrCode() {
		if(payStatus == 0) {
			//未付款
			if(!TextUtils.isEmpty(qrcode_url)) {
				return true;
			}
		}

		return false;
	}
}

