package com.ms.util;

import com.ms.entity.OrderGoods;

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
    //餐厅名称、桌号、订单日期、餐段、单号、服务员、菜单列表、折扣代码、折扣金额
    public static String getPrinterMsg(String shopName, String shopDesk, String orderDate,
                                       String orderType, String orderNo, String orderServer,
                                       ArrayList<OrderGoods> foodList,
                                       String discountNo, String discountMoney) {
        String ret = "";

        //商铺名称
        shopName = getPrintCenter(shopName);

        ret += shopName;
        ret += getPrintCenter("客人就餐消费单");
        ret += "\n\n";
        ret += getPrintLeft("桌号：" + shopDesk, length);
        ret += getPrintChar("-", length);
        ret += "酒店日期：" + orderDate;
        int ll = (length - 30) / 2;
        ret += getPrintChar(" ", ll);

        ret += "餐段：";
        ret += orderType;
        ret += getPrintChar(" ", ll);

//        int left_length = 20 + ll;

        ret += getPrintLeft("单号：" + orderNo, length);
        ret += getPrintLeft("服务员：" + orderServer, length);

        String ss = "数量  规格    小计";   //18
        int ss_l = length - getWordCount(ss);
        ret += "菜品名称";
        ret += getPrintChar(" ", ss_l - 8);
        ret += ss;
        ret += getPrintChar("-", length);

        //打印菜品
        double all_price = 0.00;
        for(int i = 0; i < foodList.size(); i++) {
            OrderGoods bean = foodList.get(i);

            //菜品名称
            ret += bean.getName();
            int l1 = ss_l - getWordCount(bean.getName());
            for(int j = 0; j < l1; j++) {
                ret += " ";
            }

            int nn = bean.getQuantity();
            double pp = bean.getPrice();
            double su = nn * pp;
            //数量
            ret += getPrintRight(String.valueOf(bean.getQuantity()), 4);

            //规格
            ret += getPrintRight("", 6);
            //小计
            ret += getPrintRight(String.valueOf(su), 8);

            all_price += su;
        }
        ret += getPrintChar("-", length);

        //打印合计
        int right_l = 20;   //合计右栏的宽度
        int l2 = length - right_l;

        //折扣代码
        ret += getPrintLeft(" ", l2);
        ret += "折扣代码：";
        ret += getPrintRight(discountNo, right_l - 10);

        //消费合计
        ret += getPrintLeft(" ", l2);
        ret += "消费合计：";
        ret += getPrintRight(String.valueOf(all_price), right_l - 10);

        //折扣金额
        ret += getPrintLeft(" ", l2);
        ret += "折扣金额：";
        ret += getPrintRight(discountMoney, right_l - 10);

        //应收现金
        double dm = Double.parseDouble(discountMoney);
        dm = all_price - dm;
        ret += getPrintLeft(" ", l2);
        ret += "应收现金：";
        ret += getPrintRight(String.valueOf(dm), right_l - 10);

        //打印尾部
        ret += "\n\n";
        String timeString = "打印时间：" + SysUtils.getCurrentDate();
        ret += getPrintLeft(timeString, length);
        String copyString = "欧虎餐饮系统http://www.ohocn.com";
        ret += getPrintLeft(copyString, length);
        ret += "\n\n";

        return ret;
    }

    public static String getTestMsg() {
        String shopName ="xx餐厅";
        String shopDesk = "A1";
        String orderDate = "2015-02-13";
        String orderType = "晚餐";
        String orderNo = "0026001";
        String orderServer = "xx";
        String discountNo = "";
        String discountMoney = "0.00";

        ArrayList<OrderGoods> foodList = new ArrayList<OrderGoods>();

        OrderGoods a = new OrderGoods();
        a.setName("可口可乐");
        a.setQuantity(4);
        a.setPrice(10);

        foodList.add(a);

        a = new OrderGoods();
        a.setName("鲟龙鱼");
        a.setQuantity(1);
        a.setPrice(132);
        foodList.add(a);

        a = new OrderGoods();
        a.setName("蛏子");
        a.setQuantity(1);
        a.setPrice(28);
        foodList.add(a);


        a = new OrderGoods();
        a.setName("多春鱼");
        a.setQuantity(1);
        a.setPrice(28);
        foodList.add(a);

        return getPrinterMsg(shopName, shopDesk, orderDate, orderType, orderNo, orderServer, foodList, discountNo, discountMoney);
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
