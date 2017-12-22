package com.ecacho.perhis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BVLShareData {

    private static final String URL_FIND_BY_NEMO = "http://www.bvl.com.pe/jsp/cotizacion.jsp?fec_inicio=19960101&fec_fin=20171201&nemonico=";
    private static final String URL_BVL = "http://www.bvl.com.pe";


    public static String shareHistory(String nemo) throws Exception {
        String url = URL_FIND_BY_NEMO + nemo.trim();
        Document doc = Jsoup.connect(url).get();

        Elements listEl = doc.select("table tr");
        if(listEl.size() == 0){
            throw new Exception("Nenomico no encontrado");
        }

        for(Element trEl: listEl){
            if( trEl.childNodeSize() >= 10){

            }
        }

        return URL_BVL + listEl.get(0).attr("href");
    }

    public static String shareHistoryFromFile(String strfilepath) throws Exception {
        StringBuilder sb = new StringBuilder();

        File myfile = new File(strfilepath);
        String nemonico = myfile.getName();
        String html = new String(Files.readAllBytes(Paths.get(strfilepath)));
        Document doc = Jsoup.parse(html);

        Elements listEl = doc.select("table tr");
        if(listEl.size() == 0){
            throw new Exception("Nenomico no encontrado");
        }

        for(Element trEl: listEl){
            Elements tdList = trEl.select("td");
            if( tdList.size() >= 10){
                for(Element tdEl: tdList){
                    sb.append(tdEl.text().trim());
                    sb.append(";");
                }

            }
            //sb.delete(sb.length()-2, sb.length()-
            sb.append("\n");
        }

        return sb.toString();
    }

}
