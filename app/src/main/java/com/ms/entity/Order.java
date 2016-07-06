package com.ms.entity;

import android.os.Parcel;
import android.os.Parcelable;

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

	private double payed;
	private double shipped;

	private int deliveryExpress;
	private int deliverySeller;
	private int deliverySellerDtId;

	private String sellerName;
	private String sellerTel;

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

	public double getPayed() {
		return payed;
	}

	public void setPayed(double payed) {
		this.payed = payed;
	}

	public double getShipped() {
		return shipped;
	}

	public void setShipped(double shipped) {
		this.shipped = shipped;
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

	public String getPayStatusStr() {
		if (payStatus == 1) {
			return "已支付";
		} else {
			return "未支付";
		}
	}

	public String getShippingStr() {
		if (shippingId == 11) {
			return "到店付";
		} else {
			return "配送单";
		}
	}

	public String getShippingStr2() {
		if (shippingId == 11) {
			return "到店";
		} else {
			return "配送";
		}
	}

	public boolean hasShippingAddr() {
		if (shippingId == 11) {
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
				 String shipAddr, double payed, double shipped, int deliveryExpress, int deliverySeller, int deliverySellerDtId,
				 String sellerName, String sellerTel){
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
		this.payed = payed;
		this.shipped = shipped;
		this.deliveryExpress = deliveryExpress;
		this.deliverySeller = deliverySeller;
		this.deliverySellerDtId = deliverySellerDtId;
		this.sellerName = sellerName;
		this.sellerTel = sellerTel;
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
		this.payed = in.readDouble();
		this.shipped = in.readDouble();
		this.deliveryExpress = in.readInt();
		this.deliverySeller = in.readInt();
		this.deliverySellerDtId = in.readInt();
		this.sellerName = in.readString();
		this.sellerTel = in.readString();
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
		dest.writeDouble(this.getPayed());
		dest.writeDouble(this.getShipped());
		dest.writeInt(this.getDeliveryExpress());
		dest.writeInt(this.getDeliverySeller());
		dest.writeInt(this.getDeliverySellerDtId());
		dest.writeString(this.getSellerName());
		dest.writeString(this.getSellerTel());
	}

}

