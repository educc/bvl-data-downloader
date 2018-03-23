package com.ecacho.perhis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class App {

    private static final Logger log = Logger.getLogger(App.class.getName());
    private static final int LENGTH_PARAMS = 3;
    private static final String FORMAT_DATE_IN = "yyyy-MM-dd";

    public static void main(String[] args) throws Exception {
        if(!isValidInputParams(args)){
            System.exit(-1);
        }
        log.info("Starting app");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE_IN);

        String file = args[0];
        LocalDate startDate = LocalDate.parse(args[1], formatter);
        LocalDate endDate = LocalDate.parse(args[2], formatter);

        Observable.from(getNemos(file))
            .map(nemo -> {
                log.info("createUrl: " + nemo);
                String url = BVLShareData.createUrl(startDate, endDate, nemo);
                return new NemoBVL(nemo, url);
            })
            .map(nemobvl -> {
                log.info("createDoc:" + nemobvl.getNemo());
                Document doc = null;
                try {
                    doc = Jsoup.connect(nemobvl.getUrl()).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                nemobvl.setDoc(doc);
                return nemobvl;
            })//.subscribeOn(Schedulers.newThread())
            .doOnError(error -> {
                error.printStackTrace();
            }).forEach(nemobvl -> {
                log.info("save: " + nemobvl.getNemo());
                try {
                    BVLShareData.saveHistoryFromDoc(
                            nemobvl.getNemo(),
                            nemobvl.getDoc()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }

    private static List<String> getNemos(String file) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while( (line = br.readLine()) != null ){
            line = line.trim();
            if (!line.isEmpty()) {
                result.add(line);
            }
        }
        return result;
    }

    private static boolean isValidInputParams(String[] args ){
        //validate params
        if( args.length != LENGTH_PARAMS){
            log.info("Faltan parametros");
            log.info("perhis file startDate endDate");
            log.info("file: the path for file contains all nemo, each nemo at line");
            log.info("startDate: " + FORMAT_DATE_IN);
            log.info("endDate: " + FORMAT_DATE_IN);
            return false;
        }

        //validate file
        String file = args[0];

        File f = new File(file);
        if( !(f.exists() && f.canRead()) ){
            log.info("The file does not exist: " + file);
            return false;
        }


        //validate dates

        String startDate = args[1].trim();
        String endDate = args[2].trim();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE_IN);

        try{
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);
        }catch (DateTimeParseException ex){
            log.info(ex.toString());
            log.info(String.format("Some dates are invalid: %s, %s",
                    startDate,
                    endDate));
            return false;
        }

        return true;
    }

}
