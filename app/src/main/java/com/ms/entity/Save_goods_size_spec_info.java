package com.ms.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/21.
 */

public class Save_goods_size_spec_info {
    String store;
    String info;
    String price;
    boolean is_default;
    Map<String,String> spec_info_arry_map;

    public Save_goods_size_spec_info(String store, String info, String price, boolean is_default, Map<String, String> spec_info_arry_map) {
        this.store = store;
        this.info = info;
        this.price = price;
        this.is_default = is_default;
        this.spec_info_arry_map = spec_info_arry_map;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean is_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public Map<String, String> getSpec_info_arry_map() {
        return spec_info_arry_map;
    }

    public void setSpec_info_arry_map(Map<String, String> spec_info_arry_map) {
        this.spec_info_arry_map = spec_info_arry_map;
    }

    @Override
    public String toString() {
        return "Save_goods_size_spec_info{" +
                "store='" + store + '\'' +
                ", info='" + info + '\'' +
                ", price='" + price + '\'' +
                ", is_default=" + is_default +
                ", spec_info_arry_map=" + spec_info_arry_map +
                '}';
    }
}
