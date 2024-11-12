package com.yutak.hotzone.entry;

import java.io.Serializable;
import java.util.List;

public class YutakPushMessage implements Serializable {

    private String body;

    private byte category;



    private List<YutakEntry> entries;


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<YutakEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<YutakEntry> entries) {
        this.entries = entries;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(byte category) {
        this.category = category;
    }


}
