package com.ecacho.perhis;

import org.jsoup.nodes.Document;

public class NemoBVL {

    private String nemo;
    private String url;
    private Document doc;

    public NemoBVL(String nemo, String url) {
        this.nemo = nemo;
        this.url = url;
    }

    public String getNemo() {
        return nemo;
    }

    public void setNemo(String nemo) {
        this.nemo = nemo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }
}
