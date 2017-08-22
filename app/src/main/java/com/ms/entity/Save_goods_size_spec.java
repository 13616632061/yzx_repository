package com.ms.entity;

/**
 * Created by Administrator on 2017/8/21.
 */

public class Save_goods_size_spec {
    String spec_value;
    String spec_id;
    String spec_value_id;
    String spec_name;
    String info;

    public Save_goods_size_spec(String spec_value, String spec_id, String spec_value_id, String spec_name, String info) {
        this.spec_value = spec_value;
        this.spec_id = spec_id;
        this.spec_value_id = spec_value_id;
        this.spec_name = spec_name;
        this.info = info;
    }

    public String getSpec_value() {
        return spec_value;
    }

    public void setSpec_value(String spec_value) {
        this.spec_value = spec_value;
    }

    public String getSpec_id() {
        return spec_id;
    }

    public void setSpec_id(String spec_id) {
        this.spec_id = spec_id;
    }

    public String getSpec_value_id() {
        return spec_value_id;
    }

    public void setSpec_value_id(String spec_value_id) {
        this.spec_value_id = spec_value_id;
    }

    public String getSpec_name() {
        return spec_name;
    }

    public void setSpec_name(String spec_name) {
        this.spec_name = spec_name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Save_goods_size_spec{" +
                "spec_value='" + spec_value + '\'' +
                ", spec_id='" + spec_id + '\'' +
                ", spec_value_id='" + spec_value_id + '\'' +
                ", spec_name='" + spec_name + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
