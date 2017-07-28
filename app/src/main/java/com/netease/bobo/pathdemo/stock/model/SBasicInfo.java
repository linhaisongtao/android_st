package com.netease.bobo.pathdemo.stock.model;

import java.io.Serializable;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class SBasicInfo implements Serializable{
    public String code;
    public String name;

    public SBasicInfo() {
    }

    public SBasicInfo(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return "SBasicInfo{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
