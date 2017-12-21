package com.ecacho.extnmo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BVLData {

    private static final String URL_FIND_BY_NEMO = "http://www.bvl.com.pe/jsp/Inf_Cotizaciones.jsp?Nemonico=";
    private static final String URL_BVL = "http://www.bvl.com.pe";

    public static String getRazonSocialFromNemonico(String nemo) throws Exception {
        String url = BVLData.findUrlFromNemonico(nemo);
        return findRazonSocial(url);
    }

    private static String findUrlFromNemonico(String nemo) throws Exception {
        String url = URL_FIND_BY_NEMO + nemo.trim();
        Document doc = Jsoup.connect(url).get();
        String str = doc.toString();

        Elements listEl = doc.select(".Tablas a");
        if(listEl.size() == 0){
            throw new Exception("Nenomico no encontrado");
        }

        return URL_BVL + listEl.get(0).attr("href");
    }

    private  static String findRazonSocial(String url) throws Exception {
        Document doc = Jsoup.connect(url).get();

        Elements listEl = doc.select("h2.txtRojo");
        if(listEl.size() == 0){
            throw new Exception("Nenomico no encontrado");
        }

        return listEl.get(0).text();
    }
}
