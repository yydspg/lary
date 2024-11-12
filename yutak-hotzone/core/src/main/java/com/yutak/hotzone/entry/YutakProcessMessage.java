package com.yutak.hotzone.entry;

import java.io.Serializable;

public class YutakProcessMessage  implements Serializable {

    private int category;

    private String K;

    public String getK() {
        return K;
    }

    public void setK(String k) {
        K = k;
    }

    public String getV() {
        return V;
    }

    public void setV(String v) {
        V = v;
    }

    private String V;


    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
