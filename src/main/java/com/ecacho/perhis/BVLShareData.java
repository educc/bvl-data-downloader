package com.ecacho.perhis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BVLShareData {

    private static final String URL_FIND_BY_NEMO = "http://www.bvl.com.pe/jsp/cotizacion.jsp?fec_inicio=%s&fec_fin=%s&nemonico=%s";

    private static final String URL_BVL = "http://www.bvl.com.pe";
    private static final String OUT_DIR = "./data/";

    public static final String FORMAT_DATE = "yyyyMMdd";


    public static String createUrl(LocalDate startDate, LocalDate endEnd, String nemo){
        if(startDate.isAfter(endEnd)){
            throw new IllegalArgumentException("The startDate can't be after the endEnd");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE);

        return String.format(
                URL_FIND_BY_NEMO,
                formatter.format(startDate),
                formatter.format(endEnd),
                nemo
        );
    }

    public static void saveHistoryFromDoc(String nemo, Document doc) throws Exception {
        Path dirNemo = Paths.get(OUT_DIR,nemo);
        Files.createDirectories(dirNemo);

        String absNewFile = dirNemo.resolve(nemo + "_1.txt").toString();

        FileWriter fw = new FileWriter(absNewFile);


        Elements listEl = doc.select("table tr");
        if(listEl.size() == 0){
            throw new Exception("Nenomico no encontrado");
        }

        for(Element trEl: listEl){
            StringBuilder sb = new StringBuilder();

            Elements tdList = trEl.select("td");
            if( tdList.size() >= 10){
                for(Element tdEl: tdList){
                    sb.append(tdEl.text().trim());
                    sb.append(";");
                }

            }
            sb.append("\n");

            fw.write(sb.toString());
        }

        fw.close();
    }

}
