package com.ecacho.perhis;

public class App {

    public static void main(String[] args) throws Exception {
        if( args.length == 0){
            System.out.println("Faltan parametros");
            System.out.println("perhis file");
            System.exit(-1);
        }
        //String testFile = "C:\\Users\\edu\\dev\\downloader\\data\\ALICORC1";
        String result = BVLShareData.shareHistoryFromFile(args[0]);
        System.out.println(result);
    }
}
