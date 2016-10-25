package com.ms.util;

import android.text.TextUtils;

import com.ms.entity.OrderGoods;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PrintUtil {
    private static int length = 32;

    public static  int  getWordCount(String s) {
        int  length  =   0 ;
        for ( int  i  =   0 ; i  <  s.length(); i ++ )
        {
            int  ascii  =  Character.codePointAt(s, i);
            if (ascii  >=   0   &&  ascii  <= 255 )
                length ++ ;
            else
                length  +=   2 ;

        }
        return  length;
    }

    //得到收银小票的打印模板
    public static String getPrinterMsg(String index,
                                       String shippingStr,
                                       String shopName,
                                       String deskNo,
                                       String shopTel,
                                       String orderSn,
                                       String orderDate,
                                       ArrayList<OrderGoods> goodsList,
                                       boolean hasPay,
                                       double payed,
                                       String orderRemark,
                                       boolean hasAddress,
                                       String consignee,
                                       String mobile,
                                       String address) {
        String ret = "";

        //抬头
        ret += "----------";
        ret += index;
        int a = 32 - 20 - getWordCount(index) - getWordCount(shippingStr);
        ret += getPrintChar(" ", a);
        ret += shippingStr;
        ret += "----------";
        ret += "\r\n";

        //商家名称
        ret += "\r\n";
        shopName = getPrintCenter(shopName);
        ret += shopName;
        if(!TextUtils.isEmpty(deskNo)) {
            ret += "\r\n";
            ret += getPrintCenter("桌号：" + deskNo);
        }
        ret += "\r\n";
        ret += "\r\n";

        //电话
        ret += getPrintCenter("电话：" + shopTel);
        //订单号
        ret += getPrintCenter("订单号：" + orderSn);
        //下单时间
        ret += getPrintCenter("下单时间：" + orderDate);
        ret += "\r\n";
        //分隔符
        ret += getPrintChar("*", length);
        //商品列表
        String ss = "数量    小计";   //12
        int ss_l = length - getWordCount(ss);
        ret += "名称";
        ret += getPrintChar(" ", ss_l - 4);
        ret += ss;
        ret += getPrintChar("-", length);
        //打印菜品
        for(int i = 0; i < goodsList.size(); i++) {
            OrderGoods bean = goodsList.get(i);

            //名称
            ret += bean.getName();
            int l1 = ss_l - getWordCount(bean.getName());
            for(int j = 0; j < l1; j++) {
                ret += " ";
            }

            int nn = bean.getQuantity();
            double pp = bean.getPrice();
            String su = SysUtils.priceFormat(nn * pp, false);
            //数量
            ret += getPrintRight(String.valueOf(bean.getQuantity()), 4);

            //小计
            ret += getPrintRight(su, 8);

            if(!TextUtils.isEmpty(bean.getAttr())) {
                //属性
                ret += getPrintLeft(bean.getAttr(), length);
            }
        }
        ret += getPrintChar("-", length);

        String sss = "" + payed;
        int sss_l = length - getWordCount(sss);
        ret += hasPay ? "已支付" : "未支付";
        ret += getPrintChar(" ", sss_l - 6);
        ret += sss;
        ret += "\r\n";
        ret += getPrintChar("*", length);

        //备注
        orderRemark = orderRemark.replaceAll("<br/>","\r\n");
        ret += getPrintLeft("备注：" + orderRemark, length);
        ret += "\r\n";
        if (hasAddress) {
            ret += getPrintLeft(consignee + "  " + mobile, length);
            ret += "\r\n";
            ret += getPrintLeft(address, length);
            ret += "\r\n";
        }
        ret += getPrintChar("-", length);
        ret += getPrintCenter("本地生活就选 易之星");

        ret += "\n\n";

        return ret;
    }

    public static String getTestMsg() {
        String index = "1";
        String shippingStr = "配送";
        String shopName ="xx餐厅";
        String deskNo = "B11";
        String shopTel ="13000000000";
        String orderSn = "0026001";
        String orderDate = "2015-02-13 11:11";

        ArrayList<OrderGoods> goodsList = new ArrayList<OrderGoods>();

        OrderGoods a = new OrderGoods();
        a.setName("可口可乐");
        a.setQuantity(4);
        a.setPrice(10);

        goodsList.add(a);

        a = new OrderGoods();
        a.setName("鲟龙鱼");
        a.setQuantity(1);
        a.setPrice(132);
        goodsList.add(a);

        a = new OrderGoods();
        a.setName("蛏子");
        a.setQuantity(1);
        a.setPrice(28);
        goodsList.add(a);


        a = new OrderGoods();
        a.setName("多春鱼");
        a.setQuantity(1);
        a.setPrice(28);
        a.setAttr("大小: 大份, 味道: 不辣");
        goodsList.add(a);

        boolean hasPay = true;
        double payed = 228.0;

        String orderRemark = "订单备注";
        boolean hasAddress = true;

        String consignee = "王小二";
        String mobile = "18900000000";
        String address = "上海市黄浦区北京东路";

        return getPrinterMsg(index,
                            shippingStr,
                            shopName,
                            deskNo,
                            shopTel,
                            orderSn,
                            orderDate,
                            goodsList,
                            hasPay,
                            payed,
                            orderRemark,
                            hasAddress,
                            consignee,
                            mobile,
                            address);
    }

    //打印在中间
    public static String getPrintCenter(String title) {
        String ret = "";

        int shopNameLength = getWordCount(title);
        int l = length - shopNameLength;
        if(l > 0) {
            int l_left = l / 2;
            int l_right = l - l_left;

            ret = getPrintChar(" ", l_left) + title + getPrintChar(" ", l_right);
        }

//        if(ret.length() > 0) {
//            ret += "\n";
//        }

        return ret;
    }

    //打印在左边
    public static String getPrintLeft(String title, int l) {
        String ret = title;

        int shopNameLength = getWordCount(title);
        l -= shopNameLength;
        if(l > 0) {
            for(int i = 0; i < l; i++) {
                ret += " ";
            }
        }

        return ret;
    }

    public static String getPrintChar(String c, int length) {
        String ret = "";

        for(int i = 0; i < length; i++) {
            ret += c;
        }

        return ret;
    }


    //打印在右边
    public static String getPrintRight(String title, int l) {
        return getPrintRight(title, " ", l);
    }

    public static String getPrintRight(String title, String ch, int l) {
        String ret = "";

        int shopNameLength = getWordCount(title);
        l -= shopNameLength;
        if(l > 0) {
            for(int i = 0; i < l; i++) {
                ret += ch;
            }
        }
        ret += title;

        return ret;
    }
}
