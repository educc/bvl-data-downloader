package com.ecacho.perhis;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import static io.reactivex.Observable.create;

public class App {

    private static final Logger log = Logger.getLogger(App.class.getName());
    private static final int LENGTH_PARAMS = 3;
    private static final String FORMAT_DATE_IN = "yyyy-MM-dd";

    public static void main(String[] args) throws Exception {
      args = new String[]{"nemos.txt", "2018-03-01",  "2018-03-31"};
      if(!isValidInputParams(args)){
          System.exit(-1);
      }
      log.info("Starting app");

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE_IN);

      String file = args[0];
      LocalDate startDate = LocalDate.parse(args[1], formatter);
      LocalDate endDate = LocalDate.parse(args[2], formatter);



      Observable.fromArray(getNemos(file))
              .flatMap(Observable::fromIterable)
              .map(nemo -> {
                log.info("createUrl: " + nemo);
                String url = BVLShareData.createUrl(startDate, endDate, nemo);
                return new NemoBVL(nemo, url);
              })
              .flatMap(nemobvl -> {
                return Observable.create(emitter -> {
                  log.info("createDoc:" + nemobvl.getNemo());
                  Document doc = null;
                  try {
                    doc = Jsoup.connect(nemobvl.getUrl()).get();
                    nemobvl.setDoc(doc);
                    emitter.onNext((NemoBVL) nemobvl);
                  } catch (IOException e) {
                    e.printStackTrace();
                  } finally {
                    emitter.onComplete();
                  }
                }).subscribeOn(Schedulers.io());
              })
              .blockingSubscribe(it -> {
                NemoBVL nemobvl = (NemoBVL) it;
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
